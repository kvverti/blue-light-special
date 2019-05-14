package io.github.kvverti.bluelightspecial.block.entity;

import net.minecraft.block.BlockEntityProvider;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import io.github.kvverti.bluelightspecial.BlueLightSpecial;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;

import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.command.arguments.BlockStateArgumentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.state.property.Property;
import net.minecraft.util.registry.Registry;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class MultiBlockEntity extends BlockEntity implements BlockEntityClientSerializable {

    private static final Logger log = LogManager.getLogger(MultiBlockEntity.class);
    private static final BlockStateArgumentType blockStateParser = BlockStateArgumentType.create();

    private final Set<BlockState> containedStates = new HashSet<>();

    public MultiBlockEntity() {
        super(BlueLightSpecial.MULTI_BLOCK_ENTITY);
    }

    public Set<BlockState> getBlockStates() {
        return Collections.unmodifiableSet(containedStates);
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
                if(state.getRenderType() != BlockRenderType.MODEL) {
                    log.warn("Non-model render types are not supported in bls:multiblock");
                } else if(state.getBlock() instanceof BlockEntityProvider) {
                    log.warn("Block entities are not supported in bls:multiblock");
                } else {
                    containedStates.add(state);
                }
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
}
