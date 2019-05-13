package io.github.kvverti.bluelightspecial.api;

import net.minecraft.block.BlockState;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ViewableWorld;

public interface FluorescentPowerSource {

    /**
     * Returns the power out of the given blockstate through and along the given
     * directions.
     * @param side the direction to test for power
     * @param attach the direction offset through which power is supplied
     */
    int getPowerLevel(BlockState state, ViewableWorld world, BlockPos pos, Direction attach, RelativeDirection side);

    /**
     * Returns whether the given block state can connect through the given side.
     * @param side the direction of connection
     * @param attach the face on which the connection is
     */
    boolean canConnect(BlockState state, ViewableWorld world, BlockPos pos, Direction attach, RelativeDirection side);

    /**
     * The relative single-plane version of Direction.
     */
    public enum RelativeDirection implements StringIdentifiable {
        BACK("back", 0),
        FORE("fore", 1),
        LEFT("left", 2),
        RIGHT("right", 3);

        private static final RelativeDirection[] opposites = { FORE, BACK, RIGHT, LEFT };

        private final String name;
        private final int id;

        private RelativeDirection(String name, int id) {
            this.name = name;
            this.id = id;
        }

        @Override
        public String asString() {
            return name;
        }

        public int id() {
            return id;
        }

        public RelativeDirection opposite() {
            return opposites[id];
        }

        @Override
        public String toString() {
            return name;
        }

        private static final RelativeDirection[][] DIRECTION_LOOKUP = {
            /*            DOWN  UP    NORTH SOUTH WEST  EAST */
            /* DOWN */  { null, null, FORE, BACK, LEFT, RIGHT },
            /* UP */    { null, null, BACK, FORE, LEFT, RIGHT },
            /* NORTH */ { BACK, FORE, null, null, LEFT, RIGHT },
            /* SOUTH */ { BACK, FORE, null, null, RIGHT, LEFT },
            /* WEST */  { BACK, FORE, RIGHT, LEFT, null, null },
            /* EAST */  { BACK, FORE, LEFT, RIGHT, null, null }
        };

        /**
         * Converts the given direction into a relative direction
         * based on a base direction. The directions must be orthogonal.
         * @throws IllegalArgumentException if base and dir are not orthogonal
         */
        public static RelativeDirection getRelativeDirection(Direction base, Direction dir) {
            RelativeDirection ret = DIRECTION_LOOKUP[base.getId()][dir.getId()];
            if(ret == null) {
                throw new IllegalArgumentException(String.format("Invalid direction pair: (%s, %s)", base, dir));
            }
            return ret;
        }
    }
}
