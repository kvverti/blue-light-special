package io.github.kvverti.bluelightspecial.block;

import io.github.kvverti.bluelightspecial.api.FluorescentPowerSource;
import io.github.kvverti.bluelightspecial.api.RelativeDirection;
import io.github.kvverti.bluelightspecial.block.entity.MultiBlockEntity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.ViewableWorld;

/**
 * Allows multiple fluorescent lights to coexist in the same block space.
 */
public class MultiBlock extends Block implements BlockEntityProvider, FluorescentPowerSource {

    public MultiBlock(Block.Settings settings) {
        super(settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockView var1) {
        return new MultiBlockEntity();
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public int getPowerLevel(BlockState state, ViewableWorld world, BlockPos pos, Direction attach, RelativeDirection side) {
        return 0;
    }

    @Override
    public boolean canConnect(BlockState state, ViewableWorld world, BlockPos pos, Direction attach, RelativeDirection side) {
        return false;
    }
}
