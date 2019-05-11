package io.github.kvverti.bluelightspecial.api;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ViewableWorld;

public interface FluorescentPowerSource {

    /**
     * Returns the power out of the given blockstate through and along the given
     * directions.
     * @param side the direction to test for power
     * @param attach the direction offset through which power is supplied
     */
    int getPowerLevel(BlockState state, ViewableWorld world, BlockPos pos, Direction side, Direction attach);
}
