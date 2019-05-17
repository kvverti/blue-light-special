package io.github.kvverti.bluelightspecial.block;

import io.github.kvverti.bluelightspecial.api.FluorescentPowerSource;
import io.github.kvverti.bluelightspecial.api.MultiBlockComponent;
import io.github.kvverti.bluelightspecial.api.RelativeDirection;
import io.github.kvverti.bluelightspecial.block.entity.MultiBlockEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.Blocks;
import net.minecraft.block.BlockState;
import net.minecraft.client.block.ColoredBlock;
import net.minecraft.entity.EntityContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntegerProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public class FluorescentLightBlock extends Block implements FluorescentPowerSource, MultiBlockComponent, ColoredBlock {

    public static final Property<Integer> POWER = IntegerProperty.create("power", 0, 15);
    public static final Property<Direction> ATTACH = DirectionProperty.create("attach", d -> true);
    public static final Property<Boolean> FORE = BooleanProperty.create("fore");
    public static final Property<Boolean> BACK = BooleanProperty.create("back");
    public static final Property<Boolean> LEFT = BooleanProperty.create("left");
    public static final Property<Boolean> RIGHT = BooleanProperty.create("right");

    private static final Map<BlockState, VoxelShape> boundingBoxes = new HashMap<>();

    private final DyeColor color;

    public FluorescentLightBlock(DyeColor color, Block.Settings settings) {
        super(settings);
        this.color = color;
        this.setDefaultState(this.stateFactory.getDefaultState()
            .with(POWER, 0)
            .with(ATTACH, Direction.DOWN)
            .with(FORE, false)
            .with(BACK, false)
            .with(LEFT, false)
            .with(RIGHT, false));
    }

    @Override
    protected void appendProperties(StateFactory.Builder<Block, BlockState> factory) {
        factory.add(POWER, ATTACH, FORE, BACK, LEFT, RIGHT);
    }

    @Override
    public DyeColor getColor() {
        return color;
    }

    @Override
    public int getLuminance(BlockState state) {
        return state.get(POWER) != 0 ? super.getLuminance(state) : 0;
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.TRANSLUCENT;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, EntityContext ctx) {
        return boundingBoxes.computeIfAbsent(state, FluorescentLightBlock::computeVoxelShape);
    }

    private static VoxelShape[][] BBS_TABLE;

    /**
     * Creates a bounding box for the given state.
     */
    private static VoxelShape computeVoxelShape(BlockState state) {
        VoxelShape[] bbs = BBS_TABLE[state.get(ATTACH).getId()];
        VoxelShape bb = bbs[0];
        if(state.get(BACK)) {
            bb = VoxelShapes.union(bb, bbs[1]);
        }
        if(state.get(FORE)) {
            bb = VoxelShapes.union(bb, bbs[2]);
        }
        if(state.get(LEFT)) {
            bb = VoxelShapes.union(bb, bbs[3]);
        }
        if(state.get(RIGHT)) {
            bb = VoxelShapes.union(bb, bbs[4]);
        }
        return bb;
    }

    @Override
    public boolean canPlaceAt(BlockState self, ViewableWorld world, BlockPos pos) {
        Direction dir = self.get(ATTACH);
        BlockPos offset = pos.offset(self.get(ATTACH));
        return Block.isSolidFullSquare(world.getBlockState(offset), world, offset, dir.getOpposite());
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        World world = ctx.getWorld();
        BlockPos blockPos = ctx.getBlockPos();
        Direction attach =  ctx.getFacing().getOpposite();
        BlockState state = getDefaultState().with(ATTACH, attach);
        // determine initial connections
        for(Direction dir : SIDE_DIRECTIONS[attach.getId() / 2]) {
            BlockPos pos = blockPos.offset(dir);
            RelativeDirection rel = RelativeDirection.getRelativeDirection(attach, dir);
            if(world.getBlockState(pos).getBlock() instanceof FluorescentPowerSource) {
                BlockState neighbor = world.getBlockState(pos);
                FluorescentPowerSource src = (FluorescentPowerSource)neighbor.getBlock();
                boolean connect = src.canConnect(neighbor, world, pos, attach, rel);
                state = state.with(getConnectionProperty(rel), connect);
            } else {
                state = state.with(getConnectionProperty(rel), false);
            }
        }
        if(world.getBlockEntity(blockPos) instanceof MultiBlockEntity) {
            MultiBlockEntity be = (MultiBlockEntity)world.getBlockEntity(blockPos);
            boolean added = be.addBlockState(ctx.getFacing(), state);
            if(added) {
                if(!world.isClient()) {
                    be.scheduleTick(ctx.getFacing(), 1);
                }
                return MultiBlock.toggle(world.getBlockState(blockPos));
            } else {
                return world.getBlockState(blockPos);
            }
        } else {
            if(!world.isClient()) {
                // properly set power value once connections are set up
                world.getBlockTickScheduler().schedule(blockPos, this, 1);
            }
            return state;
        }
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState self, Direction dir, BlockState neighbor, IWorld world, BlockPos selfPos, BlockPos neighborPos) {
        // detach if mount is removed
        if(!canPlaceAt(self, world, selfPos)) {
            return Blocks.AIR.getDefaultState();
        }
        Direction attach = self.get(ATTACH);
        if(attach != dir && attach != dir.getOpposite()) {
            RelativeDirection rel = RelativeDirection.getRelativeDirection(attach, dir);
            if(neighbor.getBlock() instanceof FluorescentPowerSource) {
                FluorescentPowerSource src = (FluorescentPowerSource)neighbor.getBlock();
                boolean connected = src.canConnect(neighbor, world, neighborPos, attach, rel.opposite());
                self = self.with(getConnectionProperty(rel), connected);
            } else {
                self = self.with(getConnectionProperty(rel), false);
            }
        }
        return self;
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block neighbor, BlockPos neighborPos, boolean idk) {
        // update light power level immediately, without tick delay
        if(!world.isClient()) {
            int newPower = getPowerFromSurroundings(state, world, pos);
            world.setBlockState(pos, state.with(POWER, newPower));
        }
    }

    @Override
    public void onScheduledTick(BlockState state, World world, BlockPos pos, Random rand) {
        // update light power level
        if(!world.isClient()) {
            int newPower = getPowerFromSurroundings(state, world, pos);
            world.setBlockState(pos, state.with(POWER, newPower));
        }
    }

    @Override
    public int getPowerLevel(BlockState state, ViewableWorld world, BlockPos pos, Direction offset, RelativeDirection side) {
        if(state.get(ATTACH) != offset) {
            return 0;
        }
        Property<Boolean> relativeSide = getConnectionProperty(side);
        if(state.get(relativeSide)) {
            return state.get(POWER);
        } else {
            return 0;
        }
    }

    @Override
    public boolean canConnect(BlockState state, ViewableWorld world, BlockPos pos, Direction attach, RelativeDirection side) {
        Direction base = state.get(ATTACH);
        return base == attach;
    }

    private static final Property<?>[] PROPERTIES = { BACK, FORE, LEFT, RIGHT };

    /**
     * Converts the given direction into a relative direction property.
     */
    private Property<Boolean> getConnectionProperty(RelativeDirection dir) {
        @SuppressWarnings("unchecked")
        Property<Boolean> ret = (Property<Boolean>)PROPERTIES[dir.id()];
        return ret;
    }

    private static final Direction[][] SIDE_DIRECTIONS = {
        /* DOWN/UP */ { Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST },
        /* NORTH/SOUTH */ { Direction.DOWN, Direction.UP, Direction.WEST, Direction.EAST },
        /* WEST/EAST */ { Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH }
    };

    /**
     * Gets the max power from the state's valid surrounding blocks.
     */
    private int getPowerFromSurroundings(BlockState state, World world, BlockPos pos) {
        Direction attach = state.get(ATTACH);
        // get weak redstone power from the blocks we are attached to
        int power = world.getEmittedRedstonePower(pos.offset(attach), attach);
        // get light power and weak redstone power from the sides
        for(Direction dir : SIDE_DIRECTIONS[attach.getId() / 2]) {
            BlockPos offset = pos.offset(dir);
            BlockState offsetState = world.getBlockState(offset);
            power = Math.max(power, world.getEmittedRedstonePower(offset, dir));
            if(offsetState.getBlock() instanceof FluorescentPowerSource) {
                RelativeDirection rel = RelativeDirection.getRelativeDirection(attach, dir);
                power = Math.max(power, ((FluorescentPowerSource)offsetState.getBlock())
                    .getPowerLevel(offsetState, world, offset, attach, rel.opposite()) - 1);
            }
        }
        return power;
    }

    static {
        // stores an array keyed by the direction enum. The indices of
        // the sub-arrays correspond to: none, back, fore, left, right
        VoxelShape[][] bbsTable = {
            /* DOWN */ {
                Block.createCuboidShape(7.0, 0.0, 7.0, 9.0, 2.0, 9.0),
                Block.createCuboidShape(7.0, 0.0, 7.0, 9.0, 2.0, 16.0),
                Block.createCuboidShape(7.0, 0.0, 0.0, 9.0, 2.0, 9.0),
                Block.createCuboidShape(0.0, 0.0, 7.0, 9.0, 2.0, 9.0),
                Block.createCuboidShape(7.0, 0.0, 7.0, 16.0, 2.0, 9.0)
            },
            /* UP */ {
                Block.createCuboidShape(7.0, 14.0, 7.0, 9.0, 16.0, 9.0),
                Block.createCuboidShape(7.0, 14.0, 0.0, 9.0, 16.0, 9.0),
                Block.createCuboidShape(7.0, 14.0, 7.0, 9.0, 16.0, 16.0),
                Block.createCuboidShape(0.0, 14.0, 7.0, 9.0, 16.0, 9.0),
                Block.createCuboidShape(7.0, 14.0, 7.0, 16.0, 16.0, 9.0)
            },
            /* NORTH */ {
                Block.createCuboidShape(7.0, 7.0, 0.0, 9.0, 9.0, 2.0),
                Block.createCuboidShape(7.0, 0.0, 0.0, 9.0, 9.0, 2.0),
                Block.createCuboidShape(7.0, 7.0, 0.0, 9.0, 16.0, 2.0),
                Block.createCuboidShape(0.0, 7.0, 0.0, 9.0, 9.0, 2.0),
                Block.createCuboidShape(7.0, 7.0, 0.0, 16.0, 9.0, 2.0)
            },
            /* SOUTH */ {
                Block.createCuboidShape(7.0, 7.0, 14.0, 9.0, 9.0, 16.0),
                Block.createCuboidShape(7.0, 0.0, 14.0, 9.0, 9.0, 16.0),
                Block.createCuboidShape(7.0, 7.0, 14.0, 9.0, 16.0, 16.0),
                Block.createCuboidShape(7.0, 7.0, 14.0, 16.0, 9.0, 16.0),
                Block.createCuboidShape(0.0, 7.0, 14.0, 9.0, 9.0, 16.0)
            },
            /* WEST */ {
                Block.createCuboidShape(0.0, 7.0, 7.0, 2.0, 9.0, 9.0),
                Block.createCuboidShape(0.0, 0.0, 7.0, 2.0, 9.0, 9.0),
                Block.createCuboidShape(0.0, 7.0, 7.0, 2.0, 16.0, 9.0),
                Block.createCuboidShape(0.0, 7.0, 7.0, 2.0, 9.0, 16.0),
                Block.createCuboidShape(0.0, 7.0, 0.0, 2.0, 9.0, 9.0)
            },
            /* EAST */ {
                Block.createCuboidShape(14.0, 7.0, 7.0, 16.0, 9.0, 9.0),
                Block.createCuboidShape(14.0, 0.0, 7.0, 16.0, 9.0, 9.0),
                Block.createCuboidShape(14.0, 7.0, 7.0, 16.0, 16.0, 9.0),
                Block.createCuboidShape(14.0, 7.0, 0.0, 16.0, 9.0, 9.0),
                Block.createCuboidShape(14.0, 7.0, 7.0, 16.0, 9.0, 16.0)
            }
        };
        BBS_TABLE = bbsTable;
    }
}
