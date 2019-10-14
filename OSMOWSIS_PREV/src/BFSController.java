import java.util.*;

public class BFSController implements LawnMowerController {
    LawnMower lawnMower;
    Lawn globalLawn;

    public BFSController(LawnMower lawnMower, Lawn globalLawn){
        this.lawnMower = lawnMower;
        this.globalLawn = globalLawn;
    }

    // prints all not yet visited vertices reachable from s
    public Action getNextAction()
    {
        Action action = new Action();
        if (existsUnexploredSquare()){
            action.trackAction = "scan";
            return action;
        }

        // If there is grass in the squares in front of the mower - immediately mow them
        if (checkNearestSameDirectionNeighbor(lawnMower.getX(), lawnMower.getY())) {
            action.trackAction = "move";
            action.trackMoveDistance = 1;
            action.trackNewDirection = lawnMower.getMowerDirection();

            // Check if we can mow two spots
            int nearestNeighborX = lawnMower.getX() + Constants.xDIR_MAP.get(lawnMower.getMowerDirection().name());
            int nearestNeighborY = lawnMower.getY() + Constants.yDIR_MAP.get(lawnMower.getMowerDirection().name());
            if (checkNearestSameDirectionNeighbor(nearestNeighborX, nearestNeighborY)){
                action.trackMoveDistance = 2;
            }

            return action;
        }

        // Otherwise search for the nearest grass square
        LawnSquare mowerSquare = new LawnSquare(Constants.EMPTY_CODE,lawnMower.getX(), lawnMower.getY());
        SearchResult result = searchForNearestAnyDirectionNeighbor(mowerSquare);
        if (result != null && !mowerSquare.id().equals(result.destinationSquare.id())){
            LawnSquare previousPreviousSquare = result.destinationSquare;
            LawnSquare previousSquare = result.neighborList.get(result.destinationSquare.id());
            while (!previousSquare.id().equals(mowerSquare.id())) {
                previousPreviousSquare = previousSquare;
                previousSquare = result.neighborList.get(previousSquare.id());
            }

            return determineAction(previousPreviousSquare, lawnMower.getX(),lawnMower.getY(), action);
        }
        else{
            // else we have finished the entire lawn, turn off lawnmower
            action.trackAction = "turn off";
            return action;
        }
    }

    public Action determineAction(LawnSquare destinationSquare, int mowerX, int mowerY, Action action){
        // determine how we should move towards closest grass square and what kind of direction we should be taking
        int differenceX = destinationSquare.x - mowerX;
        int differenceY = destinationSquare.y - mowerY;
        List<String> directionOptionsX = new ArrayList<>();
        List<String> directionOptionsY = new ArrayList<>();
        for(String key:Constants.xDIR_MAP.keySet()){
            if (Constants.xDIR_MAP.get(key) == differenceX){
                directionOptionsX.add(key);
            }
        }
        for(String key:Constants.yDIR_MAP.keySet()){
            if (Constants.yDIR_MAP.get(key) == differenceY){
                directionOptionsY.add(key);
            }
        }
        List<String> commonList = new ArrayList<>(directionOptionsX);
        commonList.retainAll(directionOptionsY);
        String direction = commonList.get(0);

        Constants.Direction enumDirection = Constants.toDirection(direction);
        action.trackNewDirection = enumDirection;
        if (lawnMower.getMowerDirection() == enumDirection){
            // TODO: Can't you perform the next rotation here...
            action.trackMoveDistance = 1;
        } else {
            action.trackMoveDistance = 0;
        }
        action.trackAction = "move";
        return action;
    }

    public boolean existsUnexploredSquare(){
        List<Integer> scanValues = lawnMower.getLocalLawn().getScanValues(lawnMower.getX(), lawnMower.getY());
        for(Integer scanValue:scanValues){
            if (scanValue == Constants.NULL_CODE){
                return true;
            }
        }
        return false;
    }

    public boolean checkNearestSameDirectionNeighbor(int x, int y){
        // checking if the neighbor in the same direction is mowable
        Constants.Direction direction = lawnMower.getMowerDirection();
        int nearestNeighborX = x + Constants.xDIR_MAP.get(direction.name());
        int nearestNeighborY = y + Constants.yDIR_MAP.get(direction.name());
        int checkValue = lawnMower.getLocalLawn().getLawnSquare(nearestNeighborX, nearestNeighborY);
        return checkValue == Constants.GRASS_CODE;
    }

    public SearchResult searchForNearestAnyDirectionNeighbor(LawnSquare mowerSquare){
        // using bfs to search for the nearest neighbor in the lawn to the lawnmower
        LinkedList<LawnSquare> queue = new LinkedList<>();
        queue.add(mowerSquare);
        HashMap<String,LawnSquare> neighborList = new HashMap<>();
        SearchResult search = new SearchResult();
        while(!queue.isEmpty()) {
            LawnSquare item = queue.pop();

            if (item.lawnCode == Constants.GRASS_CODE){
                search.destinationSquare = item;
                search.neighborList = neighborList;
                return search;
            }

            ListIterator<LawnSquare> itr = lawnMower.getLocalLawn().getScanValuesInLawnSquares(item.x, item.y).listIterator();
            while (itr.hasNext()) {
                LawnSquare scannedSquare = itr.next();
                // only add squares that are traversable by the lawnmower
                if (scannedSquare.lawnCode == Constants.EMPTY_CODE || scannedSquare.lawnCode == Constants.GRASS_CODE) {
                    if (!neighborList.containsKey(scannedSquare.id())) {
                        queue.add(scannedSquare);
                        neighborList.put(scannedSquare.id(), item);
                    }
                }
            }
        }
        return null;
    }
}