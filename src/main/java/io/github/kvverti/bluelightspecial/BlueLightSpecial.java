package io.github.kvverti.bluelightspecial;

import io.github.kvverti.bluelightspecial.block.FluorescentLightBlock;

import net.fabricmc.api.ModInitializer;

import net.minecraft.block.Block;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BlueLightSpecial implements ModInitializer {

    public static final String MODID = "bluelightspecial";

    // custom blocks

    public static final Block FLUORESCENT_LIGHT = new FluorescentLightBlock();

    @Override
    public void onInitialize() {
        Registry.register(Registry.BLOCK, new Identifier(MODID, "fluoresent_light"), FLUORESCENT_LIGHT);
    }
}
