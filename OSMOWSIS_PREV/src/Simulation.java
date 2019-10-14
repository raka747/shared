import java.util.*;
import java.io.*;

public class Simulation {
    private Lawn globalLawn;
    private LawnMower lawnMower;
    private LawnMowerController controller;
    private int maxTurns;
    private int turnsCompleted;
    private Action action;

    private String trackMoveCheck;
    private String trackScanResults;
    private List<Integer> scanResults;


    public Simulation() {
        Constants.initializeConstants();
    }

    public int uploadStartingFile(String testFileName) {
        final String DELIMITER = ",";

        try {
            Scanner takeCommand = new Scanner(new File(testFileName));
            String[] tokens;

            // read in the lawn information
            tokens = takeCommand.nextLine().split(DELIMITER);
            int lawnWidth = Integer.parseInt(tokens[0]);
            tokens = takeCommand.nextLine().split(DELIMITER);
            int lawnHeight = Integer.parseInt(tokens[0]);

            globalLawn = new Lawn(lawnWidth, lawnHeight);
            globalLawn.populateLawnWithGrassAndFences();

            // read in the lawnmower starting information
            tokens = takeCommand.nextLine().split(DELIMITER);
            int numMowers = Integer.parseInt(tokens[0]);
            for (int k = 0; k < numMowers; k++) {
                tokens = takeCommand.nextLine().split(DELIMITER);
                int mowerX = Integer.parseInt(tokens[0]);
                int mowerY = Integer.parseInt(tokens[1]);
                String mowerDirection = tokens[2];
                Constants.Direction enumDirection = Constants.toDirection(mowerDirection);
                // mow the grass at the initial location
                globalLawn.addMower(mowerX, mowerY);
                lawnMower = new LawnMower(mowerX, mowerY, enumDirection,lawnWidth, lawnHeight);
            }


            // read in the crater information
            tokens = takeCommand.nextLine().split(DELIMITER);
            int numCraters = Integer.parseInt(tokens[0]);
            for (int k = 0; k < numCraters; k++) {
                tokens = takeCommand.nextLine().split(DELIMITER);
                int craterX = Integer.parseInt(tokens[0]);
                int craterY = Integer.parseInt(tokens[1]);

                // place a crater at the given location
                globalLawn.addCrater(craterX, craterY);
            }

            tokens = takeCommand.nextLine().split(DELIMITER);
            maxTurns = Integer.parseInt(tokens[0]);

            controller = new BFSController(lawnMower, globalLawn);

            takeCommand.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println();
        }
        return maxTurns;
    }

    public void pollMowerForAction() {
        // poll for next action
        action = controller.getNextAction();
        if (action.trackAction != "turn off") {
            turnsCompleted++;
        }
    }

    public boolean validateMowerAction() {
        int xOrientation, yOrientation;
        // if actino is scan
        if (action.trackAction.equals("scan")) {
            // in the case of a scan, return the information for the eight surrounding squares
            // always use a northbound orientation

            scanResults = globalLawn.getScanValues(lawnMower.getX(), lawnMower.getY());
            // update local lawn
            lawnMower.getLocalLawn().updateLawn(scanResults, lawnMower.getX(), lawnMower.getY());
            ArrayList<String> tempResults = new ArrayList<>();
            // create the string output
            for(Integer result:scanResults) {
                switch (result) {
                    case Constants.EMPTY_CODE:
                        tempResults.add("empty");
                        break;
                    case Constants.GRASS_CODE:
                        tempResults.add("grass");
                        break;
                    case Constants.CRATER_CODE:
                        tempResults.add("crater");
                        break;
                    case Constants.FENCE_CODE:
                        tempResults.add("fence");
                        break;
                    default:
                        break;
                }
            }
            trackScanResults = String.join(", ", tempResults);
            trackMoveCheck = "ok";

        } else if (action.trackAction.equals("move")) {
            // in the case of a move, ensure that the move doesn't cross craters or fences
            xOrientation = Constants.xDIR_MAP.get(lawnMower.getMowerDirection().name());
            yOrientation = Constants.yDIR_MAP.get(lawnMower.getMowerDirection().name());

            int newSquareX = lawnMower.getX() + action.trackMoveDistance * xOrientation;
            int newSquareY = lawnMower.getY() + action.trackMoveDistance * yOrientation;

            // check to make sure it is not crashed
            if (newSquareX >= 0 & newSquareX < globalLawn.getLawnWidth() & newSquareY >= 0 & newSquareY < globalLawn.getLawnHeight()) {

                // update lawn status
                if(action.trackMoveDistance > 2){
                    trackMoveCheck = "crash";
                } else {
                    trackMoveCheck = "ok";
                }

                // This will handle both steps=1 and steps=2
                if (action.trackMoveDistance >= 1) {
                    int targetX = lawnMower.getX() + xOrientation;
                    int targetY = lawnMower.getY() + yOrientation;
                    int lawnSquareOneCode = globalLawn.getLawnSquare(targetX, targetY);
                    if (lawnSquareOneCode == Constants.GRASS_CODE) {
                        lawnMower.mow(globalLawn, targetX, targetY);
                    }
                }

                if (action.trackMoveDistance == 2) {
                    int targetX = lawnMower.getX() + (2 * xOrientation);
                    int targetY = lawnMower.getY() + (2 * yOrientation);
                    int lawnSquareTwoCode = globalLawn.getLawnSquare(targetX, targetY);
                    if (lawnSquareTwoCode == Constants.GRASS_CODE) {
                        lawnMower.mow(globalLawn, targetX, targetY);
                    }
                }

                // Update the mower state
                lawnMower.setX(newSquareX);
                lawnMower.setY(newSquareY);
                lawnMower.setMowerDirection(action.trackNewDirection);
            }
            else{
                trackMoveCheck = "crash";
            }

        } else if (action.trackAction.equals("turn off")) {
            trackMoveCheck = "ok";
        }
        if (trackMoveCheck == "ok"){
            return true;
        }
        else{
            return false;
        }
    }

    public boolean isLawnComplete(){
        return action.trackAction.equals("turn off");
    }

    public void displayActionAndResponses() {
        // display the mower's actions
        System.out.print(action.trackAction);
        if (action.trackAction.equals("move")) {
            System.out.println("," + action.trackMoveDistance + "," + action.trackNewDirection);
        } else {
            System.out.println();
        }

        // display the simulation checks and/or responses
        if (action.trackAction.equals("move") | action.trackAction.equals("turn off")) {
            System.out.println(trackMoveCheck);
        } else if (action.trackAction.equals("scan")) {
            System.out.println(trackScanResults);
        }else {
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

    public void renderGlobalLawn() {
        renderLawn(globalLawn);
    }

    public void renderLocalLawn() {
        renderLawn(lawnMower.getLocalLawn());
    }

    public void renderLawn(Lawn lawn) {
        // render lawn with fences in mind
        int i, j;
        int charWidth = 2 * lawn.getLawnWidth() + 4;

        // display the rows of the lawn from top to bottom
        for (j = lawn.getLawnHeight() + 1; j >= 0; j--) {
            renderHorizontalBar(charWidth);

            // display the Y-direction identifier
            System.out.print(j);

            // display the contents of each square on this row
            for (i = 0; i < lawn.getLawnWidth() + 2; i++) {
                System.out.print("|");

                // the mower overrides all other contents
                if (i == lawnMower.getX() +1 & j == lawnMower.getY() + 1) {
                    System.out.print("M");
                } else {
                    switch (lawn.getLawnSquare(i-1,j-1)) {
                        case Constants.EMPTY_CODE:
                            System.out.print(" ");
                            break;
                        case Constants.GRASS_CODE:
                            System.out.print("g");
                            break;
                        case Constants.CRATER_CODE:
                            System.out.print("c");
                            break;
                        case Constants.NULL_CODE:
                            System.out.print("x");
                            break;
                        case Constants.FENCE_CODE:
                            System.out.print("-");
                            break;
                        default:
                            break;
                    }
                }
            }
            System.out.println("|");
        }
        renderHorizontalBar(charWidth);

        // display the column X-direction identifiers
        System.out.print(" ");
        for (i = 0; i < lawn.getLawnWidth() + 2; i++) {
            System.out.print(" " + i);
        }
        System.out.println("");

        // display the mower's direction
        System.out.println("dir: " + lawnMower.getMowerDirection().name());
        System.out.println("");
    }

    public void displayReport(){
//        System.out.println("Final Report: ");
//        System.out.println("Total number of lawn squares: " + globalLawn.getNumOfLawnSquares());
//        System.out.println("Initial number of grass: " + globalLawn.getInitialNumOfGrass());
//        System.out.println("Number of grass cut: " + lawnMower.getNumOfGrassCut());
//        System.out.println("Number of turns: " + turnsCompleted);
        System.out.println(globalLawn.getNumOfLawnSquares() + "," +globalLawn.getInitialNumOfGrass() + "," +  lawnMower.getNumOfGrassCut() + "," + turnsCompleted);
    }

}