package io.github.kvverti.bluelightspecial;

import io.github.kvverti.bluelightspecial.block.entity.MultiBlockEntity;
import io.github.kvverti.bluelightspecial.client.renderer.MultiBlockEntityRenderer;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.render.BlockEntityRendererRegistry;

public class BlueLightSpecialClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BlockEntityRendererRegistry.INSTANCE.register(MultiBlockEntity.class, new MultiBlockEntityRenderer());
    }
}
