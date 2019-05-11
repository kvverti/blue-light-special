package io.github.kvverti.bluelightspecial;

import io.github.kvverti.bluelightspecial.block.FluorescentLightBlock;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.block.FabricBlockSettings;

import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BlueLightSpecial implements ModInitializer {

    public static final String MODID = "bluelightspecial";

    // custom blocks

    public static final Block WHITE_FLUORESCENT_LIGHT;

    // custom items

    public static final Item WHITE_FLUORESCENT_LIGHT_ITEM;

    @Override
    public void onInitialize() {
        // blocks
        Registry.register(Registry.BLOCK, new Identifier(MODID, "white_fluorescent_light"), WHITE_FLUORESCENT_LIGHT);

        // items
        Registry.register(Registry.ITEM, new Identifier(MODID, "white_fluorescent_light"), WHITE_FLUORESCENT_LIGHT_ITEM);
    }

    static {
        Block.Settings lightSettings = FabricBlockSettings.of(Material.GLASS)
            .sounds(BlockSoundGroup.GLASS)
            .noCollision()
            .lightLevel(15)
            .build();
        WHITE_FLUORESCENT_LIGHT = new FluorescentLightBlock(lightSettings);

        Item.Settings lightItemSettings = new Item.Settings()
            .itemGroup(ItemGroup.REDSTONE);
        WHITE_FLUORESCENT_LIGHT_ITEM = new BlockItem(WHITE_FLUORESCENT_LIGHT, lightItemSettings);
    }
}
