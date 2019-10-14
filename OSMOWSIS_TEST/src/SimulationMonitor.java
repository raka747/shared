import java.io.FileNotFoundException;
import java.util.*;

public class SimulationMonitor {
    private int strategy;
    private int maxTurns;
    private int turnsLeft, trackTurnsCompleted;
    public static HashMap<Integer, LawnMower> lawnMowers = new HashMap<>();
    private LawnSquare[][] lawnMap;
    private MowerStateSummary stateSummary;
    private ReadScenario readScenario;
    private CuttingAlgorithm cutAlgo;
    private String[] ActionDirection;
    private String trackAction;

    private Integer mowerX[], mowerY[];
    private String mowerDirection[];
    private String trackNewDirection;
    private String trackScanResults, trackMoveCheck;
    private Integer mowerStatus[];
    private int width;
    private int height;


    public SimulationMonitor(ReadScenario readScenario) throws FileNotFoundException {
        this.readScenario = readScenario;
        this.lawnMap = readScenario.InitializeLawn();
        this.maxTurns = readScenario.getMaxTurns();
        this.mowerX = readScenario.getMowerX();
        this.mowerY = readScenario.getMowerY();
        this.mowerDirection = readScenario.getmowerDirection();
        CONSTANTS.initializeConstants();
        this.maxTurns = readScenario.getMaxTurns();
        this.mowerStatus = new Integer[readScenario.getTotalMowers()];
    }

    public int getMaxTurns(){
        return maxTurns;
    }

    public ReadScenario getReadScenario(){
        return readScenario;
    }

    public LawnSquare[][] getLawnMap(){
        return lawnMap;
    }

    public int getTrackTurnsCompleted(){
        return trackTurnsCompleted;
    }

    public void pollMowerForAction(int id, LawnMower currentMower) {

        int strategy = readScenario.getAlgoNum();
        if (strategy > 0) {
            cutAlgo = new CuttingAlgorithm(id, currentMower, currentMower.getPrevScanInfo(), readScenario, lawnMap);
            ActionDirection = cutAlgo.getAction();
        }else {
            cutAlgo = new CuttingAlgorithm(id, currentMower);
            ActionDirection = cutAlgo.getRandomAction();

        }
        if (ActionDirection.length > 1) {
            trackAction = ActionDirection[0];
            trackNewDirection = ActionDirection[1];
        } else {
            trackAction = ActionDirection[0];
        }
    }

    public void validateMowerAction(int id, LawnMower currentMower) {

        //int id = currentMower.getLawnMowerId();

        int xOrientation, yOrientation;

        if (trackAction.equals("scan")) {
            // in the case of a scan, return the information for the eight surrounding squares
            // always use a northbound orientation
            trackScanResults = scanAroundSquare(mowerX[id], mowerY[id]);
            List<String> scanList = new ArrayList<String>(Arrays.asList(trackScanResults.split(" , ")));
            lawnMowers.get(id).updateScanResults(scanList);
            trackMoveCheck = "ok";

        } else if (trackAction.equals("pass")) {
            trackMoveCheck = "ok";

        } else if (trackAction.equals("steer")) {
            mowerDirection[id] = trackNewDirection;
            currentMower.setCurrentDirection(Direction.toDirection(trackNewDirection));
            trackMoveCheck = "ok";

        } else if (trackAction.equals("move")) {
            // in the case of a move, ensure that the move doesn't cross craters or fences
            //System.out.println("xxxx" + String.valueOf(CONSTANTS.xDIR_MAP.get(mowerDirection[id])));
            xOrientation = CONSTANTS.xDIR_MAP.get(mowerDirection[id]);
            yOrientation = CONSTANTS.yDIR_MAP.get(mowerDirection[id]);

            int newSquareX = mowerX[id] + xOrientation;
            int newSquareY = mowerY[id] + yOrientation;

            if (newSquareX < 0 || newSquareX >= readScenario.getWidth() || newSquareY < 0 || newSquareY >= readScenario.getHeight()) {
                // mower hit a fence
                mowerStatus[id] = CONSTANTS.CRASH_CODE;
                trackMoveCheck = "crash";
            } else if (lawnMap[newSquareX][newSquareY] == LawnSquare.CRATER) {
                // mower hit a crater
                mowerStatus[id] = CONSTANTS.CRASH_CODE;
                trackMoveCheck = "crash";
            } else if ((readScenario.getTotalMowers() > 1) && (newSquareX == mowerX[1 - id] && newSquareY == mowerY[1 - id])) {
                // mower collided with the other mower
                mowerStatus[id] = CONSTANTS.CRASH_CODE;
                mowerStatus[1 - id] = CONSTANTS.CRASH_CODE;
                trackMoveCheck = "crash";
            } else {
                // mower move is successful
                mowerX[id] = newSquareX;
                mowerY[id] = newSquareY;
                // update lawn status
                lawnMap[newSquareX][newSquareY] = LawnSquare.CUT;
                //update lawnmower
                currentMower.setCurrentX(newSquareX);
                currentMower.setCurrentY(newSquareY);
                trackMoveCheck = "ok";
            }
        } else {
            mowerStatus[id] = CONSTANTS.CRASH_CODE;
            trackMoveCheck = "crash";
        }
    }


    public String scanAroundSquare(int targetX, int targetY) {
        String nextSquare, resultString = "";

        for (int k = 0; k < CONSTANTS.ORIENT_LIST.length; k++) {
            String lookThisWay = CONSTANTS.ORIENT_LIST[k];
            int offsetX = CONSTANTS.xDIR_MAP.get(lookThisWay);
            int offsetY = CONSTANTS.yDIR_MAP.get(lookThisWay);

            int checkX = targetX + offsetX;
            int checkY = targetY + offsetY;

            if (checkX < 0 || checkX >= readScenario.getWidth() || checkY < 0 || checkY >= readScenario.getHeight()) {
                nextSquare = "fence";
            } else if (mowerStatus[0] == CONSTANTS.OK_CODE && checkX == mowerX[0] && checkY == mowerY[0]) {
                nextSquare = "mower";
            } else if ( (readScenario.getTotalMowers() > 1) && (mowerStatus[1] == CONSTANTS.OK_CODE && checkX == mowerX[1] && checkY == mowerY[1])) {
                nextSquare = "mower";
            } else {
                nextSquare = LawnSquare.getStateByCode(lawnMap[checkX][checkY]);
            }

            if (resultString.isEmpty()) { resultString = nextSquare; }
            else { resultString = resultString + "," + nextSquare; }
        }

        return resultString;
    }


    public void runSimulation(Boolean showState){

        for(int mowerN = 0; mowerN < readScenario.getTotalMowers(); mowerN++){
            Direction startDirection = Direction.toDirection(mowerDirection[mowerN]);
            mowerStatus[mowerN] = CONSTANTS.OK_CODE;
            lawnMowers.put(mowerN, new LawnMower(mowerN, startDirection, mowerX[mowerN], mowerY[mowerN], mowerStatus[mowerN]));
        }

        // run the simulation for a fixed number of steps
        for(int turns = 0; turns < maxTurns; turns++) {
            this.trackTurnsCompleted = turns;

            if (mowersAllStopped()) { break; }

            for (int k = 0; k < readScenario.getTotalMowers(); k++) {

                if (mowerStopped(k)) { continue; }

                pollMowerForAction(k, lawnMowers.get(k));
                try {
                    validateMowerAction(k, lawnMowers.get(k));
                } catch (Exception e){
                    System.out.println(trackAction);
                }
                displayActionAndResponses(k);

                // render the state of the lawn after each command
                if (showState) { renderLawn(); }
            }
        }

    }

    public void displayActionAndResponses(int id) {
        // display the mower's actions
        System.out.print("m" + String.valueOf(id) + "," + trackAction);
        if (trackAction.equals("steer")) {
            System.out.println("," + trackNewDirection);
        } else {
            System.out.println();
        }

        // display the simulation checks and/or responses
        if (trackAction.equals("move") || trackAction.equals("steer") || trackAction.equals("pass")) {
            System.out.println(trackMoveCheck);
        } else if (trackAction.equals("scan")) {
            System.out.println(trackScanResults);
        } else {
            System.out.println("action not recognized");
        }
    }

    private void renderHorizontalBar(int size) {
        System.out.print(" ");
        for (int k = 0; k < size; k++) {
            System.out.print("-");
        }
        System.out.println("");
    }

    public void renderLawn() {
        int i, j;
        int charWidth = 2 * readScenario.getWidth() + 2;

        // display the rows of the lawn from top to bottom
        for (j = readScenario.getHeight() - 1; j >= 0; j--) {
            renderHorizontalBar(charWidth);

            // display the Y-direction identifier
            System.out.print(j);

            // display the contents of each square on this row
            for (i = 0; i < readScenario.getWidth(); i++) {
                System.out.print("|");

                // the mower overrides all other contents
                if (mowerStatus[0] == CONSTANTS.OK_CODE && i == mowerX[0] && j == mowerY[0]) {
                    System.out.print("0");
                } else if (mowerStatus[1] == CONSTANTS.OK_CODE && i == mowerX[1] && j == mowerY[1]) {
                    System.out.print("1");
                } else {
                    String square_stat = LawnSquare.getStateByCode(lawnMap[i][j]);
                    System.out.print(square_stat);
                }
            }
            System.out.println("|");
        }
        renderHorizontalBar(charWidth);

        // display the column X-direction identifiers
        System.out.print(" ");
        for (i = 0; i < readScenario.getWidth(); i++) {
            System.out.print(" " + i);
        }
        System.out.println("");

        // display the mower's directions
        for(int k = 0; k < readScenario.getTotalMowers(); k++) {
            if (mowerStatus[k] == CONSTANTS.CRASH_CODE) { continue; }
            System.out.println("dir m" + String.valueOf(k) + ": " + mowerDirection[k]);
        }
        System.out.println("");
    }

    public Boolean mowersAllStopped() {
        for(int k = 0; k < readScenario.getTotalMowers(); k++) {
            int temp = mowerStatus.length;
            if (mowerStatus[k] == CONSTANTS.OK_CODE) { return Boolean.FALSE; }
        }
        return Boolean.TRUE;
    }

    public Boolean mowerStopped(int id) {
        return mowerStatus[id] == CONSTANTS.CRASH_CODE;
    }


}
