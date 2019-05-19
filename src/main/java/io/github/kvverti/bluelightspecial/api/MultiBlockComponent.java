package io.github.kvverti.bluelightspecial.api;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.Direction;

/**
 * Applied to blocks whose states are able to exist inside a multiblock.
 */
public interface MultiBlockComponent {

    /**
     * Returns the face the given state should be attached to in the multiblock.
     */
    Direction getFace(BlockState state);
}
