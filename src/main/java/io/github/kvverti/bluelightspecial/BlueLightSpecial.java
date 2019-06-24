package io.github.kvverti.bluelightspecial;

import com.google.common.collect.ImmutableMap;

import io.github.kvverti.bluelightspecial.block.CagedBulbBlock;
import io.github.kvverti.bluelightspecial.block.FluorescentLightBlock;
import io.github.kvverti.bluelightspecial.block.FluorescentRepeaterBlock;
import io.github.kvverti.bluelightspecial.block.MultiBlock;
import io.github.kvverti.bluelightspecial.block.TwistleBlock;
import io.github.kvverti.bluelightspecial.block.TwistlePlantBlock;
import io.github.kvverti.bluelightspecial.block.entity.MultiBlockEntity;
import io.github.kvverti.bluelightspecial.feature.FluorescentFlowerFeature;

import java.util.function.Function;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.fabricmc.fabric.api.event.server.ServerStopCallback;

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
import net.minecraft.util.Lazy;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.decorator.CountDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.FeatureConfig;

public class BlueLightSpecial implements ModInitializer {

    public static final String MODID = "bluelightspecial";

    // custom blocks

    public static final ImmutableMap<DyeColor, Block> FLUORESCENT_LIGHTS;
    public static final ImmutableMap<DyeColor, Block> CAGED_LANTERNS;
    public static final Block FLUORESCENT_TUBE;
    public static final Block FLUORESCENT_REPEATER;
    public static final Block MULTIBLOCK;
    public static final Block GLOW_FLOWER;
    public static final Block POTTED_GLOW_FLOWER;
    public static final Block TWISTLE_PLANT;
    public static final Block TWISTLE;

    // custom items

    public static final ImmutableMap<DyeColor, Item> FLUORESCENT_LIGHT_ITEMS;
    public static final ImmutableMap<DyeColor, Item> CAGED_LANTERN_ITEMS;
    public static final Item FLUORESCENT_TUBE_ITEM;
    public static final Item FLUORESCENT_REPEATER_ITEM;
    public static final Item GLOW_FLOWER_ITEM;
    public static final Item TWISTLE_ITEM;
    public static final Item FLUORESCENT_DUST;
    public static final Item CONCENTRATED_FLUORESCENT_DUST;

    // custom block entities

    public static final BlockEntityType<MultiBlockEntity> MULTI_BLOCK_ENTITY;

    // custom features

    public static final FluorescentFlowerFeature GLOW_FLOWER_FEATURE;

    @Override
    public void onInitialize() {
        // blocks
        registerColored(Registry.BLOCK, "fluorescent_light", FLUORESCENT_LIGHTS);
        registerColored(Registry.BLOCK, "caged_lantern", CAGED_LANTERNS);
        register(Registry.BLOCK, "fluorescent_tube", FLUORESCENT_TUBE);
        register(Registry.BLOCK, "fluorescent_repeater", FLUORESCENT_REPEATER);
        register(Registry.BLOCK, "multiblock", MULTIBLOCK);
        register(Registry.BLOCK, "glow_flower", GLOW_FLOWER);
        register(Registry.BLOCK, "potted_glow_flower", POTTED_GLOW_FLOWER);
        register(Registry.BLOCK, "twistle_plant", TWISTLE_PLANT);
        register(Registry.BLOCK, "twistle", TWISTLE);

        // items
        registerColored(Registry.ITEM, "fluorescent_light", FLUORESCENT_LIGHT_ITEMS);
        registerColored(Registry.ITEM, "caged_lantern", CAGED_LANTERN_ITEMS);
        register(Registry.ITEM, "fluorescent_tube", FLUORESCENT_TUBE_ITEM);
        register(Registry.ITEM, "fluorescent_repeater", FLUORESCENT_REPEATER_ITEM);
        register(Registry.ITEM, "glow_flower", GLOW_FLOWER_ITEM);
        register(Registry.ITEM, "twistle", TWISTLE_ITEM);
        register(Registry.ITEM, "fluorescent_dust", FLUORESCENT_DUST);
        register(Registry.ITEM, "concentrated_fluorescent_dust", CONCENTRATED_FLUORESCENT_DUST);

        // block entities
        register(Registry.BLOCK_ENTITY, "multiblock", MULTI_BLOCK_ENTITY);

        // features
        register(Registry.FEATURE, "fluorescent_flowers", GLOW_FLOWER_FEATURE);

        // tweak vanilla biomes
        ConfiguredFeature<?> glowFlower = Biome.configureFeature(
            BlueLightSpecial.GLOW_FLOWER_FEATURE,
            FeatureConfig.DEFAULT,
            Decorator.COUNT_HEIGHTMAP_32,
            new CountDecoratorConfig(2));
        Biomes.FOREST.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, glowFlower);
        Biomes.BIRCH_FOREST.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, glowFlower);
        Biomes.BIRCH_FOREST_HILLS.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, glowFlower);
        Biomes.DARK_FOREST.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, glowFlower);
        Biomes.DARK_FOREST_HILLS.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, glowFlower);

        // callbacks
        ServerStopCallback.EVENT.register(server -> MultiBlockEntity.clearLevelCache());
    }

    private static <T> void registerColored(Registry<T> registry, String baseName, ImmutableMap<DyeColor, T> objects) {
        for(ImmutableMap.Entry<DyeColor, T> entry : objects.entrySet()) {
            String name = entry.getKey().getName() + "_" + baseName;
            register(registry, name, entry.getValue());
        }
    }

    private static <T> void register(Registry<T> registry, String name, T object) {
        Registry.register(registry, new Identifier(MODID, name), object);
    }

    private static <T> ImmutableMap<DyeColor, T> makeColored(Function<? super DyeColor, ? extends T> template) {
        ImmutableMap.Builder<DyeColor, T> build = ImmutableMap.builder();
        for(DyeColor color : DyeColor.values()) {
            build.put(color, template.apply(color));
        }
        return build.build();
    }

    static {
        Block.Settings lightSettings = FabricBlockSettings.of(Material.PART)
            .strength(0.1f, 0.1f)
            .sounds(BlockSoundGroup.GLASS)
            .noCollision()
            .lightLevel(15)
            .build();
        FLUORESCENT_LIGHTS = makeColored(col -> new FluorescentLightBlock(col, lightSettings));

        Block.Settings lanternSettings = FabricBlockSettings.of(Material.PART)
            .strength(0.3f, 0.3f)
            .sounds(BlockSoundGroup.GLASS)
            .build();
        CAGED_LANTERNS = makeColored(col -> new CagedBulbBlock(col, lanternSettings));

        Block.Settings tubeSettings = FabricBlockSettings.of(Material.PART)
            .strength(0.1f, 0.1f)
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
        TWISTLE_PLANT = new TwistlePlantBlock(FabricBlockSettings.of(Material.PLANT)
            .sounds(BlockSoundGroup.WET_GRASS)
            .noCollision()
            .lightLevel(11)
            .build(), new Lazy<>(() -> BlueLightSpecial.TWISTLE));
        TWISTLE = new TwistleBlock(FabricBlockSettings.of(Material.PLANT)
            .sounds(BlockSoundGroup.WET_GRASS)
            .noCollision()
            .lightLevel(11)
            .ticksRandomly()
            .build(), new Lazy<>(() -> TWISTLE_PLANT));

        Item.Settings lightItemSettings = new Item.Settings()
            .itemGroup(ItemGroup.REDSTONE);
        FLUORESCENT_LIGHT_ITEMS = makeColored(col -> new BlockItem(FLUORESCENT_LIGHTS.get(col), lightItemSettings));
        CAGED_LANTERN_ITEMS = makeColored(col -> new BlockItem(CAGED_LANTERNS.get(col), lightItemSettings));
        FLUORESCENT_TUBE_ITEM = new BlockItem(FLUORESCENT_TUBE, lightItemSettings);
        FLUORESCENT_REPEATER_ITEM = new BlockItem(FLUORESCENT_REPEATER, lightItemSettings);

        Item.Settings matSettings = new Item.Settings().itemGroup(ItemGroup.MATERIALS);
        FLUORESCENT_DUST = new Item(matSettings);
        CONCENTRATED_FLUORESCENT_DUST = new Item(matSettings);

        Item.Settings decoSettings = new Item.Settings().itemGroup(ItemGroup.DECORATIONS);
        GLOW_FLOWER_ITEM = new BlockItem(GLOW_FLOWER, decoSettings);
        TWISTLE_ITEM = new BlockItem(TWISTLE, decoSettings);

        MULTI_BLOCK_ENTITY = BlockEntityType.Builder
            .create(MultiBlockEntity::new, MULTIBLOCK)
            .build(null);

        GLOW_FLOWER_FEATURE = new FluorescentFlowerFeature(DefaultFeatureConfig::deserialize);
    }
}
