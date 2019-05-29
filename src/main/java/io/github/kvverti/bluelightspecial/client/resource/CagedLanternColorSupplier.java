package io.github.kvverti.bluelightspecial.client.resource;

import io.github.kvverti.bluelightspecial.BlueLightSpecial;
import io.github.kvverti.bluelightspecial.block.CagedBulbBlock;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;

import net.minecraft.block.BlockState;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.client.util.RawTextureDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.ExtendedBlockView;

@Environment(EnvType.CLIENT)
public class CagedLanternColorSupplier implements SimpleResourceReloadListener<int[]>, BlockColorProvider {

    private static final Identifier TEXTURE = new Identifier(BlueLightSpecial.MODID, "textures/colormap/caged_lantern.png");

    private final Identifier id;
    private int[] colorData = new int[16];

    public CagedLanternColorSupplier(Identifier id) {
        this.id = id;
    }

    @Override
    public Identifier getFabricId() {
        return id;
    }

    @Override
    public int getColor(BlockState state, ExtendedBlockView world, BlockPos pos, int tintIdx) {
        int powerIdx = state.get(CagedBulbBlock.POWER);
        return colorData[powerIdx];
    }

    @Override
    public CompletableFuture<int[]> load(ResourceManager manager, Profiler prof, Executor exec) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return RawTextureDataLoader.loadRawTextureData(manager, TEXTURE);
            } catch(IOException e) {
                throw new IllegalStateException("Failed to load caged lantern colormap", e);
            }
        }, exec);
    }

    @Override
    public CompletableFuture<Void> apply(int[] data, ResourceManager manager, Profiler prof, Executor exec) {
        return CompletableFuture.runAsync(() -> colorData = data, exec);
    }
}
