package io.github.kvverti.bluelightspecial.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.IntegerProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.Lazy;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.LightType;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

/**
 * An underwater plant similar to kelp that grows in deep water.
 */
public class TwistleBlock extends AbstractTwistleBlock {

    public static final Property<Integer> AGE = IntegerProperty.create("age", 0, 5);
    private static final VoxelShape shape = Block.createCuboidShape(3.0, 0.0, 3.0, 13.0, 7.0, 13.0);

    private final Lazy<Block> plant;

    public TwistleBlock(Block.Settings settings, Lazy<Block> plant) {
        super(settings);
        this.plant = plant;
        this.setDefaultState(this.stateFactory.getDefaultState()
            .with(AGE, 0));
    }

    @Override
    protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, EntityContext ctx) {
        return shape;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction dir, BlockState neighbor, IWorld world, BlockPos pos, BlockPos neighborPos) {
        if(!canPlaceAt(state, world, pos)) {
            return Blocks.AIR.getDefaultState();
        }
        if(dir == Direction.UP && neighbor.getBlock() == this) {
            return plant.get().getDefaultState();
        }
        return state;
    }

    @Override
    public void onScheduledTick(BlockState state, World world, BlockPos pos, Random rand) {
        if(isSuitableFluidPos(state, world, pos)) {
            int age = state.get(AGE);
            if(age == 5) {
                if(this.canPlaceAt(state, world, pos.up())) {
                    world.setBlockState(pos.up(), this.getDefaultState());
                }
            } else {
                world.setBlockState(pos, state.cycle(AGE));
            }
        }
    }

    /**
     * Returns whether the position is suitable fluid-wise.
     */
    private boolean isSuitableFluidPos(BlockState state, ViewableWorld world, BlockPos pos) {
        // must be placed in water and in low light
        return world.getFluidState(pos).getFluid() == this.getFluidState(state).getFluid() &&
            world.getLightLevel(LightType.SKY, pos) <= 3;
    }
}
