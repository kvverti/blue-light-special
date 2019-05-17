package io.github.kvverti.bluelightspecial.block.entity;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import io.github.kvverti.bluelightspecial.BlueLightSpecial;
import io.github.kvverti.bluelightspecial.api.FluorescentPowerSource;
import io.github.kvverti.bluelightspecial.api.RelativeDirection;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.command.arguments.BlockStateArgumentType;
import net.minecraft.entity.EntityContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class MultiBlockEntity extends BlockEntity implements BlockEntityClientSerializable {

    private static final Logger log = LogManager.getLogger(MultiBlockEntity.class);
    private static final BlockStateArgumentType blockStateParser = BlockStateArgumentType.create();
    private static final Map<World, ForwardingWorld> levelMap = new IdentityHashMap<>();

    private final Map<Direction, BlockState> containedStates;
    private final SortedMap<Integer, Map<Direction, BlockState>> upcomingTicks = new TreeMap<>();
    private Map.Entry<Direction, BlockState> currentIteratingEntry;
    private boolean stateUpdated;

    public MultiBlockEntity() {
        super(BlueLightSpecial.MULTI_BLOCK_ENTITY);
        containedStates = new EnumMap<>(Direction.class);
    }

    /**
     * Queries whether a contained block state was updated. Calling this
     * method clears the state update flag.
     */
    private boolean stateUpdated() {
        boolean updated = stateUpdated;
        stateUpdated = false;
        return updated;
    }

    private boolean addContainedState(Direction dir, BlockState state) {
        boolean changed = false;
        if(state.getRenderType() != BlockRenderType.MODEL) {
            log.warn("Ignored multiblock state with non-model render type");
        } else if(state.getBlock() instanceof BlockEntityProvider) {
            log.warn("Ignored multiblock state with block entity");
        } else if(containedStates.put(dir, state) != state) {
            changed = true;
        }
        return changed;
    }

    @Override
    public void setWorld(World world) {
        super.setWorld(world);
        levelMap.computeIfAbsent(world, ForwardingWorld::new);
    }

    public Iterable<BlockState> getBlockStates() {
        return Collections.unmodifiableCollection(containedStates.values());
    }

    /**
     * Attempts to add the given block state to this block entity.
     * @return whether the block state was added
     */
    public boolean addBlockState(Direction dir, BlockState state) {
        boolean changed = !containedStates.containsKey(dir) &&
            state.canPlaceAt(this.world, this.pos) &&
            addContainedState(dir, state);
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
        if(currentIteratingEntry == null) {
            throw new IllegalStateException("Cannot set block state from null");
        }
        boolean changed = addContainedState(currentIteratingEntry.getKey(), state);
        if(changed) {
            this.markDirty();
            stateUpdated = true;
        }
        return changed;
    }

    /**
     * Schedules a block tick through this block entity.
     */
    public void scheduleTick(Direction side, int ticks) {
        BlockState state = containedStates.get(side);
        if(state == null) {
            throw new IllegalArgumentException("Cannot add tick with null state");
        }
        upcomingTicks.compute(ticks, (k, v) -> {
            if(v == null) { v = new HashMap<>(); }
            v.put(side, state);
            return v;
        });
        this.world.getBlockTickScheduler().schedule(this.pos, BlueLightSpecial.MULTIBLOCK, ticks);
    }

    /**
     * Marks the block state currently in iteration as having a scheduled
     * block update. This is required since we otherwise cannot distinguish
     * which block state scheduled a block tick update.
     */
    void addTick(int ticks) {
        if(currentIteratingEntry == null) {
            throw new IllegalStateException("Cannot add tick with null state");
        }
        Direction dir = currentIteratingEntry.getKey();
        BlockState state = currentIteratingEntry.getValue();
        upcomingTicks.compute(ticks, (k, v) -> {
            if(v == null) { v = new HashMap<>(); }
            v.put(dir, state);
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
        for(Map<Direction, BlockState> toTick : upcomingTicks.values()) {
            toTick.replaceAll((k, v) -> v == oldState ? newState : v);
        }
    }

    /**
     * Retrieves the visual bounding box.
     */
    public VoxelShape getOutlineShape(EntityContext ctx) {
        VoxelShape shape = VoxelShapes.empty();
        for(BlockState state : containedStates.values()) {
            shape = VoxelShapes.union(shape, state.getOutlineShape(this.world, this.pos, ctx));
        }
        if(shape == VoxelShapes.empty()) {
            shape = VoxelShapes.fullCube();
        }
        return shape;
    }

    public int getPowerLevel(Direction attach, RelativeDirection side) {
        int power = 0;
        for(BlockState state : containedStates.values()) {
            if(state.getBlock() instanceof FluorescentPowerSource) {
                FluorescentPowerSource src = (FluorescentPowerSource)state.getBlock();
                power = Math.max(power, src.getPowerLevel(state, this.world, this.pos, attach, side));
            }
        }
        return power;
    }

    public boolean canConnect(Direction attach, RelativeDirection side) {
        for(BlockState state : containedStates.values()) {
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
        Map<Direction, BlockState> newStates = new HashMap<>();
        ForwardingWorld forwardWorld = levelMap.get(this.world);
        forwardWorld.setCurrentMulti(this);
        boolean dirty = false;
        for(Iterator<Map.Entry<Direction, BlockState>> itr = containedStates.entrySet().iterator(); itr.hasNext(); ) {
            currentIteratingEntry = itr.next();
            BlockState state = currentIteratingEntry.getValue();
            BlockState newState = state.getStateForNeighborUpdate(dir, neighbor, forwardWorld, this.pos, neighborPos);
            if(state != newState) {
                patchTickStates(state, newState);
                newStates.put(currentIteratingEntry.getKey(), newState);
                itr.remove();
                dirty = true;
            }
        }
        currentIteratingEntry = null;
        for(Map.Entry<Direction, BlockState> entry : newStates.entrySet()) {
            addContainedState(entry.getKey(), entry.getValue());
        }
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
        Set<Map.Entry<Direction, BlockState>> tmpStates = new HashSet<>(containedStates.entrySet());
        for(Map.Entry<Direction, BlockState> entry : tmpStates) {
            currentIteratingEntry = entry;
            BlockState state = entry.getValue();
            state.neighborUpdate(forwardWorld, this.pos, neighbor, neighborPos, false);
            if(stateUpdated()) {
                dirty = true;
            }
        }
        currentIteratingEntry = null;
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
        Map<Direction, BlockState> toTick = upcomingTicks.remove(tick0);
        ForwardingWorld forwardWorld = levelMap.get(this.world);
        forwardWorld.setCurrentMulti(this);
        for(Map.Entry<Direction, BlockState> entry : toTick.entrySet()) {
            currentIteratingEntry = entry;
            BlockState state = entry.getValue();
            if(containedStates.get(entry.getKey()) == state) {
                state.getBlock().onScheduledTick(state, forwardWorld, this.pos, rand);
                if(stateUpdated()) {
                    dirty = true;
                }
            }
        }
        currentIteratingEntry = null;
        if(dirty) {
            this.markDirty();
        }
        return dirty;
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        tag = super.toTag(tag);
        CompoundTag states = new CompoundTag();
        for(Map.Entry<Direction, BlockState> entry : containedStates.entrySet()) {
            states.put(
                entry.getKey().getName(),
                serialize(entry.getValue()));
        }
        tag.put("StateMap", states);
        return tag;
    }

    @Override
    public void fromTag(CompoundTag tag) {
        super.fromTag(tag);
        containedStates.clear();
        CompoundTag states = tag.getCompound("StateMap");
        for(Direction dir : Direction.values()) {
            if(states.containsKey(dir.getName(), 8)) {
                String str = states.getString(dir.getName());
                StringReader reader = new StringReader(str);
                try {
                    BlockState state = blockStateParser.parse(reader).getBlockState();
                    containedStates.put(dir, state);
                } catch(CommandSyntaxException e) {
                    log.error("Could not parse block state string: " + str);
                }
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
}
