import java.util.HashMap;

public class SearchResult {
    LawnSquare destinationSquare;
    // Key is the LawnSquare id
    // TODO: It would be nicer to just provide a list representing the path...
    HashMap<String,LawnSquare> neighborList= new HashMap<>();
}
