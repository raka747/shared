import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.*;

public class ReadScenario {
    // readFile and sets lawn
    private int width;
    private int height;
    private int totalMowers;
    private LawnSquare[][] InitialLawn;
    private Integer mowerX[], mowerY[];
    private String mowerDirection[];


    private int algoNum;
    private String filepath;
    private int maxTurns;


    public ReadScenario(String filepath) {
        this.filepath = filepath;
    }

    public int getTotalMowers() {
        return totalMowers;
    }

    public int getWidth(){
        return width;
    }

    public int getHeight(){
        return height;
    }

    public int getMaxTurns() {
        return maxTurns;
    }

    public LawnSquare[][] InitializeLawn() throws FileNotFoundException {
        //String lawnPath = this.filepath;
        final String DELIMITER = ",";

        //Scanner
        Scanner takeCommand  = new Scanner(new File(this.filepath));

        String[] tokens;

        // read in the lawn information
        tokens = takeCommand.nextLine().split(DELIMITER);
        this.width = Integer.parseInt(tokens[0]);
        tokens = takeCommand.nextLine().split(DELIMITER);
        this.height = Integer.parseInt(tokens[0]);

        // generate the lawn information
        InitialLawn = new LawnSquare[this.width][this.height];

        for (int i = 0; i < this.width; i++) {
            for (int j = 0; j < this.height; j++) {
                InitialLawn[i][j] = LawnSquare.GRASS;
            }
        }

        // read in the lawnmower starting information
        tokens = takeCommand.nextLine().split(DELIMITER);
        this.totalMowers = Integer.parseInt(tokens[0]);

        mowerX = new Integer[this.totalMowers];
        mowerY = new Integer[this.totalMowers];
        mowerDirection = new String[this.totalMowers];

        for (int k = 0; k < this.totalMowers; k++) {
            tokens = takeCommand.nextLine().split(DELIMITER);

            int tempX = Integer.parseInt(tokens[0]);
            int tempY = Integer.parseInt(tokens[1]);

            //this.mowerX.put(k, tempX);
            //this.mowerY.put(k, tempY);
            //this.mowerDirection.put(k, tokens[2]);

            mowerX[k] = tempX;
            mowerY[k] = tempY;
            mowerDirection[k] = tokens[2];


            InitialLawn[tempX][tempY] = LawnSquare.CUT;
        }

        //read algorithm choice
        this.algoNum = Integer.parseInt(tokens[3]);

        // read in the crater information
        tokens = takeCommand.nextLine().split(DELIMITER);

        int numCraters = Integer.parseInt(tokens[0]);

        for (int k = 0; k < numCraters; k++) {
            tokens = takeCommand.nextLine().split(DELIMITER);

            int tempX = Integer.parseInt(tokens[0]);
            int tempY = Integer.parseInt(tokens[1]);

            // to track crater positions
            //Integer[] temp = new Integer[]{tempX, tempY};
            //temp.set(0, tempX);
            //temp.set(1, tempY);
            //this.craterPositions.set(k, temp);

            // place a crater at the given location
            InitialLawn[tempX][tempY] = LawnSquare.CRATER;
        }

        tokens = takeCommand.nextLine().split(DELIMITER);
        this.maxTurns = Integer.parseInt(tokens[0]);

        takeCommand.close();
        return this.InitialLawn;
    }

    public int getAlgoNum(){
        return algoNum;
    }

    public Integer[] getMowerX(){
        return mowerX;
    }

    public Integer[] getMowerY(){
        return mowerY;
    }

    public String[] getmowerDirection(){
        return mowerDirection;
    }


    public LawnSquare[][] getLawn() {
        LawnSquare[][] lawn = this.InitialLawn;
        return lawn;
    }
}
