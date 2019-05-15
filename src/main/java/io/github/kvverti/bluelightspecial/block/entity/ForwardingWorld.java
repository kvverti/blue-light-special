package io.github.kvverti.bluelightspecial.block.entity;

import io.github.kvverti.bluelightspecial.BlueLightSpecial;

import java.util.List;
import java.util.stream.Stream;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.map.MapState;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.tag.RegistryTagManager;
import net.minecraft.util.TaskPriority;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ScheduledTick;
import net.minecraft.world.TickScheduler;
import net.minecraft.world.World;

/**
 * Forwards block updates to MultiBlockEntity. Will extend World until
 * Block methods stop taking concrete class parameters.
 */
public class ForwardingWorld extends World {

    private final World forward;
    private final ForwardingBlockTickScheduler scheduler;
    private MultiBlockEntity be;

    public ForwardingWorld(World forward) {
        super(forward.getLevelProperties(),
            forward.dimension.getType(),
            (a, b) -> forward.getChunkManager(),
            forward.getProfiler(),
            forward.isClient);
        this.forward = forward;
        this.scheduler = new ForwardingBlockTickScheduler(forward.getBlockTickScheduler());
    }

    public void setCurrentMulti(MultiBlockEntity be) {
        this.be = be;
    }

    @Override
    public boolean setBlockState(BlockPos pos, BlockState state, int flags) {
        BlockEntity be = this.getBlockEntity(pos);
        if(be instanceof MultiBlockEntity) {
            return ((MultiBlockEntity)be).setBlockState(state, flags);
        } else {
            return super.setBlockState(pos, state, flags);
        }
    }

    @Override
    public RecipeManager getRecipeManager() {
        return forward.getRecipeManager();
    }

    @Override
    public List<? extends PlayerEntity> getPlayers() {
        return forward.getPlayers();
    }

    @Override
    public Entity getEntityById(int id) {
        return forward.getEntityById(id);
    }

    @Override
    public TickScheduler<Fluid> getFluidTickScheduler() {
        return forward.getFluidTickScheduler();
    }

    @Override
    public RegistryTagManager getTagManager() {
        return forward.getTagManager();
    }

    @Override
    public void putMapState(MapState state) {
        forward.putMapState(state);
    }

    @Override
    public void updateListeners(BlockPos var1, BlockState var2, BlockState var3, int var4) {
        forward.updateListeners(var1, var2, var3, var4);
    }

    @Override
    public void playLevelEvent(PlayerEntity var1, int var2, BlockPos var3, int var4) {
        forward.playLevelEvent(var1, var2, var3, var4);
    }

    @Override
    public void setBlockBreakingProgress(int var1, BlockPos var2, int var3) {
        forward.setBlockBreakingProgress(var1, var2, var3);
    }

    @Override
    public int getNextMapId() {
        return forward.getNextMapId();
    }

    @Override
    public void playSound(PlayerEntity var1, double var2, double var4, double var6, SoundEvent var8, SoundCategory var9,
            float var10, float var11) {
        forward.playSound(var1, var2, var4, var6, var8, var9, var10, var11);
    }

    @Override
    public Scoreboard getScoreboard() {
        return forward.getScoreboard();
    }

    @Override
    public void playSoundFromEntity(PlayerEntity var1, Entity var2, SoundEvent var3, SoundCategory var4, float var5,
            float var6) {
        forward.playSoundFromEntity(var1, var2, var3, var4, var5, var6);
    }

    @Override
    public MapState getMapState(String var1) {
        return forward.getMapState(var1);
    }

    @Override
    public TickScheduler<Block> getBlockTickScheduler() {
        return scheduler;
    }

    /**
     * Replaces the block tick scheduler so block ticks are forwarded correctly.
     */
    private class ForwardingBlockTickScheduler implements TickScheduler<Block> {

        private final TickScheduler<Block> forward;

        ForwardingBlockTickScheduler(TickScheduler<Block> forward) {
            this.forward = forward;
        }

        @Override
        public void schedule(BlockPos pos, Block block, int ticks, TaskPriority priority) {
            BlockEntity be = ForwardingWorld.this.getBlockEntity(pos);
            if(be instanceof MultiBlockEntity) {
                forward.schedule(pos, BlueLightSpecial.MULTIBLOCK, ticks, priority);
                ForwardingWorld.this.be.addTick(ticks);
            } else {
                forward.schedule(pos, block, ticks, priority);
            }
        }

        @Override
        public boolean isScheduled(BlockPos var1, Block var2) {
            return forward.isScheduled(var1, var2);
        }

        @Override
        public boolean isTicking(BlockPos var1, Block var2) {
            return forward.isTicking(var1, var2);
        }

        @Override
        public void method_20470(Stream<ScheduledTick<Block>> var1) {
            forward.method_20470(var1);
        }
    }
}
