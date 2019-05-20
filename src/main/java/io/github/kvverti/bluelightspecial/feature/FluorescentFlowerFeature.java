package io.github.kvverti.bluelightspecial.feature;

import com.mojang.datafixers.Dynamic;

import io.github.kvverti.bluelightspecial.BlueLightSpecial;

import java.util.Random;
import java.util.function.Function;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.DefaultFlowerFeature;

public class FluorescentFlowerFeature extends DefaultFlowerFeature {

    public FluorescentFlowerFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> f) {
       super(f);
    }

    @Override
    public BlockState getFlowerToPlace(Random rand, BlockPos pos) {
        return BlueLightSpecial.GLOW_FLOWER.getDefaultState();
    }
}
