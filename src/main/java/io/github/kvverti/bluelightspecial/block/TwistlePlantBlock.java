package io.github.kvverti.bluelightspecial.block;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityContext;
import net.minecraft.util.Lazy;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;

/**
 * An underwater plant similar to kelp that grows in deep water.
 */
public class TwistlePlantBlock extends AbstractTwistleBlock {

    private static final VoxelShape shape = Block.createCuboidShape(3.0, 0.0, 3.0, 13.0, 16.0, 13.0);

    private final Lazy<Block> top;

    public TwistlePlantBlock(Block.Settings settings, Lazy<Block> top) {
        super(settings);
        this.top = top;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, EntityContext ctx) {
        return shape;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction dir, BlockState neighbor, IWorld world, BlockPos pos, BlockPos neighborPos) {
        if(!canPlaceAt(state, world, pos)) {
            return Blocks.AIR.getDefaultState();
        }
        Block nblock = neighbor.getBlock();
        if(dir == Direction.UP && nblock != this && nblock != top.get()) {
            return top.get().getDefaultState();
        }
        return state;
    }
}
