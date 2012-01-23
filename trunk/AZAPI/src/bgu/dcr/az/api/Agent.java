package bgu.dcr.az.api;

import bgu.dcr.az.api.Hooks.BeforeCallingFinishHook;
import bgu.dcr.az.api.Hooks.BeforeMessageSentHook;
import bgu.dcr.az.api.ano.Algorithm;
import bgu.dcr.az.api.ano.WhenReceived;
import bgu.dcr.az.api.ds.ImmutableSet;
import bgu.dcr.az.api.exp.InvalidValueException;
import bgu.dcr.az.api.exp.PanicedAgentException;
import bgu.dcr.az.api.exp.RepeatedCallingException;
import bgu.dcr.az.api.infra.Execution;
import bgu.dcr.az.api.infra.VariableMetadata;
import bgu.dcr.az.api.tools.Assignment;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Agent is the main building block for a CP algorithms, it includes the algorithms
 * main logic.
 * This is The base class of AbstractAgent and SimpleAgent.
 * 
 * The agents now have only one entry point and one exit point – means that there is no need for nasty System messages that was: 
 * a.	make the algorithm code much more complicated 
 * b.	imposes the use of a specially defined main because the algorithm can produce a solution / no-solution with lots of ways
 * Entry point: the function start() (or init() in the abstract agent)
 * Exit point: the exit point is not accessed directly instead you can call one of the finish functions or call panic
 * 
 */
public abstract class Agent extends Agt0DSL {

    /**
     * will output the logs to stdout
     */
    private static final boolean USE_DEBUG_LOGS = false;
    /**
     * the name of the statistic which collects the number of non concurrent constraint checks
     */
    public static final String NCCC_STATISTIC = "Number Of Concurrent Constraint Checks";
    /**
     * the name of the statistic which collects the number of non concurrent steps of computation
     */
    public static final String NCSC_STATISTIC = "Number Of Concurrent Steps Of Computation";
    /**
     * the name of the statistic which collects the number of constraint checks made by agent
     */
    public static final String CC_PER_AGENT_STATISTIC = "Constraint Checks Per Agent";
    /**
     * the name of the statistics which collects number of messages that this agent have received during work
     */
    public static final String MESSAGES_RECEIVED_PER_AGENT_STATISTIC = "Messages Received Per Agent";
    /**
     * the name for the system termination message 
     * the system termination message is getting sent only by the abstract agent 
     */
    public static final String SYS_TERMINATION_MESSAGE = "__TERMINATE__";
    /**
     * the name for the system tick message 
     * the system tick message is getting sent only by the local search mailer when the system clock performs a 'tick'
     * it wakes up the agent even if he doesn't have any messages - in order for him to re-tick the clock
     */
    public static final String SYS_TICK_MESSAGE = "__TICK__";
    private int id; //The Agent ID
    private Execution exec; //The Execution That This Agent Is Currently Running Within
    private MessageQueue mailbox; //This Agent Mailbox
    private ImmutableProblem prob; // The Agent Local Problem
    private boolean finished = false; //The Status of the current Agent - TODO: TRANSFORM INTO A STATUS ENUM SO WE CAN BE ABLE TO QUERY THE AGENT ABOUT IT CURRENT STATUS
    private Message currentMessage = null; //The Current Message (The Last Message That was taken from the mailbox)
    private PlatformOps pops; //Hidden Platform Operation 
    private String mailGroupKey = getClass().getName(); // The Mail Group Key  - when sending mail it will be recieved only by the relevant group
    /*
     * S T A T I S T I C S
     */
    private int cc = 0;
    /**
     * H O O K S
     */
    protected List<Hooks.BeforeMessageSentHook> beforeMessageSentHooks;
    /**
     * collection of hooks that will get called before message processing on this agent
     */
    protected List<Hooks.BeforeMessageProcessingHook> beforeMessageProcessingHooks;
    /**
     * collection of hooks that will be called before the agent calls finish
     */
    protected List<Hooks.BeforeCallingFinishHook> beforeCallingFinishHooks;

    /**
     * METADATA
     */
    private String algorithmName;
    
    /**
     * create a default agent - this agent will have id = -1 so you must reassign it 
     */
    public Agent() {
        this.id = -1;
        this.exec = null;
        beforeMessageSentHooks = new LinkedList<Hooks.BeforeMessageSentHook>();
        beforeMessageProcessingHooks = new LinkedList<Hooks.BeforeMessageProcessingHook>();
        beforeCallingFinishHooks = new LinkedList<Hooks.BeforeCallingFinishHook>();
        this.pops = new PlatformOps();
        this.algorithmName = getClass().getAnnotation(Algorithm.class).name();
    }

    /**
     * @return the number of constraint checks this agent performed
     */
    public long getNumberOfConstraintChecks() {
        return cc;
    }

    /**
     * creates a message object from the given parameters 
     * and attach some metadata to it.. 
     * you can override this method to add some more metadata of your own on each message that your agent sends
     * or even modify the message being sent
     * just use super.createMessage(...) to retrieve a new message and then modify it as you please
     * @param name
     * @param args
     * @return 
     */
    protected Message createMessage(String name, Object[] args) {
        Message ret = new Message(name, getId(), args);
        for (BeforeMessageSentHook hook : beforeMessageSentHooks) {
            hook.hook(this, ret);
        }
        beforeMessageSending(ret);
        return ret;
    }

    /**
     * report to statistic analyzer / algorithm visualization
     * @param args
     * @return
     */
    public ReportMediator report(Object... args) {
        return new ReportMediator(args, this);
    }

    /**
     * override this function in case you want to make some action every time before sending a message
     * this is a great place to write logs, attach timestamps to the message etc.
     * @param m
     */
    protected void beforeMessageSending(Message m) {
        //do nothing - derived classes can implement this if they want
    }

    /**
     * the agent is an message driven creature - he works only if he 
     * received any message - this is the message that the agent currently processing = the last message taken from the mailbox
     * @return 
     */
    public Message getCurrentMessage() {
        return currentMessage;
    }

    /**
     * hook-in to this agent class in the given hook point
     * hooks are mostly used for "automatic services/tools" like timestamp etc.
     * @param hook 
     */
    public void hookIn(Hooks.BeforeMessageSentHook hook) {
        beforeMessageSentHooks.add(hook);
    }

    /**
     * hook-in to this agent class in the given hook point
     * hooks are mostly used for "automatic services/tools" like timestamp etc.
     * @param hook 
     */
    public void hookIn(Hooks.BeforeMessageProcessingHook hook) {
        beforeMessageProcessingHooks.add(hook);
    }

    /**
     * hook to be callback before agent calls finish
     * @param hook
     */
    public void hookIn(Hooks.BeforeCallingFinishHook hook) {
        beforeCallingFinishHooks.add(hook);
    }

    /**
     * @return the problem currently being worked on
     * each agent has its own unique instance of the problem (based on the global problem that was given to the simulator)
     * and he manages it differently - so if you are building tools that have to be sent with the mailer to other agents don't 
     * include the agent's problem in them as a field.
     */
    protected ImmutableProblem getProblem() {
        return prob;
    }

    /**
     * @return the id of this agent. In simple algorithms this ID is the variable that the agent is "handling"
     */
    public int getId() {
        return id;
    }

    /**
     * request the agent to process the next message (waiting if the agents message queue is empty)
     * @throws InterruptedException
     */
    public abstract void processNextMessage() throws InterruptedException;

    /**
     * @return true if this agent have messages in its mailbox
     */
    public boolean hasPendingMessages() {
        return mailbox.size() > 0;
    }

    /**
     * @return the next message from the mailbox - waiting if necessary for a new message to arrive
     * @throws InterruptedException
     */
    protected Message nextMessage() throws InterruptedException {
        currentMessage = mailbox.take();
        return currentMessage;
    }

    /**
     * waits for new messages to arrive
     * @throws InterruptedException
     */
    public void waitForNewMessages() throws InterruptedException {
        mailbox.waitForNewMessages();

    }

    /**
     * stop execution with severe error
     * @param why
     */
    protected void panic(String why) {
        panic(why, null);
    }

    /**
     * stop execution with severe error
     * @param why
     * @param cause
     */
    public void panic(String why, Exception cause) {
        if (cause == null) {
            cause = new PanicedAgentException(why);
        }
        exec.reportCrushAndStop(cause, why); //should cause all the inner agent runners to stop too.
    }

    /**
     * stop execution with severe error
     * @param cause
     */
    public void panic(Exception cause) {
        panic(null, cause);
    }

    /**
     * log something inside this agent log
     * @param what 
     */
    public void log(String what) {
        exec.log(id, this.mailGroupKey, what);
        if (USE_DEBUG_LOGS) {
            System.out.println("[" + getClass().getSimpleName() + "] " + getId() + ": " + what);
        }
    }

    /**
     * stop execution - returning the given assignment, will cause a TERMINATION message to be sent
     * to all other agents, if ans is null there is no solution
     * @param ans
     */
    protected void finish(Assignment ans) {
        log("Calling Finish with assignment: " + ans + ", Starting shutdown sequence.");
        exec.reportFinalAssignment(ans);
        send(SYS_TERMINATION_MESSAGE).toAll(range(0, getNumberOfVariables() - 1));
    }

    /**
     * stop the execution (send TERMINATION to all agents) without solution - this method should be used in csp problem 
     * as it make sense there. This is the same as calling finish(null)
     */
    protected void finishWithNoSolution() {
        finish(null);
    }

    /**
     * will collect all the partial assignments that got submitted (from all the agents) into an assignment 'a' and 
     * then will act as if you called finish(a) :- see finish(Assignment) for more details.
     */
    protected void finishWithAccumulationOfSubmitedPartialAssignments() {
        finish(pops.getExecution().getResult().getAssignment());
    }

    /**
     * stop execution of <b> current </b> agent - will not affect other agents
     * most of the times the desired function to call is finish(current-assignment) or finish(full-assignment) 
     * this function is here so that you can implement your own shutdown mechanism
     */
    protected void finish() {
        hookBeforeCallingFinish();
        finished = true;
    }

    /**
     * call this function when an agent is done and want to report its assignment upon finishing
     * @param currentAssignment 
     */
    protected void finish(int currentAssignment) {
        submitCurrentAssignment(currentAssignment);
        finish();
    }

    /**
     * the agent can submit its assignment
     * so that when the function finishWithAccumulationOfSubmitedPartialAssignments will get called 
     * this will be the assignment to be accumulated
     * - if you want to re-assign a new value you don't have to call unSubmitCurrentAssignment, you can just call this function again with the new value
     * @param currentAssignment the assignment to submit
     */
    protected void submitCurrentAssignment(int currentAssignment) {
        exec.reportPartialAssignment(getId(), currentAssignment);
    }

    /**
     * remove the submitted current assignment
     */
    protected void unSubmitCurrentAssignment() {
        final Assignment partialAssignment = exec.getResult().getAssignment();
        if (partialAssignment != null) {
            partialAssignment.unassign(this);
        }
    }

    /**
     * @return the last submitted assignment
     * will throw InvalideValueException if no assignment was submitted
     */
    protected Integer getSubmitedCurrentAssignment() {
        final Assignment finalAssignment = exec.getResult().getAssignment();
        if (finalAssignment != null) {
            return finalAssignment.getAssignment(getId());
        }

        throw new InvalidValueException("Agent called 'getSubmitedCurrentAssignment' before he ever called 'submitCurrentAssignment'");
    }

    /**
     * this function called once on each agent when the algorithm is started 
     */
    public abstract void start();

    /**
     * @return true if this is the first agent
     * current implementation only checks if this agent's id is 0 but later implementations can use 
     * variable arranger that can change the first agent's id
     */
    public boolean isFirstAgent() {
        return this.getId() == 0;
    }

    /**
     * @return true if this is the last agent
     * current implementation just checks if this agent's id +1 is num_of_vars  but later implementations can use 
     * variable arranger that can change the last agent's id
     */
    public boolean isLastAgent() {
        return this.getId() + 1 == getNumberOfVariables();
    }

    /**
     * same as calling a.calcCost(getProblem()); accept - if a is null returns infinity
     * @param a
     * @return 
     */
    public int costOf(Assignment a) {
        return (a == null ? Integer.MAX_VALUE : a.calcCost(prob));
    }

    /**
     * @param var1
     * @param val1
     * @param var2
     * @param val2
     * @return the cost of assigning var1<-val1 while var2=val2 in the current problem
     */
    public int getConstraintCost(int var1, int val1, int var2, int val2) {
        return getProblem().getConstraintCost(var1, val1, var2, val2);
    }

    /**
     * @param var1
     * @param val1
     * @return the unary cost of assigning var1<-val1 in the current problem
     */
    public int getConstraintCost(int var1, int val1) {
        return getProblem().getConstraintCost(var1, val1);
    }

    /**
     * @return the number of variable in the current problem
     */
    public int getNumberOfVariables() {
        return getProblem().getNumberOfVariables();
    }

    /**
     * @param var
     * @return the domain of some variable in the current problem
     */
    public ImmutableSet<Integer> getDomainOf(int var) {
        return getProblem().getDomainOf(var);
    }

    /**
     * @return this agents full domain - as immutable set - if you need to change your domain- copy this set and then 
     * change your copy : HashSet<Integer> currentDomain = new HashSet<Integer>(getDomain());
     */
    public ImmutableSet<Integer> getDomain() {
        return getProblem().getDomainOf(getId());
    }

    /**
     * same as getDomain().size()
     * @return 
     */
    public int getDomainSize() {
        return getDomain().size();
    }

    /**
     * @param var1
     * @param var2
     * @return true if var1 is constrained with var2 which means :
     *  there is val1 in domainOf[var1] and val2 in domainOf[var2] where getConstraintCost(val1, var1, var2, val2) != 0 
     */
    public boolean isConstrained(int var1, int var2) {
        return getProblem().isConstrained(var1, var2);
    }

    /**
     * @return true if this agent called one of the finish methods (or panic.. )
     */
    public boolean isFinished() {
        return finished;
    }

    /**
     * send the given message+arguments to all other agents except the sender (read the method send javadoc for
     * more details about sending the message)
     * @param msg
     * @param args
     */
    public void broadcast(String msg, Object... args) {
        broadcast(createMessage(msg, args));
    }

    /**
     * broadcast a new message - prefer using broadcast(String msg, Object... args)
     * @param msg
     */
    public void broadcast(Message msg) {
        final Execution execution = PlatformOperationsExtractor.extract(this).getExecution();
        execution.getMailer().broadcast(msg, mailGroupKey);
    }

    /**
     * sends a new message 
     * the message should have a name and any number of arguments
     * the message which will be sent here will be received by an agent in the method that 
     * defines @WhenReceived with the name of the message (case sensitive!)
     * and the arguments will be inserted to the parameters of that method
     * 
     * usage: send("MESSAGE_NAME", ARG1, ARG2, ..., ARGn).to(OTHER_AGENT_ID)
     * 
     * @param msg the message name
     * @param args the list (variadic) of arguments that belongs to this message
     * @return continuation class 
     */
    public SendMediator send(String msg, Object... args) {
        return send(createMessage(msg, args));
    }

    /**
     * send a new message - prefer using send(String msg, Object... args)
     * @param msg
     * @return
     */
    public SendMediator send(Message msg) {
        final Execution execution = pops.getExecution();
        return new SendMediator(msg, execution.getMailer(), execution.getGlobalProblem(), mailGroupKey);
    }

    /**
     * a callback which is called when idle detected - this is the place to finish the algorithm or revive from idle
     */
    public void onIdleDetected() {
        throw new UnsupportedOperationException("if you are using IdleDetected feature you must implements Agent.onIdleDetected method");
    }

    /**
     * a callback which is called (only when running in synchronized mode) just before the next tick (when the agent finish handling all its messages)
     */
    public void onMailBoxEmpty() {
//        throw new UnsupportedOperationException("if you are running a Synchronized Search you must implements Agent.onMailBoxEmpty method");
    }

    /**
     * this function is called when a SYS_TERMINATION Message Arrived -> it just calls finish on the agent, 
     * you can override it to make your own termination handling.
     */
    @WhenReceived(Agent.SYS_TERMINATION_MESSAGE)
    public void handleTermination() {
        finish();
    }

    /**
     * Note: the concept 'system time' only exists in synchronized execution 
     * @return the number of ticks passed since the algorithm start (first tick is 0), you can read about the definition of tick
     * in agent zero manual
     */
    public long getSystemTimeInTicks() {
        return pops.getExecution().getSystemClock().time();
    }

    private void hookBeforeCallingFinish() {
        for (BeforeCallingFinishHook l : beforeCallingFinishHooks) {
            l.hook(this);
        }
    }

    /**
     * @return the registered algorithm name for this agent
     */
    public String getAlgorithmName() {
        return algorithmName;
    }

    /**
     * this class contains all the "hidden but public" methods,
     * because the user should extend the agent class all the "platform" operations 
     * can be called mistakenly by him, 
     * instead of making those operations private and then access them via reflection - which will create a decrease in the
     * performance - we just hide them in this inner class. One private object of this class are held by each agent.
     * In order for the platform to obtain this instance it uses another inner class 'PlatformOperationsExtractor'
     * this class contains a static method that extracts the private field - because it's also defined inside the agent it doesn't have to use reflection to do so.
     */
    public class PlatformOps {

        private int numberOfSetIdCalls = 0;

        /**
         * attach an execution to this agent - this execution needs to already contains global problem 
         * @param exec
         */
        public void setExecution(Execution exec) {
            Agent.this.exec = exec;
            prob = new AgentProblem();
        }

        /**
         * set the agent id - this method is called by the execution environment 
         * to set the agent id and should not be called by hand / by an algorithm implementer
         * this function should only be called once and will throw Repeated Calling Exception upon repeated calls.
         * @param id 
         */
        public void setId(int id) {
            numberOfSetIdCalls++;
            if (numberOfSetIdCalls != 1) {
                throw new RepeatedCallingException("you can only call setId once.");
            }

            Agent.this.id = id;
            mailbox = getExecution().getMailer().register(Agent.this, mailGroupKey);
        }

        /**
         * @return the Execution object - this is the object that connects the agent to the execution environment
         * most of the time the algorithm implementer will not have to deal with this object 
         * it is mostly here for advance users / tool implementers 
         */
        public Execution getExecution() {
            return exec;
        }

        /**
         * @return the current agent mail group key (current implementation will just return the class name 
         * but it should get changed in later implementations)
         */
        public String getMailGroupKey() {
            return mailGroupKey;
        }
//
//        public VariableMetadata[] provideExpectendVariabls() {
//            return VariableMetadata.scan(Agent.this);
//        }

        public void configure(Map<String, Object> vars) {
            VariableMetadata.assign(Agent.this, vars);
        }
    }

    /**
     * See documentation of PlatformOps.
     */
    public static class PlatformOperationsExtractor {

        /**
         * extracting the hidden Platform Operations object from the given agent.
         * @param a
         * @return
         */
        public static PlatformOps extract(Agent a) {
            return a.pops;
        }
    }

    /**
     * this is a wrap on the given problem - each agent posess a wrap like this instaed of the actual problem
     */
    public class AgentProblem implements ImmutableProblem {

        public int getAgentId() {
            return Agent.this.getId();
        }

        @Override
        public int getNumberOfVariables() {
            return exec.getGlobalProblem().getNumberOfVariables();
        }

        @Override
        public ImmutableSet<Integer> getDomainOf(int var) {
            return exec.getGlobalProblem().getDomainOf(var);
        }

        @Override
        public int getConstraintCost(int var1, int val1) {
            cc++;
            return exec.getGlobalProblem().getConstraintCost(var1, val1);
        }

        @Override
        public int getConstraintCost(int var1, int val1, int var2, int val2) {
            cc++;
            return exec.getGlobalProblem().getConstraintCost(var1, val1, var2, val2);
        }

        @Override
        public String toString() {
            return exec.getGlobalProblem().toString();
        }

        @Override
        public int getConstraintCost(int var, int val, Assignment ass) {
            cc++;
            return exec.getGlobalProblem().getConstraintCost(var, val, ass);
        }

        @Override
        public int getDomainSize(int var) {
            return exec.getGlobalProblem().getDomainSize(var);
        }

        @Override
        public HashMap<String, Object> getMetadata() {
            return exec.getGlobalProblem().getMetadata();
        }

        @Override
        public Set<Integer> getNeighbors(int var) {
            return exec.getGlobalProblem().getNeighbors(var);
        }

        @Override
        public boolean isConsistent(int var1, int val1, int var2, int val2) {
            return exec.getGlobalProblem().isConsistent(var1, val1, var2, val2);
        }

        @Override
        public boolean isConstrained(int var1, int var2) {
            return exec.getGlobalProblem().isConstrained(var1, var2);
        }

        /**
         * @return the type of the problem
         */
        @Override
        public ProblemType type() {
            return exec.getGlobalProblem().type();
        }
    }
}
