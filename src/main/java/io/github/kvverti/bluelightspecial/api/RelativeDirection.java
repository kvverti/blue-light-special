package io.github.kvverti.bluelightspecial.api;

import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.Direction;

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

    public static Direction getDirection(Direction base, RelativeDirection rel) {
        RelativeDirection[] relatives = DIRECTION_LOOKUP[base.getId()];
        for(int i = 0; i < relatives.length; i++) {
            if(relatives[i] == rel) {
                return Direction.byId(i);
            }
        }
        throw new AssertionError(String.format("Failed direction pair: (%s, %s) - this should not happen", base, rel));
    }
}
