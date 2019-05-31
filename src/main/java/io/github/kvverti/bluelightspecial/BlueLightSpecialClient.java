package io.github.kvverti.bluelightspecial;

import io.github.kvverti.bluelightspecial.block.entity.MultiBlockEntity;
import io.github.kvverti.bluelightspecial.client.renderer.MultiBlockEntityRenderer;
import io.github.kvverti.bluelightspecial.client.resource.CagedLanternColorSupplier;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.render.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.render.ColorProviderRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;

import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

public class BlueLightSpecialClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BlockEntityRendererRegistry.INSTANCE.register(MultiBlockEntity.class, new MultiBlockEntityRenderer());
        CagedLanternColorSupplier cagedLanternColors =
            new CagedLanternColorSupplier(new Identifier(BlueLightSpecial.MODID, "caged_lantern_color"));
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(cagedLanternColors);
        ColorProviderRegistry.BLOCK.register(cagedLanternColors,
            BlueLightSpecial.BLACK_CAGED_LANTERN,
            BlueLightSpecial.RED_CAGED_LANTERN,
            BlueLightSpecial.GREEN_CAGED_LANTERN,
            BlueLightSpecial.BROWN_CAGED_LANTERN,
            BlueLightSpecial.BLUE_CAGED_LANTERN,
            BlueLightSpecial.PURPLE_CAGED_LANTERN,
            BlueLightSpecial.CYAN_CAGED_LANTERN,
            BlueLightSpecial.LIGHT_GRAY_CAGED_LANTERN,
            BlueLightSpecial.GRAY_CAGED_LANTERN,
            BlueLightSpecial.PINK_CAGED_LANTERN,
            BlueLightSpecial.LIME_CAGED_LANTERN,
            BlueLightSpecial.YELLOW_CAGED_LANTERN,
            BlueLightSpecial.LIGHT_BLUE_CAGED_LANTERN,
            BlueLightSpecial.MAGENTA_CAGED_LANTERN,
            BlueLightSpecial.ORANGE_CAGED_LANTERN,
            BlueLightSpecial.WHITE_CAGED_LANTERN);
    }
}
