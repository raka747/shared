public class Main {
    public static void main(String[] args) {
        Simulation monitorSim = new Simulation();
        boolean isValid;
        System.out.println("Working Directory = " +
                System.getProperty("user.dir"));
        // check for the test scenario file name
        if (args.length == 0) {
            System.out.println("ERROR: Test scenario file name not found.");
        } else {
            int maxTurns = monitorSim.uploadStartingFile(args[0]);

            // run the simulation for a fixed number of steps
            for(int turns = 0; turns < maxTurns; turns++) {
                monitorSim.pollMowerForAction();
                if(monitorSim.isLawnComplete()){
                    break;
                }
                isValid = monitorSim.validateMowerAction();
                monitorSim.displayActionAndResponses();

                // Render lawn if needed
//                monitorSim.renderGlobalLawn();
//                monitorSim.renderLocalLawn();

                if(!isValid){
                    break;
                }
            }
        }
        monitorSim.displayReport();
    }

}
