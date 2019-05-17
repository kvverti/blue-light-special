package io.github.kvverti.bluelightspecial.api;

import java.util.Collection;

import net.minecraft.block.BlockState;

/**
 * Applied to blocks whose states are able to exist inside a multiblock.
 */
public interface MultiBlockComponent {

    /**
     * Returns additional state updates depending on the other block states in
     * the multi block.
     */
    // BlockState getAdditionalState(BlockState state, Collection<? extends BlockState> others);
}
