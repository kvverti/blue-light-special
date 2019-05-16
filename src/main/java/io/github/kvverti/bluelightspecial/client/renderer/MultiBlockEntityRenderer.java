package io.github.kvverti.bluelightspecial.client.renderer;

import net.minecraft.client.render.model.BakedModel;
import com.mojang.blaze3d.platform.GlStateManager;

import io.github.kvverti.bluelightspecial.block.entity.MultiBlockEntity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.texture.SpriteAtlasTexture;

@Environment(EnvType.CLIENT)
public class MultiBlockEntityRenderer extends BlockEntityRenderer<MultiBlockEntity> {

    @Override
    public void render(MultiBlockEntity blockEntity, double x, double y, double z, float partialTicks, int destroyStage) {
        BlockRenderManager renderManager = MinecraftClient.getInstance().getBlockRenderManager();
        GlStateManager.pushMatrix();
        GlStateManager.translated(x, y, z);
        this.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
        for(BlockState state : blockEntity.getBlockStates()) {
            assert state.getRenderType() == BlockRenderType.MODEL : state.getRenderType();
            boolean translucent = state.getBlock().getRenderLayer() == BlockRenderLayer.TRANSLUCENT;
            if(translucent) {
                GlStateManager.enableNormalize();
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            }
            BakedModel model = renderManager.getModel(state);
            GlStateManager.rotatef(-90.0f, 0.0f, 1.0f, 0.0f);
            renderManager.getModelRenderer().render(model, state, 1.0f, true);
            if(translucent) {
                GlStateManager.disableBlend();
                GlStateManager.disableNormalize();
            }
        }
        GlStateManager.popMatrix();
    }
}
