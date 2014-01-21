package bgu.dcr.az.execs.api;

import java.util.Collection;

/**
 *
 * Note: implementations of this interface are assumed to be thread safe.
 *
 * @author bennyl
 */
public interface ProcTable {

    /**
     * attempt to acquire one processes, this method can block in the case where
     * there are no processes that can be aquire
     *
     * @return the acquired process, it is guarantee that there is no other core
     * that currently holding this process, you must call release with the
     * returned process pid when you finish with the process in order allow
     * different cores to handle this process again later. note that after
     * releasing a process you should dispose it and never touch it again until
     * you acquire it back. the method may also return null in order to signal
     * for idle detection or empty table (you should check {@link ProcTable#isEmpty()
     * } and {@link ProcTable#isInIdleState() }.
     *
     * @throws InterruptedException
     */
    Proc acquire() throws InterruptedException;

    /**
     * behave the same as {@link ProcTable#acquire()} but in the case where
     * there are no available processes to acquire it will return null instead
     * of blocking
     *
     * @see ProcTable#acquire()
     * @return the same as {@link ProcTable#acquire()} but in the case where
     * there are no available processes to acquire it will return null.
     */
    Proc acquireNonBlocking();

    /**
     * release a process with the given pid, the core that attempt to release a
     * process should have this process acquired via the aquire method. when
     * releasing a process the process state is queried, and appropriate actions
     * are made according to its status.
     *
     * @param pid
     */
    void release(int pid);

    /**
     * signaling that the process with the given pid should get one quota of
     * execution independent of its state (with one exception of it being
     * terminated - in such case a ProcessesDoesntExistsException will get
     * thrown), it is assumed that only a process can wake another process (this
     * assumption is seating in the base of different idle detection algorithms)
     *
     * @param pid
     * @return true if the given process was notified successfully - can return
     * false if the given process is already terminated or not started
     */
    boolean wake(int pid);

    /**
     * wake all the blocking processes - this method should be called only if
     * the system is on idle state and it is assumed that the caller is not a
     * process (or else we are not in an idle state)
     *
     * @see bgu.dcr.az.execs.api.ProcTable#wake(int) wake(int)
     */
    void resumeAll();

    /**
     * add a process to this ProcTable, note that the process id must be unique
     * or else the result of this method is undefined. it is assumed that once
     * the execution started any further invokations of this method will be
     * performed by an existing process
     *
     * @param p
     */
    void add(Proc p);

    /**
     * @return true if all the processes in this process table are in Blocking
     * state.
     */
    boolean isInIdleState();

    /**
     * @return true in the case that all the processes that was contained by
     * this process table were terminated.
     */
    boolean isEmpty();

    /**
     * @return a new unique id that must be used for the next new process in the
     * system, NOTE: do not use this method for other purposes as resources
     * regarding the returned pid may be allocated upon calling this method
     * (implementation dependent).
     */
    int nextProcessId();

    /**
     * @return all the existing process ids in this table
     */
    Collection<Integer> allProcessIds();
}
