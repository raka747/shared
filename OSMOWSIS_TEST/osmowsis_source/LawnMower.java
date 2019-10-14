import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LawnMower {

    private int lawnMowerId;

    private Direction currentDirection;

    private int currentX;

    private int currentY;

    private  int status;

    private List<String> ScanResults;

    private LawnSquare[][] lawn;

    private ArrayList<String> prevScanInfo;

    public LawnMower(int lawnMowerId, Direction currentDirection, int currentX, int currentY, int mowerStatus) { //int currentTurnCount
        this.lawnMowerId = lawnMowerId;
        this.currentDirection = currentDirection;
        this.currentX = currentX;
        this.currentY = currentY;
        this.status = mowerStatus;
    }

    public void updateScanResults(List<String> ScanResults) {
        this.ScanResults = ScanResults;
        prevScanInfo = new ArrayList<>();

        for (String result : ScanResults) {
            prevScanInfo.add(result);
        }
    }

    public void setCurrentDirection(Direction d){
        this.currentDirection = d;
    }

    public void setCurrentX(int x){
        this.currentX = x;
    }

    public void setCurrentY(int y){
        this.currentY = y;
    }

    public ArrayList<String> getPrevScanInfo(){
        return prevScanInfo;
    }

    public int getCurrentX(){
        return this.currentX;
    }

    public int getCurrentY(){
        return this.currentY;
    }

    public Direction getcurrentDirection(){
        return this.currentDirection;
    }
    public String IdentifyValidMowerAction(){ //returns action - can be used to update status

        //LawnSquare[][] lawnWithNeighbors = new CurrentMowerNeighbors().getLawnWithNeighbors();

        if (true) {
            return "move";
        }else if (false) {
            return "scan";
        }else if (true && false){
            return "steer";
        }else {
            return "pass";
        }
    }
    public void move(){
        // uses scanLawn
        // uses isValidMove
        // update turnsCount, updates Simulation
    }

    public void isValidAction(){

    }

    public void steer(){

    }
    public void scan(){
        //check 8 neighbors
        //
        //lawn =
    }

    public void pass(){

    }

    public int getLawnMowerId() {
        return lawnMowerId;
    }

    //public

}
