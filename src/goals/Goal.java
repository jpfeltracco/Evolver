package goals;

import evolver.ElementHolder;

public abstract class Goal {

    private long systemTime;
    private long elapsedTime;

    /**
     * Use this method to tell the Evolver when the evolution process is complete. An
     * ElementHolder will be given that contains all the elements for the last generation,
     * and it is your job to determine if these elements are fit enough for completion.
     * @param elements the elements from the last generation
     * @return whether or not this evolution batch is complete
     */
    public abstract boolean checkComplete(ElementHolder elements);

    /**
     * The max time this Evolution is allowed to run before exiting and reporting back to
     * the server. Measured in milliseconds (ms). NOTE: 1 second = 1000 milliseconds.
     * @return the max Evolution time in milliseconds
     */
    public abstract int maxEvolveTime();

    /**
     * Use this to adjust the goal based on the starting position of this evolution.
     * @param elements the elements that will be initial given to the Evolver
     */
    public abstract void initialize(ElementHolder elements);

    /**
     * This function will be called after checkComplete() has returned true, and will send
     * the returned String to the server as a memo about this particular Evolution process.
     * Please populate this method with useful information for later analysis. NOTE: Follow
     * the new line strategy IE: "\n" + title + ":\t" + val
     * @return a String describing how this batch went
     */
    public abstract String memo();

    /**
     * Gets the ending message for this goal.
     * @return
     */
    public String message() {
        return memo() + "\nTime Elapsed: " + (double) (elapsedTime / 1000000000.0) + "s";
    }

    /**
     * Sets the start time of this Evolution batch. Run as the Evolution process begins.
     */
    public void activate(ElementHolder elements) {
        initialize(elements);
        systemTime = System.nanoTime();
    }

    /**
     * Checks whether or not the evolution process is complete
     * @param elements the elements from the last generation
     * @return whether or not this evolution batch is complete
     */
    public boolean check(ElementHolder elements) {
        elapsedTime = System.nanoTime() - systemTime;
        if (elapsedTime > (maxEvolveTime() * 1000000.0)) {
            reason = "Timeout: CurrentTime: " + elapsedTime + " > " + (maxEvolveTime() * 1000000.0);
            return true;
        }
        if (checkComplete(elements)) {
            reason = "Goal Reached";
            return true;
        }

        return false;
    }

    String reason;

    public String exitReason() {
        return reason;
    }
}
