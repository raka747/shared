import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class CuttingAlgorithm {

    private LawnSquare[][] lawnMap;
    private LawnMower currentMower;
    private int id;

    private static Random randGenerator;
    private String trackAction;
    private String trackNewDirection;
    private String[] newActionDirection;
    private ArrayList<String> prevScan;
    private Direction direction;
    int xOrientation, yOrientation;
    private int width;
    private int height;
    private ReadScenario readScenario;
    private List<String> prevScanInfo;

    public CuttingAlgorithm(int lawnMowerId, LawnMower lawnMower, ArrayList<String> scanResults, ReadScenario readScenario, LawnSquare[][] lawnMap) {
        this.currentMower = lawnMower;
        this.id = lawnMowerId;
        this.prevScan = scanResults;
        this.width = readScenario.getWidth();
        this.height = readScenario.getHeight();
        this.lawnMap = lawnMap;
        this.readScenario = readScenario;
        this.prevScanInfo = scanResults;
    }

    public CuttingAlgorithm(int lawnMowerId, LawnMower lawnMower) {
        this.currentMower = lawnMower;
        this.id = lawnMowerId;
    }

    public String[] getAction() {

        List<String> prevScanInfo = currentMower.getPrevScanInfo();

        String temp_dir = Direction.getStateByCode(currentMower.getcurrentDirection());

        int nearX = currentMower.getCurrentX() + CONSTANTS.xDIR_MAP.get(temp_dir);

        int nearY = currentMower.getCurrentY() + CONSTANTS.yDIR_MAP.get(temp_dir);

        if (!(nearX < 0 || nearX >= width || nearY < 0 || nearY >= height)) {

            LawnSquare squareVal = lawnMap[nearX][nearY];

            if (squareVal == LawnSquare.GRASS) {
                return new String[]{"move"};
            }
        }

        int moveRandomChoice;
        randGenerator = new Random();
        moveRandomChoice = randGenerator.nextInt(120);

        if (moveRandomChoice < 25) {
            // check your surroundings
            trackAction = "scan";
        } else if (moveRandomChoice < 50) {
            // change direction
            trackAction = "steer";
        } else {
            // move forward
            trackAction = "move";
            direction = currentMower.getcurrentDirection();
            String temp_direction = "";

            try {
                for (int i = 0; i < prevScanInfo.size(); i++) {
                    if (prevScanInfo.get(i) == "grass"){
                        temp_direction = CONSTANTS.ORIENT_LIST[i];
                    }
                }

            } catch (NullPointerException e){

            }

            xOrientation = CONSTANTS.xDIR_MAP.get(Direction.getStateByCode(direction));
            yOrientation = CONSTANTS.yDIR_MAP.get(Direction.getStateByCode(direction));

            int newSquareX = currentMower.getCurrentX() + xOrientation;
            int newSquareY = currentMower.getCurrentY() + yOrientation;

            if (newSquareX < 0 || newSquareX >= width || newSquareY < 0 || newSquareY >= height) {
                trackAction = "steer";
            } else if (lawnMap[newSquareX][newSquareY] == LawnSquare.CRATER) {
                trackAction = "steer";
            }

        }

        // determine a new direction

        String temp_direction = "";

        try {
            for (int i = 0; i < prevScanInfo.size(); i++) {
                if (prevScanInfo.get(i) == "grass"){
                    temp_direction = CONSTANTS.ORIENT_LIST[i];
                }
            }

        } catch (NullPointerException e){

        }

        moveRandomChoice = randGenerator.nextInt(100);
        if (trackAction.equals("steer") && moveRandomChoice < 80) {
            int ptr = 0;

            String direction = Direction.getStateByCode(currentMower.getcurrentDirection());

            while (ptr < CONSTANTS.ORIENT_LIST.length && !direction.equals(CONSTANTS.ORIENT_LIST[ptr])) {
                ptr++;
            }

            trackNewDirection = CONSTANTS.ORIENT_LIST[(ptr + 1) % CONSTANTS.ORIENT_LIST.length];

        } else {
            trackNewDirection = Direction.getStateByCode(currentMower.getcurrentDirection());//mowerDirection[id];
        }

        if (trackAction.equals("steer")) {
            newActionDirection = new String[]{trackAction, trackNewDirection};
            return newActionDirection;
        } else {
            newActionDirection = new String[]{trackAction};
            return newActionDirection;
        }
    }


    public String[] getRandomAction() {

        int moveRandomChoice;

        randGenerator = new Random();

        moveRandomChoice = randGenerator.nextInt(100);

        if (moveRandomChoice < 10) {
            // do nothing
            trackAction = "pass";
        } else if (moveRandomChoice < 35) {
            // check your surroundings
            trackAction = "scan";
        } else if (moveRandomChoice < 60) {
            // change direction
            trackAction = "steer";
        } else {
            // move forward
            trackAction = "move";
        }

        // determine a new direction
        moveRandomChoice = randGenerator.nextInt(100);
        if (trackAction.equals("steer") && moveRandomChoice < 85) {
            int ptr = 0;
            String direction = Direction.getStateByCode(currentMower.getcurrentDirection());
            while (ptr < CONSTANTS.ORIENT_LIST.length && !direction.equals(CONSTANTS.ORIENT_LIST[ptr])) {
                ptr++;
            }
            trackNewDirection = CONSTANTS.ORIENT_LIST[(ptr + 1) % CONSTANTS.ORIENT_LIST.length];
        } else {
            trackNewDirection = Direction.getStateByCode(currentMower.getcurrentDirection());//mowerDirection[id];
        }

        if (trackAction.equals("steer")) {
            newActionDirection = new String[]{trackAction, trackNewDirection};
            return newActionDirection;
        } else {
            newActionDirection = new String[]{trackAction};
            return newActionDirection;
        }
    }

}
