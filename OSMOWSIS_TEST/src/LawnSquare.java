public enum LawnSquare {
    CUT(0),
    GRASS(1),
    FENCE(3),
    CRATER(2),
    MOWER(5);

    private int square;

    LawnSquare(int i) {
        this.square = i;
    }


    public static String getStateByCode(LawnSquare s) {
        switch (s) {
            case CUT:
                return "empty";
            case GRASS:
                return "grass";
            case FENCE:
                return "fence";
            case CRATER:
                return "crater";
        }
        return "unknown";
    }


}
