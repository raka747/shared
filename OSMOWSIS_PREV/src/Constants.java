import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class Constants {
    public static HashMap<String, Integer> xDIR_MAP = new HashMap<>();
    public static HashMap<String, Integer> yDIR_MAP = new HashMap<>();

    // TODO: Better name
    public static ArrayList<String> scanORDER;
    final public static int EMPTY_CODE = 0;
    final public static int GRASS_CODE = 1;
    final  public static int CRATER_CODE = 2;
    final public static int FENCE_CODE = 3;
    final public static int NULL_CODE = -1;

    enum Direction {
        North, Northeast, Northwest, South, Southeast, Southwest, East, West, Null
    }

    public static void initializeConstants(){
        xDIR_MAP.put("North", 0);
        xDIR_MAP.put("Northeast", 1);
        xDIR_MAP.put("East", 1);
        xDIR_MAP.put("Southeast", 1);
        xDIR_MAP.put("South", 0);
        xDIR_MAP.put("Southwest", -1);
        xDIR_MAP.put("West", -1);
        xDIR_MAP.put("Northwest", -1);

        yDIR_MAP.put("North", 1);
        yDIR_MAP.put("Northeast", 1);
        yDIR_MAP.put("East", 0);
        yDIR_MAP.put("Southeast", -1);
        yDIR_MAP.put("South", -1);
        yDIR_MAP.put("Southwest", -1);
        yDIR_MAP.put("West", 0);
        yDIR_MAP.put("Northwest", 1);

        scanORDER = new ArrayList(
                List.of("North", "Northeast", "East", "Southeast", "South", "Southwest", "West", "Northwest"));
    }

    public static Direction toDirection(String mowerDirection){
        Direction enumDirection = Direction.Null;
        for (Direction d : Direction.values()) {
            if (d.name().toLowerCase().equals(mowerDirection.toLowerCase())) {
                enumDirection = d;
            }
        }
        return enumDirection;
    }
}