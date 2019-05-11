package io.github.kvverti.bluelightspecial.block;

import io.github.kvverti.bluelightspecial.api.FluorescentPowerSource;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntegerProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;

public class FluorescentLightBlock extends Block implements FluorescentPowerSource {

    public static final Property<Integer> POWER = IntegerProperty.create("power", 0, 15);
    public static final Property<Direction> ATTACH = DirectionProperty.create("attach", d -> true);
    public static final Property<Boolean> FORE = BooleanProperty.create("fore");
    public static final Property<Boolean> BACK = BooleanProperty.create("back");
    public static final Property<Boolean> LEFT = BooleanProperty.create("left");
    public static final Property<Boolean> RIGHT = BooleanProperty.create("right");

    public FluorescentLightBlock(Block.Settings settings) {
        super(settings);
        this.setDefaultState(this.stateFactory.getDefaultState()
            .with(POWER, 0)
            .with(ATTACH, Direction.UP)
            .with(FORE, false)
            .with(BACK, false)
            .with(LEFT, false)
            .with(RIGHT, false));
    }

    @Override
    protected void appendProperties(StateFactory.Builder<Block, BlockState> factory) {
        factory.add(POWER, ATTACH, FORE, BACK, LEFT, RIGHT);
    }

    @Override
    public int getLuminance(BlockState state) {
        return state.get(POWER) != 0 ? super.getLuminance(state) : 0;
    }

    @Override
    public boolean canPlaceAt(BlockState self, ViewableWorld world, BlockPos pos) {
        Direction dir = self.get(ATTACH);
        BlockPos offset = pos.offset(self.get(ATTACH));
        return Block.isSolidFullSquare(world.getBlockState(offset), world, offset, dir.getOpposite());
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState().with(ATTACH, ctx.getFacing().getOpposite());
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState self, Direction dir, BlockState neighbor, IWorld world, BlockPos selfPos, BlockPos neighborPos) {
        // detach if mount is removed
        if(!canPlaceAt(self, world, selfPos)) {
            return Blocks.AIR.getDefaultState();
        }
        Direction attach = self.get(ATTACH);
        // neighbor is to the "side"
        if(attach != dir && attach.getOpposite() != dir) {
            if(neighbor.getBlock() instanceof FluorescentLightBlock) {
                if(self.get(ATTACH) == neighbor.get(ATTACH)) {
                    // new fluorescent light block next to us
                    return self.with(getRelativeDirection(self, dir), true);
                }
            }
            // fluorescent light block no longer next to us
            return self.with(getRelativeDirection(self, dir), false);
        }
        return self;
    }

    @Override
    public int getPowerLevel(BlockState state, ViewableWorld world, BlockPos pos, Direction side, Direction offset) {
        if(state.get(ATTACH) != offset) {
            return 0;
        }
        Property<Boolean> relativeSide = getRelativeDirection(state, side);
        if(state.get(relativeSide)) {
            return state.get(POWER);
        } else {
            return 0;
        }
    }

    private static final Property<?>[][] PROPERTY_TABLE = {
        /*            DOWN  UP    NORTH SOUTH WEST  EAST */
        /* DOWN */  { null, null, FORE, BACK, LEFT, RIGHT },
        /* UP */    { null, null, BACK, FORE, LEFT, RIGHT },
        /* NORTH */ { BACK, FORE, null, null, LEFT, RIGHT },
        /* SOUTH */ { BACK, FORE, null, null, RIGHT, LEFT },
        /* WEST */  { BACK, FORE, RIGHT, LEFT, null, null },
        /* EAST */  { BACK, FORE, LEFT, RIGHT, null, null }
    };

    /**
     * Converts the given direction into a relative direction property
     * based on this state's attachment.
     */
    private Property<Boolean> getRelativeDirection(BlockState state, Direction dir) {
        @SuppressWarnings("unchecked")
        Property<Boolean> ret = (Property<Boolean>)PROPERTY_TABLE[state.get(ATTACH).getId()][dir.getId()];
        if(ret == null) {
            throw new IllegalArgumentException("Invalid direction: " + dir);
        }
        return ret;
    }
}
