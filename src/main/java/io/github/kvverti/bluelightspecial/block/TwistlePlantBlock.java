package io.github.kvverti.bluelightspecial.block;

import io.github.kvverti.bluelightspecial.BlueLightSpecial;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidFillable;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.LightType;
import net.minecraft.world.ViewableWorld;

/**
 * An underwater plant similar to kelp that grows in deep water.
 */
public class TwistlePlantBlock extends Block implements FluidFillable {

    public TwistlePlantBlock(Block.Settings settings) {
        super(settings);
    }

    @Override
    public FluidState getFluidState(BlockState blockState_1) {
        return Fluids.WATER.getStill(false);
    }

    @Override
    public boolean canPlaceAt(BlockState state, ViewableWorld world, BlockPos pos) {
        if(!isSuitableFluidPos(world, pos)) {
            return false;
        }
        BlockPos ground = pos.down();
        BlockState groundState = world.getBlockState(ground);
        return groundState.getBlock() instanceof TwistlePlantBlock ||
            (groundState.getMaterial().isSolid() &&
            Block.isSolidFullSquare(groundState, world, ground, Direction.UP));
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction dir, BlockState neighbor, IWorld world, BlockPos pos, BlockPos neighborPos) {
        if(!canPlaceAt(state, world, pos)) {
            return Blocks.AIR.getDefaultState();
        }
        if(dir == Direction.UP && !(neighbor.getBlock() instanceof TwistlePlantBlock)) {
            // change this if I ever make multiple types of twistle
            return BlueLightSpecial.TWISTLE.getDefaultState();
        }
        return state;
    }

    /**
     * Returns whether the position is suitable fluid-wise.
     */
    private boolean isSuitableFluidPos(ViewableWorld world, BlockPos pos) {
        // must be placed in water and in low light
        return world.getFluidState(pos).getFluid() == Fluids.WATER &&
            world.getLightLevel(LightType.SKY, pos) <= 3;
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
