package io.github.kvverti.bluelightspecial.block;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

/**
 * A block that emits light corresponding to its redstone power level.
 */
public class CagedBulbBlock extends Block {

    public static final Property<Direction> ATTACH = FluorescentLightBlock.ATTACH;
    public static final Property<Integer> POWER = FluorescentLightBlock.POWER;

    public CagedBulbBlock(Block.Settings settings) {
        super(settings);
        this.setDefaultState(this.stateFactory.getDefaultState()
            .with(ATTACH, Direction.DOWN)
            .with(POWER, 0));
    }

    @Override
    protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
        builder.add(ATTACH, POWER);
    }

    @Override
    public int getLuminance(BlockState state) {
        return state.get(POWER);
    }

    @Override
    public boolean canPlaceAt(BlockState self, ViewableWorld world, BlockPos pos) {
        Direction dir = self.get(ATTACH);
        BlockPos offset = pos.offset(self.get(ATTACH));
        return Block.isSolidFullSquare(world.getBlockState(offset), world, offset, dir.getOpposite());
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction dir, BlockState neighbor, IWorld world, BlockPos pos, BlockPos neighborPos) {
        if(!canPlaceAt(state, world, pos)) {
            return Blocks.AIR.getDefaultState();
        }
        return state;
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block neighbor, BlockPos neighborPos, boolean idk) {
        int power = world.getReceivedRedstonePower(pos);
        world.setBlockState(pos, state.with(POWER, power));
    }
}
