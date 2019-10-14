public class MowerStateSummary {

    private SimulationMonitor monitor;
    private LawnSquare[][] lawnMap;

    public MowerStateSummary(SimulationMonitor monitor) {
        this.monitor = monitor;
        this.lawnMap = monitor.getLawnMap();
    }

    public void finalReport() {
        int lawnSize = monitor.getReadScenario().getWidth() * monitor.getReadScenario().getHeight();
        int numCraters = 0;
        int numGrass = 0;
        for (int i = 0; i < monitor.getReadScenario().getWidth(); i++) {
            for (int j = 0; j < monitor.getReadScenario().getHeight(); j++) {
                if (lawnMap[i][j] == LawnSquare.CRATER) { numCraters++; }
                if (lawnMap[i][j] == LawnSquare.GRASS) { numGrass++; }
            }
        }
        int potentialCut = lawnSize - numCraters;
        int actualCut = potentialCut - numGrass;
        System.out.println(String.valueOf(lawnSize) + "," + String.valueOf(potentialCut) + "," + String.valueOf(actualCut) + "," + String.valueOf(monitor.getTrackTurnsCompleted()));
    }



}
