package io.github.kvverti.bluelightspecial.block;

import io.github.kvverti.bluelightspecial.api.FluorescentPowerSource;
import io.github.kvverti.bluelightspecial.api.RelativeDirection;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.ViewableWorld;

public class FluorescentRepeaterBlock extends Block implements FluorescentPowerSource {

    public static final Property<Direction> ATTACH = FluorescentLightBlock.ATTACH;
    public static final Property<RelativeDirection> FACING = EnumProperty.create("facing", RelativeDirection.class);
    public static final Property<Boolean> POWERED = Properties.POWERED;

    public FluorescentRepeaterBlock(Block.Settings settings) {
        super(settings);
        this.setDefaultState(this.stateFactory.getDefaultState()
            .with(ATTACH, Direction.DOWN)
            .with(FACING, RelativeDirection.FORE)
            .with(POWERED, false));
    }

    @Override
    protected void appendProperties(StateFactory.Builder<Block, BlockState> factory) {
        factory.add(ATTACH, FACING, POWERED);
    }

    @Override
    public boolean canPlaceAt(BlockState self, ViewableWorld world, BlockPos pos) {
        Direction dir = self.get(ATTACH);
        BlockPos offset = pos.offset(dir);
        return Block.isSolidFullSquare(world.getBlockState(offset), world, offset, dir.getOpposite());
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        if(!ctx.getWorld().isClient()) {
            // properly set power value once connections are set up
            ctx.getWorld().getBlockTickScheduler().schedule(ctx.getBlockPos(), this, 1);
        }
        return getDefaultState()
            .with(ATTACH, ctx.getFacing().getOpposite())
            .with(FACING, getPlacementFacing(ctx));
    }

    private RelativeDirection getPlacementFacing(ItemPlacementContext ctx) {
        BlockPos pos = ctx.getBlockPos();
        Vec3d fpos = ctx.getPos();
        Direction facing = ctx.getFacing().getOpposite();
        // calculate which diagonal quadrant the player is looking at
        double du = 0.0, dv = 0.0;
        switch(facing) {
            case DOWN:
                du = fpos.getX() - pos.getX();
                dv = pos.getZ() - fpos.getZ() + 1;
                break;
            case UP:
                du = fpos.getX() - pos.getX();
                dv = fpos.getZ() - pos.getZ();
                break;
            case NORTH:
                du = fpos.getX() - pos.getX();
                dv = fpos.getY() - pos.getY();
                break;
            case SOUTH:
                du = pos.getX() - fpos.getX() + 1;
                dv = fpos.getY() - pos.getY();
                break;
            case WEST:
                du = pos.getZ() - fpos.getZ() + 1;
                dv = fpos.getY() - pos.getY();
                break;
            case EAST:
                du = fpos.getZ() - pos.getZ();
                dv = fpos.getY() - pos.getY();
                break;
        }
        du = Math.abs(du) - 0.5;
        dv = Math.abs(dv) - 0.5;
        double theta = Math.atan2(dv, du);
        // determine the facing state based on the diagonal quadrant
        // the block should face the direction opposite the quadrant
        final double FORTH_PI = Math.PI / 4;
        if(-FORTH_PI <= theta && theta < FORTH_PI) {
            return RelativeDirection.LEFT;
        } else if(FORTH_PI <= theta && theta < 3 * FORTH_PI) {
            return RelativeDirection.BACK;
        } else if(-3 * FORTH_PI <= theta && theta < -FORTH_PI) {
            return RelativeDirection.FORE;
        } else {
            return RelativeDirection.RIGHT;
        }
    }

    @Override
    public int getPowerLevel(BlockState state, ViewableWorld world, BlockPos pos, Direction attach, RelativeDirection side) {
        if(state.get(POWERED) && state.get(ATTACH) == attach && state.get(FACING) == side) {
            return 15;
        } else {
            return 0;
        }
    }

    @Override
    public boolean canConnect(BlockState state, ViewableWorld world, BlockPos pos, Direction attach, RelativeDirection side) {
        RelativeDirection facing = state.get(FACING);
        return state.get(ATTACH) == attach && (facing == side || facing.opposite() == side);
    }
}
