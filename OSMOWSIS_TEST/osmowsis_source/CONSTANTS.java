import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class CONSTANTS {
    public static HashMap<String, Integer> xDIR_MAP = new HashMap<>();
    public static HashMap<String, Integer> yDIR_MAP = new HashMap<>();
    public static String[] ORIENT_LIST;

    final public static int GRASS_CODE = 1;
    final public static int FENCE_CODE = 3;
    final public static int CRASH_CODE = -1;
    final public static int OK_CODE = 1;
    final public static int NULL_CODE = -1;

    public static void initializeConstants(){
        xDIR_MAP.put("north", 0);
        xDIR_MAP.put("northeast", 1);
        xDIR_MAP.put("east", 1);
        xDIR_MAP.put("southeast", 1);
        xDIR_MAP.put("south", 0);
        xDIR_MAP.put("southwest", -1);
        xDIR_MAP.put("west", -1);
        xDIR_MAP.put("northwest", -1);

        yDIR_MAP.put("north", 1);
        yDIR_MAP.put("northeast", 1);
        yDIR_MAP.put("east", 0);
        yDIR_MAP.put("southeast", -1);
        yDIR_MAP.put("south", -1);
        yDIR_MAP.put("southwest", -1);
        yDIR_MAP.put("west", 0);
        yDIR_MAP.put("northwest", 1);

        ORIENT_LIST =new String[]{"north", "northeast", "east", "southeast", "south", "southwest", "west", "northwest"};
    }

}