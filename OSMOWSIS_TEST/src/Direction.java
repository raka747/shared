//package academy.learnprogramming;

public enum Direction {
    EAST("east"),
    NORTHEAST("northeast"),
    SOUTHEAST("southeast"),
    WEST("west"),
    NORTHWEST("northwest"),
    SOUTHWEST("southwest"),
    NORTH("north"),
    SOUTH("south");

    private String direction;

    Direction(String direction) {
        this.direction = direction;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public static Direction toDirection(String mowerDirection){
        Direction tempDirection = Direction.NORTH;
        for (Direction k : Direction.values()) {
            if (k.name().toLowerCase().equals(mowerDirection.toLowerCase())) {
                tempDirection = k;
            }
        }
        return tempDirection;
    }

    public static String getStateByCode(Direction d) {
        switch (d) {
            case EAST:
                return "east";
            case NORTHEAST:
                return "northeast";
            case SOUTHEAST:
                return "southeast";
            case WEST:
                return "west";
            case NORTHWEST:
                return "northwest";
            case SOUTHWEST:
                return "southwest";
            case NORTH:
                return "north";
            case SOUTH:
                return "south";
        }
        return "";
    }
}
