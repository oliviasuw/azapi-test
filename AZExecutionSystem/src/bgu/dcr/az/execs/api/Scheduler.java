package bgu.dcr.az.execs.api;

/**
 *
 * @author bennyl
 */
public interface Scheduler {

    /**
     * loop over the given process table until all the processes are terminated
     * and then terminate
     *
     * @param table
     * @return a result object describing the reason for the termination
     * @throws java.lang.InterruptedException
     */
    TerminationReason schedule(ProcTable table, int numCores) throws InterruptedException;
    
    double getContention();
}
