package io.github.kvverti.bluelightspecial;

import io.github.kvverti.bluelightspecial.block.FluorescentLightBlock;
import io.github.kvverti.bluelightspecial.block.FluorescentRepeaterBlock;
import io.github.kvverti.bluelightspecial.block.MultiBlock;
import io.github.kvverti.bluelightspecial.block.entity.MultiBlockEntity;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.block.FabricBlockSettings;

import net.minecraft.block.Block;
import net.minecraft.block.FlowerBlock;
import net.minecraft.block.FlowerPotBlock;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BlueLightSpecial implements ModInitializer {

    public static final String MODID = "bluelightspecial";

    // custom blocks

    public static final Block WHITE_FLUORESCENT_LIGHT;
    public static final Block ORANGE_FLUORESCENT_LIGHT;
    public static final Block MAGENTA_FLUORESCENT_LIGHT;
    public static final Block LIGHT_BLUE_FLUORESCENT_LIGHT;
    public static final Block YELLOW_FLUORESCENT_LIGHT;
    public static final Block LIME_FLUORESCENT_LIGHT;
    public static final Block PINK_FLUORESCENT_LIGHT;
    public static final Block GRAY_FLUORESCENT_LIGHT;
    public static final Block LIGHT_GRAY_FLUORESCENT_LIGHT;
    public static final Block CYAN_FLUORESCENT_LIGHT;
    public static final Block PURPLE_FLUORESCENT_LIGHT;
    public static final Block BLUE_FLUORESCENT_LIGHT;
    public static final Block BROWN_FLUORESCENT_LIGHT;
    public static final Block GREEN_FLUORESCENT_LIGHT;
    public static final Block RED_FLUORESCENT_LIGHT;
    public static final Block BLACK_FLUORESCENT_LIGHT;
    public static final Block FLUORESCENT_TUBE;
    public static final Block FLUORESCENT_REPEATER;
    public static final Block MULTIBLOCK;
    public static final Block GLOW_FLOWER;
    public static final Block POTTED_GLOW_FLOWER;

    // custom items

    public static final Item WHITE_FLUORESCENT_LIGHT_ITEM;
    public static final Item ORANGE_FLUORESCENT_LIGHT_ITEM;
    public static final Item MAGENTA_FLUORESCENT_LIGHT_ITEM;
    public static final Item LIGHT_BLUE_FLUORESCENT_LIGHT_ITEM;
    public static final Item YELLOW_FLUORESCENT_LIGHT_ITEM;
    public static final Item LIME_FLUORESCENT_LIGHT_ITEM;
    public static final Item PINK_FLUORESCENT_LIGHT_ITEM;
    public static final Item GRAY_FLUORESCENT_LIGHT_ITEM;
    public static final Item LIGHT_GRAY_FLUORESCENT_LIGHT_ITEM;
    public static final Item CYAN_FLUORESCENT_LIGHT_ITEM;
    public static final Item PURPLE_FLUORESCENT_LIGHT_ITEM;
    public static final Item BLUE_FLUORESCENT_LIGHT_ITEM;
    public static final Item BROWN_FLUORESCENT_LIGHT_ITEM;
    public static final Item GREEN_FLUORESCENT_LIGHT_ITEM;
    public static final Item RED_FLUORESCENT_LIGHT_ITEM;
    public static final Item BLACK_FLUORESCENT_LIGHT_ITEM;
    public static final Item FLUORESCENT_TUBE_ITEM;
    public static final Item FLUORESCENT_REPEATER_ITEM;
    public static final Item GLOW_FLOWER_ITEM;

    // custom block entities
    public static final BlockEntityType<MultiBlockEntity> MULTI_BLOCK_ENTITY;

    @Override
    public void onInitialize() {
        // blocks
        Registry.register(Registry.BLOCK, new Identifier(MODID, "white_fluorescent_light"), WHITE_FLUORESCENT_LIGHT);
        Registry.register(Registry.BLOCK, new Identifier(MODID, "orange_fluorescent_light"), ORANGE_FLUORESCENT_LIGHT);
        Registry.register(Registry.BLOCK, new Identifier(MODID, "magenta_fluorescent_light"), MAGENTA_FLUORESCENT_LIGHT);
        Registry.register(Registry.BLOCK, new Identifier(MODID, "light_blue_fluorescent_light"), LIGHT_BLUE_FLUORESCENT_LIGHT);
        Registry.register(Registry.BLOCK, new Identifier(MODID, "yellow_fluorescent_light"), YELLOW_FLUORESCENT_LIGHT);
        Registry.register(Registry.BLOCK, new Identifier(MODID, "lime_fluorescent_light"), LIME_FLUORESCENT_LIGHT);
        Registry.register(Registry.BLOCK, new Identifier(MODID, "pink_fluorescent_light"), PINK_FLUORESCENT_LIGHT);
        Registry.register(Registry.BLOCK, new Identifier(MODID, "gray_fluorescent_light"), GRAY_FLUORESCENT_LIGHT);
        Registry.register(Registry.BLOCK, new Identifier(MODID, "light_gray_fluorescent_light"), LIGHT_GRAY_FLUORESCENT_LIGHT);
        Registry.register(Registry.BLOCK, new Identifier(MODID, "cyan_fluorescent_light"), CYAN_FLUORESCENT_LIGHT);
        Registry.register(Registry.BLOCK, new Identifier(MODID, "purple_fluorescent_light"), PURPLE_FLUORESCENT_LIGHT);
        Registry.register(Registry.BLOCK, new Identifier(MODID, "blue_fluorescent_light"), BLUE_FLUORESCENT_LIGHT);
        Registry.register(Registry.BLOCK, new Identifier(MODID, "brown_fluorescent_light"), BROWN_FLUORESCENT_LIGHT);
        Registry.register(Registry.BLOCK, new Identifier(MODID, "green_fluorescent_light"), GREEN_FLUORESCENT_LIGHT);
        Registry.register(Registry.BLOCK, new Identifier(MODID, "red_fluorescent_light"), RED_FLUORESCENT_LIGHT);
        Registry.register(Registry.BLOCK, new Identifier(MODID, "black_fluorescent_light"), BLACK_FLUORESCENT_LIGHT);
        Registry.register(Registry.BLOCK, new Identifier(MODID, "fluorescent_tube"), FLUORESCENT_TUBE);
        Registry.register(Registry.BLOCK, new Identifier(MODID, "fluorescent_repeater"), FLUORESCENT_REPEATER);
        Registry.register(Registry.BLOCK, new Identifier(MODID, "multiblock"), MULTIBLOCK);
        Registry.register(Registry.BLOCK, new Identifier(MODID, "glow_flower"), GLOW_FLOWER);
        Registry.register(Registry.BLOCK, new Identifier(MODID, "potted_glow_flower"), POTTED_GLOW_FLOWER);

        // items
        Registry.register(Registry.ITEM, new Identifier(MODID, "white_fluorescent_light"), WHITE_FLUORESCENT_LIGHT_ITEM);
        Registry.register(Registry.ITEM, new Identifier(MODID, "orange_fluorescent_light"), ORANGE_FLUORESCENT_LIGHT_ITEM);
        Registry.register(Registry.ITEM, new Identifier(MODID, "magenta_fluorescent_light"), MAGENTA_FLUORESCENT_LIGHT_ITEM);
        Registry.register(Registry.ITEM, new Identifier(MODID, "light_blue_fluorescent_light"), LIGHT_BLUE_FLUORESCENT_LIGHT_ITEM);
        Registry.register(Registry.ITEM, new Identifier(MODID, "yellow_fluorescent_light"), YELLOW_FLUORESCENT_LIGHT_ITEM);
        Registry.register(Registry.ITEM, new Identifier(MODID, "lime_fluorescent_light"), LIME_FLUORESCENT_LIGHT_ITEM);
        Registry.register(Registry.ITEM, new Identifier(MODID, "pink_fluorescent_light"), PINK_FLUORESCENT_LIGHT_ITEM);
        Registry.register(Registry.ITEM, new Identifier(MODID, "gray_fluorescent_light"), GRAY_FLUORESCENT_LIGHT_ITEM);
        Registry.register(Registry.ITEM, new Identifier(MODID, "light_gray_fluorescent_light"), LIGHT_GRAY_FLUORESCENT_LIGHT_ITEM);
        Registry.register(Registry.ITEM, new Identifier(MODID, "cyan_fluorescent_light"), CYAN_FLUORESCENT_LIGHT_ITEM);
        Registry.register(Registry.ITEM, new Identifier(MODID, "purple_fluorescent_light"), PURPLE_FLUORESCENT_LIGHT_ITEM);
        Registry.register(Registry.ITEM, new Identifier(MODID, "blue_fluorescent_light"), BLUE_FLUORESCENT_LIGHT_ITEM);
        Registry.register(Registry.ITEM, new Identifier(MODID, "brown_fluorescent_light"), BROWN_FLUORESCENT_LIGHT_ITEM);
        Registry.register(Registry.ITEM, new Identifier(MODID, "green_fluorescent_light"), GREEN_FLUORESCENT_LIGHT_ITEM);
        Registry.register(Registry.ITEM, new Identifier(MODID, "red_fluorescent_light"), RED_FLUORESCENT_LIGHT_ITEM);
        Registry.register(Registry.ITEM, new Identifier(MODID, "black_fluorescent_light"), BLACK_FLUORESCENT_LIGHT_ITEM);
        Registry.register(Registry.ITEM, new Identifier(MODID, "fluorescent_tube"), FLUORESCENT_TUBE_ITEM);
        Registry.register(Registry.ITEM, new Identifier(MODID, "fluorescent_repeater"), FLUORESCENT_REPEATER_ITEM);
        Registry.register(Registry.ITEM, new Identifier(MODID, "glow_flower"), GLOW_FLOWER_ITEM);

        // block entities
        Registry.register(Registry.BLOCK_ENTITY, new Identifier(MODID, "multiblock"), MULTI_BLOCK_ENTITY);
    }

    static {
        Block.Settings lightSettings = FabricBlockSettings.of(Material.GLASS)
            .sounds(BlockSoundGroup.GLASS)
            .noCollision()
            .lightLevel(15)
            .build();
        WHITE_FLUORESCENT_LIGHT = new FluorescentLightBlock(DyeColor.WHITE, lightSettings);
        ORANGE_FLUORESCENT_LIGHT = new FluorescentLightBlock(DyeColor.ORANGE, lightSettings);
        MAGENTA_FLUORESCENT_LIGHT = new FluorescentLightBlock(DyeColor.MAGENTA, lightSettings);
        LIGHT_BLUE_FLUORESCENT_LIGHT = new FluorescentLightBlock(DyeColor.LIGHT_BLUE, lightSettings);
        YELLOW_FLUORESCENT_LIGHT = new FluorescentLightBlock(DyeColor.YELLOW, lightSettings);
        LIME_FLUORESCENT_LIGHT = new FluorescentLightBlock(DyeColor.LIME, lightSettings);
        PINK_FLUORESCENT_LIGHT = new FluorescentLightBlock(DyeColor.PINK, lightSettings);
        GRAY_FLUORESCENT_LIGHT = new FluorescentLightBlock(DyeColor.GRAY, lightSettings);
        LIGHT_GRAY_FLUORESCENT_LIGHT = new FluorescentLightBlock(DyeColor.LIGHT_GRAY, lightSettings);
        CYAN_FLUORESCENT_LIGHT = new FluorescentLightBlock(DyeColor.CYAN, lightSettings);
        PURPLE_FLUORESCENT_LIGHT = new FluorescentLightBlock(DyeColor.PURPLE, lightSettings);
        BLUE_FLUORESCENT_LIGHT = new FluorescentLightBlock(DyeColor.BLUE, lightSettings);
        BROWN_FLUORESCENT_LIGHT = new FluorescentLightBlock(DyeColor.BROWN, lightSettings);
        GREEN_FLUORESCENT_LIGHT = new FluorescentLightBlock(DyeColor.GREEN, lightSettings);
        RED_FLUORESCENT_LIGHT = new FluorescentLightBlock(DyeColor.RED, lightSettings);
        BLACK_FLUORESCENT_LIGHT = new FluorescentLightBlock(DyeColor.BLACK, lightSettings);

        Block.Settings tubeSettings = FabricBlockSettings.of(Material.GLASS)
            .sounds(BlockSoundGroup.STONE)
            .noCollision()
            .build();
        FLUORESCENT_TUBE = new FluorescentLightBlock(DyeColor.BLACK, tubeSettings);
        FLUORESCENT_REPEATER = new FluorescentRepeaterBlock(tubeSettings);
        MULTIBLOCK = new MultiBlock(tubeSettings);

        Block.Settings plantSettings = FabricBlockSettings.of(Material.REPLACEABLE_PLANT)
            .sounds(BlockSoundGroup.GRASS)
            .noCollision()
            .lightLevel(5)
            .build();
        GLOW_FLOWER = new FlowerBlock(StatusEffects.NIGHT_VISION, 10, plantSettings);
        POTTED_GLOW_FLOWER = new FlowerPotBlock(
            GLOW_FLOWER,
            FabricBlockSettings.of(Material.PART)
                .lightLevel(5)
                .build());

        Item.Settings lightItemSettings = new Item.Settings()
            .itemGroup(ItemGroup.REDSTONE);
        WHITE_FLUORESCENT_LIGHT_ITEM = new BlockItem(WHITE_FLUORESCENT_LIGHT, lightItemSettings);
        ORANGE_FLUORESCENT_LIGHT_ITEM = new BlockItem(ORANGE_FLUORESCENT_LIGHT, lightItemSettings);
        MAGENTA_FLUORESCENT_LIGHT_ITEM = new BlockItem(MAGENTA_FLUORESCENT_LIGHT, lightItemSettings);
        LIGHT_BLUE_FLUORESCENT_LIGHT_ITEM = new BlockItem(LIGHT_BLUE_FLUORESCENT_LIGHT, lightItemSettings);
        YELLOW_FLUORESCENT_LIGHT_ITEM = new BlockItem(YELLOW_FLUORESCENT_LIGHT, lightItemSettings);
        LIME_FLUORESCENT_LIGHT_ITEM = new BlockItem(LIME_FLUORESCENT_LIGHT, lightItemSettings);
        PINK_FLUORESCENT_LIGHT_ITEM = new BlockItem(PINK_FLUORESCENT_LIGHT, lightItemSettings);
        GRAY_FLUORESCENT_LIGHT_ITEM = new BlockItem(GRAY_FLUORESCENT_LIGHT, lightItemSettings);
        LIGHT_GRAY_FLUORESCENT_LIGHT_ITEM = new BlockItem(LIGHT_GRAY_FLUORESCENT_LIGHT, lightItemSettings);
        CYAN_FLUORESCENT_LIGHT_ITEM = new BlockItem(CYAN_FLUORESCENT_LIGHT, lightItemSettings);
        PURPLE_FLUORESCENT_LIGHT_ITEM = new BlockItem(PURPLE_FLUORESCENT_LIGHT, lightItemSettings);
        BLUE_FLUORESCENT_LIGHT_ITEM = new BlockItem(BLUE_FLUORESCENT_LIGHT, lightItemSettings);
        BROWN_FLUORESCENT_LIGHT_ITEM = new BlockItem(BROWN_FLUORESCENT_LIGHT, lightItemSettings);
        GREEN_FLUORESCENT_LIGHT_ITEM = new BlockItem(GREEN_FLUORESCENT_LIGHT, lightItemSettings);
        RED_FLUORESCENT_LIGHT_ITEM = new BlockItem(RED_FLUORESCENT_LIGHT, lightItemSettings);
        BLACK_FLUORESCENT_LIGHT_ITEM = new BlockItem(BLACK_FLUORESCENT_LIGHT, lightItemSettings);
        FLUORESCENT_TUBE_ITEM = new BlockItem(FLUORESCENT_TUBE, lightItemSettings);
        FLUORESCENT_REPEATER_ITEM = new BlockItem(FLUORESCENT_REPEATER, lightItemSettings);
        GLOW_FLOWER_ITEM = new BlockItem(GLOW_FLOWER, new Item.Settings().itemGroup(ItemGroup.DECORATIONS));

        MULTI_BLOCK_ENTITY = BlockEntityType.Builder
            .create(MultiBlockEntity::new, MULTIBLOCK)
            .build(null);
    }
}
