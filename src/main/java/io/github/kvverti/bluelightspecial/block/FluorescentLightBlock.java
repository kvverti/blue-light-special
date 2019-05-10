package io.github.kvverti.bluelightspecial.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.Direction;

public class FluorescentLightBlock extends Block {

    public static final Property<Boolean> LIT = Properties.LIT;
    public static final Property<Direction> FACING = Properties.FACING;
    public static final Property<Boolean> FORE = BooleanProperty.create("fore");
    public static final Property<Boolean> BACK = BooleanProperty.create("back");
    public static final Property<Boolean> LEFT = BooleanProperty.create("left");
    public static final Property<Boolean> RIGHT = BooleanProperty.create("right");

    public FluorescentLightBlock(Block.Settings settings) {
        super(settings);
        this.setDefaultState(this.stateFactory.getDefaultState()
            .with(LIT, false)
            .with(FACING, Direction.UP)
            .with(FORE, false)
            .with(BACK, false)
            .with(LEFT, false)
            .with(RIGHT, false));
    }

    @Override
    protected void appendProperties(StateFactory.Builder<Block, BlockState> factory) {
        factory.add(LIT, FACING, FORE, BACK, LEFT, RIGHT);
    }

    @Override
    public int getLuminance(BlockState state) {
        return state.get(LIT) ? super.getLuminance(state) : 0;
    }
}
