package bgu.dcr.az.execs.lowlevel;

/**
 *
 * @author bennyl
 */
public interface SystemCalls {

    /**
     * wake a process with the given pid (if it is in a sleeping state)
     *
     * @See bgu.dcr.az.execs.api.ProcTable#wake wake
     * @param pid - the pid of the process to wake.
     */
    boolean wake(int pid);

    /**
     * execute additional process, you can get a new id in order to initialize
     * the process with using the method
     * {@link bgu.dcr.az.execs.api.SystemCalls#nextProcessId()}
     *
     * @param p
     */
    void exec(Proc p);

    /**
     * @return a new unique id that can be used for new processes in the system,
     * NOTE: do not use this method for other purposes as resources regarding
     * the returned pid may be allocated upon calling this method
     * (implementation dependent).
     */
    int nextProcessId();
}
