package io.github.kvverti.bluelightspecial.block;

import io.github.kvverti.bluelightspecial.api.FluorescentPowerSource;
import io.github.kvverti.bluelightspecial.api.RelativeDirection;
import io.github.kvverti.bluelightspecial.block.entity.MultiBlockEntity;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.IntegerProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

/**
 * Allows multiple fluorescent lights to coexist in the same block space.
 */
public class MultiBlock extends Block implements BlockEntityProvider, FluorescentPowerSource {

    // changing this enables us to trigger a block update when the block
    // entity's state changes
    public static final Property<Integer> PARITY = IntegerProperty.create("parity", 0, 1);

    public MultiBlock(Block.Settings settings) {
        super(settings);
    }

    @Override
    protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
        builder.add(PARITY);
    }

    @Override
    public BlockEntity createBlockEntity(BlockView var1) {
        return new MultiBlockEntity();
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    /**
     * Forwards to block entity for state updates.
     */
    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction dir, BlockState neighbor, IWorld world, BlockPos pos, BlockPos neighborPos) {
        BlockEntity be = world.getBlockEntity(pos);
        if(be instanceof MultiBlockEntity) {
            boolean toggle = ((MultiBlockEntity)be).getStateForNeighborUpdate(dir, neighbor, neighborPos);
            int parity = state.get(PARITY) ^ (toggle ? 1 : 0);
            return state.with(PARITY, parity);
        }
        return state;
    }
    
    /**
     * Forwards to block entity for state updates.
     */
    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block neighbor, BlockPos neighborPos, boolean idk) {
        BlockEntity be = world.getBlockEntity(pos);
        if(be instanceof MultiBlockEntity) {
            boolean toggle = ((MultiBlockEntity)be).neighborUpdate(neighbor, neighborPos, idk);
            int parity = state.get(PARITY) ^ (toggle ? 1 : 0);
            world.setBlockState(pos, state.with(PARITY, parity));
        }
    }

    /**
     * Forwards to block entity if its states schedule ticks.
     */
    @Override
    public void onScheduledTick(BlockState state, World world, BlockPos pos, Random rand) {
        if(!world.isClient()) {
            BlockEntity be = world.getBlockEntity(pos);
            if(be instanceof MultiBlockEntity) {
                boolean toggle = ((MultiBlockEntity)be).onScheduledTick(rand);
                int parity = state.get(PARITY) ^ (toggle ? 1 : 0);
                world.setBlockState(pos, state.with(PARITY, parity));
            }
        }
    }

    @Override
    public int getPowerLevel(BlockState state, ViewableWorld world, BlockPos pos, Direction attach, RelativeDirection side) {
        BlockEntity be = world.getBlockEntity(pos);
        if(be instanceof MultiBlockEntity) {
            return ((MultiBlockEntity)be).getPowerLevel(attach, side);
        } else {
            return 0;
        }
    }

    @Override
    public boolean canConnect(BlockState state, ViewableWorld world, BlockPos pos, Direction attach, RelativeDirection side) {
        BlockEntity be = world.getBlockEntity(pos);
        if(be instanceof MultiBlockEntity) {
            return ((MultiBlockEntity)be).canConnect(attach, side);
        } else {
            return false;
        }
    }
}
