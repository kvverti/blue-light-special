package io.github.kvverti.bluelightspecial.block;

import net.minecraft.util.DyeColor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.Blocks;
import net.minecraft.block.BlockState;
import net.minecraft.client.block.ColoredBlock;
import net.minecraft.entity.EntityContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

/**
 * A block that emits light corresponding to its redstone power level.
 */
public class CagedBulbBlock extends Block implements ColoredBlock {

    public static final Property<Direction> ATTACH = FluorescentLightBlock.ATTACH;
    public static final Property<Integer> POWER = FluorescentLightBlock.POWER;

    private final DyeColor color;

    public CagedBulbBlock(DyeColor color, Block.Settings settings) {
        super(settings);
        this.color = color;
        this.setDefaultState(this.stateFactory.getDefaultState()
            .with(ATTACH, Direction.DOWN)
            .with(POWER, 0));
    }

    @Override
    protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
        builder.add(ATTACH, POWER);
    }

    @Override
    public int getLuminance(BlockState state) {
        return state.get(POWER);
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @Override
    public DyeColor getColor() {
        return color;
    }

    private static final VoxelShape[] BOUNDING_BOXES = {
        /* DOWN  */ Block.createCuboidShape(5.0, 0.0, 5.0, 11.0, 10.0, 11.0),
        /* UP    */ Block.createCuboidShape(5.0, 6.0, 5.0, 11.0, 16.0, 11.0),
        /* NORTH */ Block.createCuboidShape(5.0, 5.0, 0.0, 11.0, 11.0, 10.0),
        /* SOUTH */ Block.createCuboidShape(5.0, 5.0, 6.0, 11.0, 11.0, 16.0),
        /* WEST  */ Block.createCuboidShape(0.0, 5.0, 5.0, 10.0, 11.0, 11.0),
        /* EAST  */ Block.createCuboidShape(6.0, 5.0, 5.0, 16.0, 11.0, 11.0)
    };

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext ctx) {
        return BOUNDING_BOXES[state.get(ATTACH).getId()];
    }

    @Override
    public boolean canPlaceAt(BlockState self, ViewableWorld world, BlockPos pos) {
        Direction dir = self.get(ATTACH);
        BlockPos offset = pos.offset(self.get(ATTACH));
        return Block.isSolidFullSquare(world.getBlockState(offset), world, offset, dir.getOpposite());
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction attach = ctx.getFacing().getOpposite();
        int power = ctx.getWorld().getEmittedRedstonePower(ctx.getBlockPos().offset(attach), attach.getOpposite());
        return this.getDefaultState().with(ATTACH, attach).with(POWER, power);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction dir, BlockState neighbor, IWorld world, BlockPos pos, BlockPos neighborPos) {
        if(!canPlaceAt(state, world, pos)) {
            return Blocks.AIR.getDefaultState();
        }
        return state;
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block neighbor, BlockPos neighborPos, boolean idk) {
        Direction attach = state.get(ATTACH);
        int power = world.getEmittedRedstonePower(pos.offset(attach), attach.getOpposite());
        world.setBlockState(pos, state.with(POWER, power));
    }
}
