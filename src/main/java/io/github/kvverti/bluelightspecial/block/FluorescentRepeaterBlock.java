package io.github.kvverti.bluelightspecial.block;

import io.github.kvverti.bluelightspecial.api.FluorescentPowerSource;
import io.github.kvverti.bluelightspecial.api.RelativeDirection;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public class FluorescentRepeaterBlock extends AbstractMultiBlockComponent implements FluorescentPowerSource {

    public static final Property<Direction> ATTACH = FluorescentLightBlock.ATTACH;
    public static final Property<RelativeDirection> FACING = EnumProperty.create("facing", RelativeDirection.class);
    public static final Property<Boolean> POWERED = Properties.POWERED;

    // each element is of { BACK/FORE, LEFT/RIGHT }
    private static final VoxelShape[][] BBS_TABLE;

    public FluorescentRepeaterBlock(Block.Settings settings) {
        super(settings);
        this.setDefaultState(this.stateFactory.getDefaultState()
            .with(ATTACH, Direction.DOWN)
            .with(FACING, RelativeDirection.FORE)
            .with(POWERED, false));
    }

    @Override
    protected void appendProperties(StateFactory.Builder<Block, BlockState> factory) {
        factory.add(ATTACH, FACING, POWERED);
    }

    @Override
    public Direction getFace(BlockState state) {
        return state.get(ATTACH).getOpposite();
    }

    @Override
    public boolean canPlaceAt(BlockState self, ViewableWorld world, BlockPos pos) {
        Direction dir = self.get(ATTACH);
        BlockPos offset = pos.offset(dir);
        return Block.isSolidFullSquare(world.getBlockState(offset), world, offset, dir.getOpposite());
    }

    @Override
    protected BlockState computePlacementState(ItemPlacementContext ctx) {
        return getDefaultState()
            .with(ATTACH, ctx.getFacing().getOpposite())
            .with(FACING, getPlacementFacing(ctx));
    }

    private RelativeDirection getPlacementFacing(ItemPlacementContext ctx) {
        BlockPos pos = ctx.getBlockPos();
        Vec3d fpos = ctx.getPos();
        Direction facing = ctx.getFacing().getOpposite();
        // calculate which diagonal quadrant the player is looking at
        double du = 0.0, dv = 0.0;
        switch(facing) {
            case DOWN:
                du = fpos.getX() - pos.getX();
                dv = pos.getZ() - fpos.getZ() + 1;
                break;
            case UP:
                du = fpos.getX() - pos.getX();
                dv = fpos.getZ() - pos.getZ();
                break;
            case NORTH:
                du = fpos.getX() - pos.getX();
                dv = fpos.getY() - pos.getY();
                break;
            case SOUTH:
                du = pos.getX() - fpos.getX() + 1;
                dv = fpos.getY() - pos.getY();
                break;
            case WEST:
                du = pos.getZ() - fpos.getZ() + 1;
                dv = fpos.getY() - pos.getY();
                break;
            case EAST:
                du = fpos.getZ() - pos.getZ();
                dv = fpos.getY() - pos.getY();
                break;
        }
        du = Math.abs(du) - 0.5;
        dv = Math.abs(dv) - 0.5;
        double theta = Math.atan2(dv, du);
        // determine the facing state based on the diagonal quadrant
        // the block should face the direction opposite the quadrant
        final double FORTH_PI = Math.PI / 4;
        if(-FORTH_PI <= theta && theta < FORTH_PI) {
            return RelativeDirection.LEFT;
        } else if(FORTH_PI <= theta && theta < 3 * FORTH_PI) {
            return RelativeDirection.BACK;
        } else if(-3 * FORTH_PI <= theta && theta < -FORTH_PI) {
            return RelativeDirection.FORE;
        } else {
            return RelativeDirection.RIGHT;
        }
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, EntityContext ctx) {
        return BBS_TABLE[state.get(ATTACH).getId()][state.get(FACING).id() / 2];
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState self, Direction dir, BlockState neighbor, IWorld world, BlockPos selfPos, BlockPos neighborPos) {
        // detach if mount is removed
        if(!canPlaceAt(self, world, selfPos)) {
            return Blocks.AIR.getDefaultState();
        }
        if(!world.isClient()) {
            // two tick delay for power updating
            world.getBlockTickScheduler().schedule(selfPos, this, 2);
        }
        return self;
    }

    @Override
    public void onScheduledTick(BlockState state, World world, BlockPos pos, Random rand) {
        // update light power level
        if(!world.isClient()) {
            RelativeDirection facing = state.get(FACING);
            Direction attach = state.get(ATTACH);
            Direction absoluteFacing = RelativeDirection.getDirection(attach, facing);
            BlockPos offset = pos.offset(absoluteFacing.getOpposite());
            BlockState offsetState = world.getBlockState(offset);
            boolean powered = world.isEmittingRedstonePower(offset, absoluteFacing.getOpposite());
            if(offsetState.getBlock() instanceof FluorescentPowerSource) {
                FluorescentPowerSource src = (FluorescentPowerSource)offsetState.getBlock();
                powered |= src.getPowerLevel(offsetState, world, offset, attach, facing) > 0;
            }
            world.setBlockState(pos, state.with(POWERED, powered));
        }
    }

    @Override
    public int getPowerLevel(BlockState state, ViewableWorld world, BlockPos pos, Direction attach, RelativeDirection side) {
        if(state.get(POWERED) && state.get(ATTACH) == attach && state.get(FACING) == side) {
            return 15;
        } else {
            return 0;
        }
    }

    @Override
    public boolean canConnect(BlockState state, ViewableWorld world, BlockPos pos, Direction attach, RelativeDirection side) {
        RelativeDirection facing = state.get(FACING);
        return state.get(ATTACH) == attach && (facing == side || facing.opposite() == side);
    }

    static {
        // each element is of { BACK/FORE, LEFT/RIGHT }
        VoxelShape[][] bbsTable = {
            /* DOWN */ {
                Block.createCuboidShape(7.0, 0.0, 0.0, 9.0, 2.0, 16.0),
                Block.createCuboidShape(0.0, 0.0, 7.0, 16.0, 2.0, 9.0)
            },
            /* UP */ {
                Block.createCuboidShape(7.0, 14.0, 0.0, 9.0, 16.0, 16.0),
                Block.createCuboidShape(0.0, 14.0, 7.0, 16.0, 16.0, 9.0)
            },
            /* NORTH */ {
                Block.createCuboidShape(7.0, 0.0, 0.0, 9.0, 16.0, 2.0),
                Block.createCuboidShape(0.0, 7.0, 0.0, 16.0, 9.0, 2.0)
            },
            /* SOUTH */ {
                Block.createCuboidShape(7.0, 0.0, 14.0, 9.0, 16.0, 16.0),
                Block.createCuboidShape(0.0, 7.0, 14.0, 16.0, 9.0, 16.0)
            },
            /* WEST */ {
                Block.createCuboidShape(0.0, 0.0, 7.0, 2.0, 16.0, 9.0),
                Block.createCuboidShape(0.0, 7.0, 0.0, 2.0, 9.0, 16.0)
            },
            /* EAST */ {
                Block.createCuboidShape(14.0, 0.0, 7.0, 16.0, 16.0, 9.0),
                Block.createCuboidShape(14.0, 7.0, 0.0, 16.0, 9.0, 16.0)
            }
        };
        BBS_TABLE = bbsTable;
    }
}
