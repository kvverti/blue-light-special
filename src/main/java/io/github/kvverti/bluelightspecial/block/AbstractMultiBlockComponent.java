package io.github.kvverti.bluelightspecial.block;

import io.github.kvverti.bluelightspecial.BlueLightSpecial;
import io.github.kvverti.bluelightspecial.api.MultiBlockComponent;
import io.github.kvverti.bluelightspecial.block.entity.MultiBlockEntity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

/**
 * Abstract class that provides an initial implementation for multi block
 * components.
 */
public abstract class AbstractMultiBlockComponent extends Block implements MultiBlockComponent {

    protected AbstractMultiBlockComponent(Block.Settings settings) {
        super(settings);
    }

    /**
     *  Multi block components can be "replaced" by other components so that
     * a multi block entity can be set in its place.
     */
    @Override
    public boolean canReplace(BlockState state, ItemPlacementContext ctx) {
        return Block.getBlockFromItem(ctx.getItemStack().getItem()) instanceof MultiBlockComponent;
    }

    /**
     * Returns the block state to be placed into the world or into the
     * multiblock.
     */
    protected abstract BlockState computePlacementState(ItemPlacementContext ctx);

    /**
     * Prepares the block state to be added to the multiblock and schedules
     * a state update for the next tick. Returns the (outer) block state
     * that is actually placed into the world.
     */
    @Override
    public final BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState state = computePlacementState(ctx);
        Direction face = this.getFace(state);
        World world = ctx.getWorld();
        BlockPos pos = ctx.getBlockPos();
        if(world.getBlockState(pos).getBlock() instanceof MultiBlockComponent) {
            // set the block in the world to multiblock, store the component
            // into it, then call the placing method for the multiblock
            BlockState component = world.getBlockState(pos);
            MultiBlockComponent block = (MultiBlockComponent)component.getBlock();
            if(!state.canPlaceAt(world, pos) || block.getFace(component) == face) {
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
                    be.scheduleTick(face, 1);
                }
                return MultiBlock.toggle(world.getBlockState(pos))
                    .with(MultiBlock.LIGHT, be.getLuminance());
            }
        } else {
            if(!world.isClient()) {
                world.getBlockTickScheduler().schedule(pos, this, 1);
            }
        }
        return state;
    }
}
