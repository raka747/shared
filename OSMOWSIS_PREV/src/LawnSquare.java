public class LawnSquare {
    int lawnCode;
    int x;
    int y;
    public LawnSquare(int code, int x, int y){
        lawnCode = code;
        this.x = x;
        this.y = y;
    }

    // Used to uniquely identify lawn squares in Java containers
    public String id() {
        return x + ":" + y;
    }
}
