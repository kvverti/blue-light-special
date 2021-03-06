package io.github.kvverti.bluelightspecial.block;

import io.github.kvverti.bluelightspecial.api.FluorescentPowerSource;
import io.github.kvverti.bluelightspecial.api.MultiBlockComponent;
import io.github.kvverti.bluelightspecial.api.RelativeDirection;
import io.github.kvverti.bluelightspecial.block.entity.MultiBlockEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.IntegerProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.world.loot.context.LootContextParameters;

/**
 * Allows multiple fluorescent lights to coexist in the same block space.
 */
public class MultiBlock extends Block implements BlockEntityProvider, FluorescentPowerSource {

    // changing this enables us to trigger a block update when the block
    // entity's state changes
    public static final Property<Integer> PARITY = IntegerProperty.create("parity", 0, 1);
    public static final Property<Integer> LIGHT = IntegerProperty.create("light", 0, 15);

    public MultiBlock(Block.Settings settings) {
        super(settings);
        this.setDefaultState(this.stateFactory.getDefaultState()
            .with(PARITY, 0)
            .with(LIGHT, 0));
    }

    @Override
    protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
        builder.add(PARITY)
            .add(LIGHT);
    }

    @Override
    public BlockEntity createBlockEntity(BlockView var1) {
        return new MultiBlockEntity();
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public int getLuminance(BlockState state) {
        return state.get(LIGHT);
    }

    @Override
    public boolean canReplace(BlockState state, ItemPlacementContext ctx) {
        return Block.getBlockFromItem(ctx.getItemStack().getItem()) instanceof MultiBlockComponent;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, EntityContext ctx) {
        BlockEntity be = world.getBlockEntity(pos);
        if(be instanceof MultiBlockEntity) {
            return ((MultiBlockEntity)be).getOutlineShape(ctx);
        }
        return VoxelShapes.fullCube();
    }

    @Override
    public List<ItemStack> getDroppedStacks(BlockState state, LootContext.Builder builder) {
        BlockEntity be = builder.get(LootContextParameters.BLOCK_ENTITY);
        if(be instanceof MultiBlockEntity) {
            List<ItemStack> ls = new ArrayList<>();
            for(BlockState component : ((MultiBlockEntity)be).getBlockStates()) {
                ls.addAll(component.getDroppedStacks(builder));
            }
            return ls;
        }
        return Collections.emptyList();
    }

    /**
     * Forwards to block entity for state updates.
     */
    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction dir, BlockState neighbor, IWorld world, BlockPos pos, BlockPos neighborPos) {
        BlockEntity be = world.getBlockEntity(pos);
        if(be instanceof MultiBlockEntity) {
            MultiBlockEntity multiblock = (MultiBlockEntity)be;
            boolean toggle = multiblock.getStateForNeighborUpdate(dir, neighbor, neighborPos);
            return toggle ? getReplacementState(state, multiblock) : state;
        }
        return state;
    }

    /**
     * Forwards to block entity for state updates.
     */
    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block neighbor, BlockPos neighborPos, boolean idk) {
        if(!world.isClient()) {
            BlockEntity be = world.getBlockEntity(pos);
            if(be instanceof MultiBlockEntity) {
                MultiBlockEntity multiblock = (MultiBlockEntity)be;
                boolean toggle = multiblock.neighborUpdate(neighbor, neighborPos, idk);
                if(toggle) {
                    world.setBlockState(pos, getReplacementState(state, multiblock));
                }
            }
        }
    }

    /**
     * Forwards to block entity if its states schedule ticks.
     */
    @Override
    public void onScheduledTick(BlockState state, World world, BlockPos pos, Random rand) {
        if(!world.isClient()) {
            BlockEntity be = world.getBlockEntity(pos);
            if(be instanceof MultiBlockEntity) {
                MultiBlockEntity multiblock = (MultiBlockEntity)be;
                boolean toggle = multiblock.onScheduledTick(rand);
                if(toggle) {
                    world.setBlockState(pos, getReplacementState(state, multiblock));
                }
            }
        }
    }

    /**
     * Returns the block state to place given the initial block state and the
     * block entity. The block state may not be a multiblock.
     */
    private BlockState getReplacementState(BlockState state, MultiBlockEntity be) {
        int stateCount = be.getBlockStates().size();
        if(stateCount == 0) {
            // no states in the multiblock, set to AIR
            return Blocks.AIR.getDefaultState();
        } else if(stateCount == 1) {
            // one state in the multiblock, set to that state
            return be.getBlockStates().iterator().next();
        } else {
            // toggle the multiblock state
            int parity = state.get(PARITY) ^ 1;
            return state.with(PARITY, parity).with(LIGHT, be.getLuminance());
        }
    }

    @Override
    public int getPowerLevel(BlockState state, ViewableWorld world, BlockPos pos, Direction attach, RelativeDirection side) {
        BlockEntity be = world.getBlockEntity(pos);
        if(be instanceof MultiBlockEntity) {
            return ((MultiBlockEntity)be).getPowerLevel(attach, side);
        } else {
            return 0;
        }
    }

    @Override
    public boolean canConnect(BlockState state, ViewableWorld world, BlockPos pos, Direction attach, RelativeDirection side) {
        BlockEntity be = world.getBlockEntity(pos);
        if(be instanceof MultiBlockEntity) {
            return ((MultiBlockEntity)be).canConnect(attach, side);
        } else {
            return false;
        }
    }
}
