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
        ColorProviderRegistry.BLOCK.register(cagedLanternColors, BlueLightSpecial.WHITE_CAGED_LANTERN);
    }
}
