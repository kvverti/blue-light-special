package io.github.kvverti.bluelightspecial.block.entity;

import com.google.common.collect.ForwardingSet;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import io.github.kvverti.bluelightspecial.BlueLightSpecial;
import io.github.kvverti.bluelightspecial.api.FluorescentPowerSource;
import io.github.kvverti.bluelightspecial.api.RelativeDirection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.command.arguments.BlockStateArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.Property;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class MultiBlockEntity extends BlockEntity implements BlockEntityClientSerializable {

    private static final Logger log = LogManager.getLogger(MultiBlockEntity.class);
    private static final BlockStateArgumentType blockStateParser = BlockStateArgumentType.create();
    private static final Map<World, ForwardingWorld> levelMap = new IdentityHashMap<>();

    private final Set<BlockState> containedStates;
    private final SortedMap<Integer, Set<BlockState>> upcomingTicks = new TreeMap<>();
    private BlockState currentIteratingState;

    public MultiBlockEntity() {
        super(BlueLightSpecial.MULTI_BLOCK_ENTITY);
        containedStates = new BlockStateSet(new HashSet<>());
    }

    @Override
    public void setWorld(World world) {
        super.setWorld(world);
        levelMap.computeIfAbsent(world, ForwardingWorld::new);
    }

    public Set<BlockState> getBlockStates() {
        return Collections.unmodifiableSet(containedStates);
    }

    /**
     * Attempts to add the given block state to this block entity.
     * @return whether the block state was added
     */
    public boolean addBlockState(BlockState state) {
        boolean changed = containedStates.add(state);
        if(changed) {
            this.markDirty();
        }
        return changed;
    }

    /**
     * Called when one of this block entity's contained states replaces
     * itself by calling {@link World#setBlockState}. Note that this method
     * does <em>not</em> remove the old block state, since the other methods
     * in this class handle removing the old state in order to determine state
     * changes.
     */
    boolean setBlockState(BlockState state, int flags) {
        boolean changed = containedStates.add(state);
        if(changed) {
            this.markDirty();
        }
        return changed;
    }

    /**
     * Marks the block state currently in iteration as having a scheduled
     * block update. This is required since we otherwise cannot distinguish
     * which block state scheduled a block tick update.
     */
    void addTick(int ticks) {
        BlockState state = currentIteratingState;
        if(state == null) {
            throw new IllegalStateException("Cannot add tick with null state");
        }
        upcomingTicks.compute(ticks, (k, v) -> {
            if(v == null) { v = new HashSet<>(); }
            v.add(state);
            return v;
        });
    }

    /**
     * {@link Block#getStateForNeighborUpdate} may schedule tick events, but
     * the state that the tick events act upon is the updated state returned
     * by that method. Therefore, we call this inside out forwarding method
     * in order to update the state.
     */
    private void patchTickStates(BlockState oldState, BlockState newState) {
        for(Set<BlockState> toTick : upcomingTicks.values()) {
            if(toTick.remove(oldState)) {
                toTick.add(newState);
            }
        }
    }

    /**
     * Places the given item into this block entity, as if placed into the world.
     */
    public boolean placeStack(PlayerEntity player, Hand hand, BlockHitResult hitResult) {
        ItemUsageContext ctx =
            new ForwardingItemUsageContext(levelMap.get(this.world), player, hand, hitResult);
        Block block = Block.getBlockFromItem(ctx.getItemStack().getItem());
        if(block != Blocks.AIR) {
            // we don't have the state yet, and worse, we don't have an old state
            // to use, so we set the current iterating state to AIR
            currentIteratingState = Blocks.AIR.getDefaultState();
            BlockState state = block.getPlacementState(new ItemPlacementContext(ctx));
            patchTickStates(Blocks.AIR.getDefaultState(), state);
            boolean canPlace = block.canPlaceAt(state, this.world, this.pos);
            boolean changed = canPlace && addBlockState(state);
            currentIteratingState = null;
            if(changed && (player instanceof ServerPlayerEntity)) {
                GameMode mode = ((ServerPlayerEntity)player).interactionManager.getGameMode();
                if(mode.isSurvivalLike()) {
                    ctx.getItemStack().subtractAmount(1);
                }
            }
            return changed;
        }
        return false;
    }

    public int getPowerLevel(Direction attach, RelativeDirection side) {
        int power = 0;
        for(BlockState state : containedStates) {
            if(state.getBlock() instanceof FluorescentPowerSource) {
                FluorescentPowerSource src = (FluorescentPowerSource)state.getBlock();
                power = Math.max(power, src.getPowerLevel(state, this.world, this.pos, attach, side));
            }
        }
        return power;
    }

    public boolean canConnect(Direction attach, RelativeDirection side) {
        for(BlockState state : containedStates) {
            if(state.getBlock() instanceof FluorescentPowerSource) {
                FluorescentPowerSource src = (FluorescentPowerSource)state.getBlock();
                if(src.canConnect(state, this.world, this.pos, attach, side)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Set block states based on neighbor update.
     */
    public boolean getStateForNeighborUpdate(Direction dir, BlockState neighbor, BlockPos neighborPos) {
        List<BlockState> newStates = new ArrayList<>(containedStates.size());
        ForwardingWorld forwardWorld = levelMap.get(this.world);
        forwardWorld.setCurrentMulti(this);
        boolean dirty = false;
        for(Iterator<BlockState> itr = containedStates.iterator(); itr.hasNext(); ) {
            BlockState state = currentIteratingState = itr.next();
            BlockState newState = state.getStateForNeighborUpdate(dir, neighbor, forwardWorld, this.pos, neighborPos);
            if(state != newState) {
                patchTickStates(state, newState);
                newStates.add(newState);
                itr.remove();
                dirty = true;
            }
        }
        currentIteratingState = null;
        containedStates.addAll(newStates);
        if(dirty) {
            this.markDirty();
        }
        return dirty;
    }

    public boolean neighborUpdate(Block neighbor, BlockPos neighborPos, boolean idk) {
        // call the callback methods
        ForwardingWorld forwardWorld = levelMap.get(this.world);
        forwardWorld.setCurrentMulti(this);
        boolean dirty = false;
        Set<BlockState> tmpStates = new HashSet<>(containedStates);
        int formerSize = containedStates.size();
        for(BlockState state : tmpStates) {
            currentIteratingState = state;
            state.neighborUpdate(forwardWorld, this.pos, neighbor, neighborPos, false);
            if(containedStates.size() != formerSize) {
                containedStates.remove(state);
                dirty = true;
            }
        }
        currentIteratingState = null;
        if(dirty) {
            this.markDirty();
        }
        return dirty;
    }

    /**
     * Called when one of this block entity's contained states
     * schedules a block tick update.
     */
    public boolean onScheduledTick(Random rand) {
        if(upcomingTicks.isEmpty()) {
            log.warn("Multiblock ticked, but nothing in queue");
            return false;
        }
        // tick the first element states
        boolean dirty = false;
        Integer tick0 = upcomingTicks.firstKey();
        Set<BlockState> toTick = upcomingTicks.remove(tick0);
        int formerSize = containedStates.size();
        ForwardingWorld forwardWorld = levelMap.get(this.world);
        forwardWorld.setCurrentMulti(this);
        for(BlockState state : toTick) {
            currentIteratingState = state;
            if(containedStates.contains(state)) {
                state.getBlock().onScheduledTick(state, forwardWorld, this.pos, rand);
                if(containedStates.size() != formerSize) {
                    containedStates.remove(state);
                    dirty = true;
                }
            }
        }
        currentIteratingState = null;
        if(dirty) {
            this.markDirty();
        }
        return dirty;
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        tag = super.toTag(tag);
        ListTag states = new ListTag();
        for(BlockState bs : containedStates) {
            states.add(serialize(bs));
        }
        tag.put("States", states);
        return tag;
    }

    @Override
    public void fromTag(CompoundTag tag) {
        super.fromTag(tag);
        containedStates.clear();
        ListTag states = tag.getList("States", 8);
        for(int i = 0; i < states.size(); i++) {
            String str = states.getString(i);
            StringReader reader = new StringReader(str);
            try {
                BlockState state = blockStateParser.parse(reader).getBlockState();
                containedStates.add(state);
            } catch(CommandSyntaxException e) {
                log.error("Could not parse block state string: " + str);
            }
        }
    }

    @Override
    public CompoundTag toClientTag(CompoundTag tag) {
        return toTag(tag);
    }

    @Override
    public void fromClientTag(CompoundTag tag) {
        fromTag(tag);
    }

    private StringTag serialize(BlockState state) {
        StringBuilder sb = new StringBuilder();
        String blockId = Registry.BLOCK.getId(state.getBlock()).toString();
        sb.append(blockId);
        if(!state.getProperties().isEmpty()) {
            sb.append('[');
            for(Property<?> prop : state.getProperties()) {
                sb.append(prop.getName())
                    .append('=')
                    .append(getPropStringValue(state, prop))
                    .append(',');
            }
            sb.setCharAt(sb.length() - 1, ']');
        }
        return new StringTag(sb.toString());
    }

    private <T extends Comparable<T>> String getPropStringValue(BlockState state, Property<T> prop) {
        return prop.getValueAsString(state.get(prop));
    }

    private static class BlockStateSet extends ForwardingSet<BlockState> {

        private final Set<BlockState> delegate;

        BlockStateSet(Set<BlockState> delegate) {
            this.delegate = delegate;
        }

        @Override
        protected Set<BlockState> delegate() { return delegate; }

        @Override
        public boolean add(BlockState state) {
            boolean changed = false;
            if(state.getRenderType() != BlockRenderType.MODEL) {
                log.warn("Ignored multiblock state with non-model render type");
            } else if(state.getBlock() instanceof BlockEntityProvider) {
                log.warn("Ignored multiblock state with block entity");
            } else {
                changed = delegate.add(state);
            }
            return changed;
        }

        @Override
        public boolean addAll(Collection<? extends BlockState> collection) {
            boolean changed = false;
            for(BlockState state : collection) {
                changed |= add(state);
            }
            return changed;
        }
    };
}
