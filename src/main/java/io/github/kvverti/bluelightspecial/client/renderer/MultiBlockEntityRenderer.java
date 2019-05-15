package io.github.kvverti.bluelightspecial.client.renderer;

import net.minecraft.world.World;
import java.util.Random;
import net.minecraft.client.render.model.BakedModel;
import com.mojang.blaze3d.platform.GlStateManager;

import io.github.kvverti.bluelightspecial.block.entity.MultiBlockEntity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public class MultiBlockEntityRenderer extends BlockEntityRenderer<MultiBlockEntity> {

    private final Random rand = new Random();

    @Override
    public void render(MultiBlockEntity blockEntity, double x, double y, double z, float partialTicks, int destroyStage) {
        BlockRenderManager renderManager = MinecraftClient.getInstance().getBlockRenderManager();
        World world = this.getWorld();
        BlockPos pos = blockEntity.getPos();
        this.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
        GlStateManager.pushMatrix();
        GlStateManager.translated(x - pos.getX(), y - pos.getY(), z - pos.getZ());
        for(BlockState state : blockEntity.getBlockStates()) {
            assert state.getRenderType() == BlockRenderType.MODEL : state.getRenderType();
            boolean translucent = state.getBlock().getRenderLayer() == BlockRenderLayer.TRANSLUCENT;
            if(translucent) {
                GlStateManager.enableNormalize();
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            }
            Tessellator tez = Tessellator.getInstance();
            BufferBuilder bb = tez.getBufferBuilder();
            bb.begin(7, VertexFormats.POSITION_COLOR_UV_LMAP);
            BakedModel model = renderManager.getModel(state);
            renderManager.getModelRenderer().tesselate(world, model, state, pos, bb, false, rand, state.getRenderingSeed(pos));
            tez.draw();
            if(translucent) {
                GlStateManager.disableBlend();
                GlStateManager.disableNormalize();
            }
        }
        GlStateManager.popMatrix();
    }
}
