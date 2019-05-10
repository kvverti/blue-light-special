package io.github.kvverti.bluelightspecial;

import io.github.kvverti.bluelightspecial.block.FluorescentLightBlock;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.block.FabricBlockSettings;

import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BlueLightSpecial implements ModInitializer {

    public static final String MODID = "bluelightspecial";

    // custom blocks

    public static final Block FLUORESCENT_LIGHT = new FluorescentLightBlock(
        FabricBlockSettings.of(Material.GLASS)
            .noCollision()
            .lightLevel(15)
            .build());

    // custom items
    public static final Item FLUORESCENT_LIGHT_ITEM = new BlockItem(
        FLUORESCENT_LIGHT,
        new Item.Settings()
            .itemGroup(ItemGroup.REDSTONE));

    @Override
    public void onInitialize() {
        // blocks
        Registry.register(Registry.BLOCK, new Identifier(MODID, "fluorescent_light"), FLUORESCENT_LIGHT);

        // items
        Registry.register(Registry.ITEM, new Identifier(MODID, "fluorescent_light"), FLUORESCENT_LIGHT_ITEM);
    }
}
