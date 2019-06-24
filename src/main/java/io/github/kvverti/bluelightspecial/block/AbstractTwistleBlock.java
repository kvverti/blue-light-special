package io.github.kvverti.bluelightspecial.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidFillable;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;

/**
 * An underwater plant similar to kelp that grows in deep water.
 */
public abstract class AbstractTwistleBlock extends Block implements FluidFillable {

    protected AbstractTwistleBlock(Block.Settings settings) {
        super(settings);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return Fluids.WATER.getStill(false);
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public boolean canPlaceAt(BlockState state, ViewableWorld world, BlockPos pos) {
        if(world.getFluidState(pos).getFluid() != getFluidState(state).getFluid()) {
            return false;
        }
        BlockPos ground = pos.down();
        BlockState groundState = world.getBlockState(ground);
        return groundState.getBlock() instanceof AbstractTwistleBlock ||
            (groundState.getMaterial().isSolid() &&
            Block.isSolidFullSquare(groundState, world, ground, Direction.UP));
    }

    @Override
    public boolean canFillWithFluid(BlockView world, BlockPos pos, BlockState state, Fluid fluid) {
        return false;
    }

    @Override
    public boolean tryFillWithFluid(IWorld world, BlockPos pos, BlockState state, FluidState fluidState) {
        return false;
    }
}
