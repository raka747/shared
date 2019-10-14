import java.util.List;

public class LawnMower {
    private Lawn localLawn;
    private int x;
    private int y;
    private Constants.Direction mowerDirection;
    private int numOfGrassCut = 0;

    public Lawn getLocalLawn() {
        return localLawn;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getNumOfGrassCut() {
        return numOfGrassCut;
    }

    public Constants.Direction getMowerDirection() {
        return mowerDirection;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }


    public void setMowerDirection(Constants.Direction mowerDirection) {
        this.mowerDirection = mowerDirection;
    }

    public LawnMower(int initialX, int initialY, Constants.Direction initialDirection, int lawnWidth, int lawnHeight){
        x = initialX;
        y = initialY;
        mowerDirection = initialDirection;
        // The square where the mower is initialized is counted as being mowed.
        numOfGrassCut = 1;
        localLawn = new Lawn(lawnWidth, lawnHeight);
        localLawn.getLawnMap()[x][y] = Constants.EMPTY_CODE;
    }

    public void mow(Lawn globalLawn, int x, int y) {
        if (globalLawn.getLawnSquare(x, y) == Constants.GRASS_CODE) {
            globalLawn.replaceWithEmpty(x, y);
            localLawn.replaceWithEmpty(x, y);
            numOfGrassCut++;
        }
    }
}
