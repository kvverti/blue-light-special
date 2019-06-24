package io.github.kvverti.bluelightspecial.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.IntegerProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

/**
 * An underwater plant similar to kelp that grows in deep water.
 */
public class TwistleBlock extends TwistlePlantBlock {

    public static final Property<Integer> AGE = IntegerProperty.create("age", 0, 5);

    private final Block plant;

    public TwistleBlock(Block.Settings settings, Block plant) {
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
    public BlockState getStateForNeighborUpdate(BlockState state, Direction dir, BlockState neighbor, IWorld world, BlockPos pos, BlockPos neighborPos) {
        if(!canPlaceAt(state, world, pos)) {
            return Blocks.AIR.getDefaultState();
        }
        if(dir == Direction.UP && neighbor.getBlock() == this) {
            return plant.getDefaultState();
        }
        return state;
    }

    @Override
    public void onScheduledTick(BlockState state, World world, BlockPos pos, Random rand) {
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
