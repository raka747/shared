import javax.print.DocFlavor;
import java.util.ArrayList;
import java.util.List;

public class Lawn {
    private Integer[][] lawnMap;
    private int lawnWidth;
    private int initialNumOfGrass = 0;
    private int numOfLawnSquares;
    private int lawnHeight;

    public Integer[][] getLawnMap() {
        return lawnMap;
    }

    public int getLawnWidth() {
        return lawnWidth;
    }

    public int getLawnHeight() {
        return lawnHeight;
    }

    public int getInitialNumOfGrass() {
        return initialNumOfGrass;
    }

    public int getNumOfLawnSquares() {
        return numOfLawnSquares;
    }

    public Lawn(int width, int height) {
        lawnHeight = height;
        lawnWidth = width;
        lawnMap = new Integer[width + 2][height + 2];
        // Initialize the lawn to null
        for (int i = 0; i < width + 2; i++) {
            for (int j = 0; j < height + 2; j++) {
                lawnMap[i][j] = Constants.NULL_CODE;
            }
        }
        numOfLawnSquares = lawnHeight*lawnWidth;
    }

    public List<Integer> getScanValues(int x, int y){
        List<Integer> scanValues = new ArrayList<>();
        // Align to fence coordinates
        int row = x+1;
        int col = y+1;

        for (String key : Constants.scanORDER) {
            scanValues.add(lawnMap[row + Constants.xDIR_MAP.get(key)][col + Constants.yDIR_MAP.get(key)]);
        }
        return scanValues;
    }

    public List<LawnSquare> getScanValuesInLawnSquares(int x, int y){
        List<LawnSquare> scanValues = new ArrayList<>();
        // Align to fence coordinates
        int row = x+1;
        int col = y+1;
        for (String key : Constants.scanORDER) {
            int X = row + Constants.xDIR_MAP.get(key);
            int Y = col + Constants.yDIR_MAP.get(key);
            scanValues.add(new LawnSquare(lawnMap[X][Y],X - 1, Y - 1));  //north
        }
        return scanValues;
    }

    public void populateLawnWithGrassAndFences(){
        // generate the lawn information
        for (int i = 0; i < lawnWidth+2; i++) {
            for (int j = 0; j < lawnHeight+2; j++) {
                if (i == 0 || j == 0 || i == lawnWidth+1 || j == lawnHeight+1){
                    lawnMap[i][j] = Constants.FENCE_CODE;
                }
                else {
                    lawnMap[i][j] = Constants.GRASS_CODE;
                    initialNumOfGrass++;
                }
            }
        }
    }

    public void updateLawn(List<Integer> scanValues, int x, int y){
        int row = x+1;
        int col = y+1;
        for (String key : Constants.scanORDER) {
            lawnMap[row + Constants.xDIR_MAP.get(key)][col + Constants.yDIR_MAP.get(key)] = scanValues.get(Constants.scanORDER.indexOf(key));
        }
    }

    public void addMower(int mowerX, int mowerY){
        // mow the grass at the initial location
        lawnMap[mowerX + 1][mowerY + 1] = Constants.EMPTY_CODE;
    }

    public void addCrater(int craterX, int craterY){
        // mow the grass at the initial location
        lawnMap[craterX + 1][craterY + 1] = Constants.CRATER_CODE;
        initialNumOfGrass--;
    }

    public Integer getLawnSquare(int x, int y){
        return lawnMap[x+1][y+1];
    }

    public void replaceWithEmpty(int x, int y){
        lawnMap[x+1][y+1] = Constants.EMPTY_CODE;
    }
}
