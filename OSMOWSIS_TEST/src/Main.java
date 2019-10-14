
import java.io.FileNotFoundException;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {
        Boolean showState = Boolean.FALSE;

        // check for the test scenario file name
        if (args.length < 1) {
            System.out.println("ERROR: Test scenario file name not found.");
            return;
        }

        if (args.length >= 2 && (args[1].equals("-v") || args[1].equals("-verbose"))) { showState = Boolean.FALSE; }

        ReadScenario readScenario = new ReadScenario(args[0]);
        SimulationMonitor monitorSim = new SimulationMonitor(readScenario);

        // run the simulation for a fixed number of steps
        monitorSim.runSimulation(showState);

        MowerStateSummary summary = new MowerStateSummary(monitorSim);
        summary.finalReport();
    }
}

