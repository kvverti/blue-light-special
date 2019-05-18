package io.github.kvverti.bluelightspecial.api;

import io.github.kvverti.bluelightspecial.BlueLightSpecial;
import io.github.kvverti.bluelightspecial.block.MultiBlock;
import io.github.kvverti.bluelightspecial.block.entity.MultiBlockEntity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;

/**
 * Applied to blocks whose states are able to exist inside a multiblock.
 */
public interface MultiBlockComponent {

    /**
     * Returns the face the given state should be attached to in the multiblock.
     */
    Direction getFace(BlockState state);

    /**
     * Prepares the given block state to be added to the multiblock, and schedules
     * a state update in the given number of ticks. Returns the (outer) block state
     * that is actually placed into the world.
     */
    default BlockState multiBlockState(BlockState state, IWorld world, BlockPos pos, int ticks) {
        Direction face = getFace(state);
        if(world.getBlockState(pos).getBlock() instanceof MultiBlockComponent) {
            // set the block in the world to multiblock, store the component
            // into it, then call the placing method for the multiblock
            BlockState component = world.getBlockState(pos);
            MultiBlockComponent block = (MultiBlockComponent)component.getBlock();
            if(block.getFace(component) == face) {
                // cannot replace an existing face
                return component;
            }
            world.setBlockState(pos, BlueLightSpecial.MULTIBLOCK.getDefaultState(), 0);
            MultiBlockEntity be = (MultiBlockEntity)world.getBlockEntity(pos);
            be.addBlockState(block.getFace(component), component);
        }
        if(world.getBlockEntity(pos) instanceof MultiBlockEntity) {
            MultiBlockEntity be = (MultiBlockEntity)world.getBlockEntity(pos);
            boolean added = be.addBlockState(face, state);
            if(added) {
                if(!world.isClient()) {
                    be.scheduleTick(face, ticks);
                }
                return MultiBlock.toggle(world.getBlockState(pos))
                    .with(MultiBlock.LIGHT, be.getLuminance());
            } else {
                return world.getBlockState(pos);
            }
        } else {
            if(!world.isClient()) {
                world.getBlockTickScheduler().schedule(pos, (Block)this, ticks);
            }
            return state;
        }
    }

    /**
     * Prepares the given block state to be added to the multiblock.
     * Returns the (outer) block state that is actually placed into the world.
     */
    default BlockState multiBlockState(BlockState state, IWorld world, BlockPos pos) {
        Direction face = getFace(state);
        if(world.getBlockState(pos).getBlock() instanceof MultiBlockComponent) {
            // set the block in the world to multiblock, store the component
            // into it, then call the placing method for the multiblock
            BlockState component = world.getBlockState(pos);
            MultiBlockComponent block = (MultiBlockComponent)component.getBlock();
            if(block.getFace(component) == face) {
                // cannot replace an existing face
                return component;
            }
            world.setBlockState(pos, BlueLightSpecial.MULTIBLOCK.getDefaultState(), 0);
            MultiBlockEntity be = (MultiBlockEntity)world.getBlockEntity(pos);
            be.addBlockState(block.getFace(component), component);
        }
        if(world.getBlockEntity(pos) instanceof MultiBlockEntity) {
            MultiBlockEntity be = (MultiBlockEntity)world.getBlockEntity(pos);
            boolean added = be.addBlockState(face, state);
            if(added) {
                return MultiBlock.toggle(world.getBlockState(pos))
                    .with(MultiBlock.LIGHT, be.getLuminance());
            } else {
                return world.getBlockState(pos);
            }
        } else {
            return state;
        }
    }
}
