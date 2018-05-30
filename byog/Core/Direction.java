package byog.Core;

public enum Direction {
    NORTH, SOUTH, EAST, WEST;


    //@Source
    public static Direction randomDirectionExcluding(Direction d) {
        int num = RandomUtils.uniform(Game.getRandom(), 0, values().length);
        Direction ret = values()[num];
        if (ret == d) {
            return randomDirectionExcluding(d);
        } else {
            return ret;
        }
    }

    public static Direction getRandomDirection() {
        return Direction.values()[RandomUtils.uniform(Game.getRandom(), 0, 4)];
    }

    public static Direction getOpposite(Direction d) {
        switch (d) {
            case EAST:
                return WEST;
            case WEST:
                return EAST;
            case NORTH:
                return SOUTH;
            default:
                return NORTH;
        }
    }

    public static Direction getValidBranchDirection(Direction d) {
        return randomDirectionExcluding(getOpposite(d));
    }

    public static Direction getRandNormDirection(Direction d) {
        int swap = RandomUtils.uniform(Game.getRandom(),0,2);
        if (d == EAST || d == WEST) {
            if (swap == 0) {
                return SOUTH;
            } else {
                return NORTH;
            }
        } else {
            if (swap == 1) {
                return EAST;
            } else {
                return WEST;
            }
        }
    }


}
