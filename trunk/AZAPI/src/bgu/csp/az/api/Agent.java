package bgu.csp.az.api;

import bgu.csp.az.api.Hooks.BeforeMessageSentHook;
import bgu.csp.az.api.ano.WhenReceived;
import bgu.csp.az.api.ds.ImmutableSet;
import bgu.csp.az.api.exp.InvalidValueException;
import bgu.csp.az.api.exp.PanicedAgentException;
import bgu.csp.az.api.exp.RepeatedCallingException;
import bgu.csp.az.api.infra.Execution;
import bgu.csp.az.api.tools.Assignment;
import java.util.LinkedList;
import java.util.List;

/**
 * Agent is the main building block for a CP algorithms, it includes the algorithms
 * main logic.
 * This is The base class of AbstractAgent and SimpleAgent.
 * 
 * The agents now have only one entry point and one exit point – means that there is no need for nasty System messages that was: 
 * a.	make the algorithm code much more complicated 
 * b.	imposes the use of a specially defined main because the algorithm can produce a solution / no-solution with lots of ways
 * Entry point: the function start() (or init() in the abstract agent)
 * Exit point: the exit point is not accessed directly instead you can call one of the following: 
 * by calling one of the finish functions or by calling panic
 * @author bennyl
 */
public abstract class Agent extends Agt0DSL {

    /**
     * the name of the statistic that collect the number of non concurrent constraing checks
     */
    public static final String NCCC_STATISTIC = "Number Of Concurent Constraint Checks";
    /**
     * the name of the statistic that collect the number of non concurrent steps of computation
     */
    public static final String NCSC_STATISTIC = "Number Of Concurent Steps Of Computation";
    /**
     * the name of the statistic that collect the number of constraint checkes made by agent.
     */
    public static final String CC_PER_AGENT_STATISTIC = "Constraint Checks Per Agent";
    /**
     * the name of the statistics that collect number of messages that this agent received during work
     */
    public static final String MESSAGES_RECEIVED_PER_AGENT_STATISTIC = "Messages Received Per Agent";
    /**
     * the name for the system termination message 
     * the system termination message is getting sent only by the abstract agent 
     */
    public static final String SYS_TERMINATION_MESSAGE = "__TERMINATE__";
    
    /**
     * the name for the system tick message 
     * the system tick message is getting sent only by the local search mailer when the system clock performed a 'tick'
     * its what wakes up the agent even if he dosent have any messages - in order for him to retick the clock
     */
    public static final String SYS_TICK_MESSAGE = "__TICK__";
    
    private int id; //The Agent ID
    private Execution exec; //The Execution That This Agent Is Currently Running Within
    private MessageQueue mailbox; //This Agent Mailbox
    private Problem prob; // The Agent Local Problem
    private boolean finished = false; //The Status of the current Agent - TODO: TRANSFORM INTO A STATUS ENUM SO WE CAN BE ABLE TO QUERY THE AGENT ABOUT IT CURRENT STATUS
    private Message currentMessage = null; //The Current Message (The Last Message That was taken from the mailbox
    private PlatformOps pops; //Hidden Platform Operation 
    private String mailGroupKey = getClass().getName(); // The Mail Group Key  - when sending mail the mail will get only to the relevant group
    /*
     * S T A T I S T I C S
     */
    private Statistic messagesReceived; //Counter for the number of messages received -> example usage: agent work ratio
    private Statistic ccStatistic;
    private long nccc = 0;
    private long ncsc = 0;
    /**
     * H O O K S
     */
    protected List<Hooks.BeforeMessageSentHook> beforeMessageSentHooks;
    /**
     * collection of hooks that will get called before message processing on this agent.
     */
    protected List<Hooks.BeforeMessageProcessingHook> beforeMessageProcessingHooks;

    /**
     * create a default agent - this agent will have id = -1 so you must reassign it 
     */
    public Agent() {
        this.id = -1;
        this.exec = null;
        beforeMessageSentHooks = new LinkedList<Hooks.BeforeMessageSentHook>();
        beforeMessageProcessingHooks = new LinkedList<Hooks.BeforeMessageProcessingHook>();
        this.pops = new PlatformOps();
    }

    /**
     * creates a message object from the given parameters 
     * and attach some metadata to it.. 
     * you can override this method to add some more metadata of your own each message that your agent sends
     * or even modify the message being sent
     * just use super.createMessage(...) to retrive a new message and then modify it as you please.
     * @param name
     * @param args
     * @return 
     */
    protected Message createMessage(String name, Object[] args) {
        Message ret = new Message(name, getId(), args);
        ret.getMetadata().put("nccc", nccc);
        ret.getMetadata().put("ncsc", ncsc);
        for (BeforeMessageSentHook hook : beforeMessageSentHooks) {
            hook.hook(ret);
        }
        beforeMessageSending(ret);
        return ret;
    }

    /**
     * override this function in the case you want to make some action every time before sending a message
     * this is a great place to write logs, attach timestamps to the message etc.
     * @param m
     */
    protected void beforeMessageSending(Message m) {
        //do nothing - derived classes can implement this if they want
    }

    private void updateAutoStatistics() {
        if (isFirstAgent()) {
            exec.getStatisticsTree().getChild(NCCC_STATISTIC).setValue(nccc);
            exec.getStatisticsTree().getChild(NCSC_STATISTIC).setValue(ncsc);
        }
    }

    /**
     * the agent is an message driven creature - each time the agent works it is because he 
     * received some message - this is the message that the agent currently processing = the last message taken from the mailbox
     * @return 
     */
    public Message getCurrentMessage() {
        return currentMessage;
    }

    /**
     * hookin to this agent class in the given hook point
     * hooks are mostly used for "automatic services/tools" like timestamp etc.
     * @param hook 
     */
    public void hookIn(Hooks.BeforeMessageSentHook hook) {
        beforeMessageSentHooks.add(hook);
    }

    /**
     * hookin to this agent class in the given hook point
     * hooks are mostly used for "automatic services/tools" like timestamp etc.
     * @param hook 
     */
    public void hookIn(Hooks.BeforeMessageProcessingHook hook) {
        beforeMessageProcessingHooks.add(hook);
    }

    /**
     * @return the problem currently working on
     * each agents has its own unique instance of the problem (based on the global problem that was given to the simulator)
     * and he managed it differently - so if you are building tools that have to be sent with the mailer to other agent dont 
     * include the agents problem in them as field.
     */
    protected Problem getProblem() {
        return prob;
    }

    /**
     * @return the id of this agent in simple algorithms this ID is the variable that the agent "handling"
     */
    public int getId() {
        return id;
    }

    /**
     * request the agent to process the next message (waiting if no such message in this agent mailbox)
     * @throws InterruptedException
     * @return the processed message
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
        Message msg = mailbox.take();
        currentMessage = msg;

        if (messagesReceived == null) {
            messagesReceived = createAgentStatistic(MESSAGES_RECEIVED_PER_AGENT_STATISTIC);
        }
        messagesReceived.add(1);
        updateNextMessageStatistics(msg);


        return msg;
    }

    private void updateNextMessageStatistics(Message msg) {
        long nnc = -1;
        long nns = -1;
        if (msg.getMetadata().containsKey("nccc")) {
            nnc = (Long) msg.getMetadata().get("nccc");
        }
        if (msg.getMetadata().containsKey("nccc")) {
            nns = (Long) msg.getMetadata().get("ncsc");
        }
        nccc = Math.max(nccc, nnc);
        ncsc = Math.max(ncsc, nns);
        ncsc++;
    }

    /**
     * waits for new messages to arraive
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
     * log somthing inside this agent log
     * @param what 
     */
    public void log(String what) {
        exec.log(id, this.mailGroupKey, what);
    }

    /**
     * stop execution - returning the given assignment, will cause a TERMINATION message to be sent
     * to all other agents, ans can be null and that will mean that there is no solution
     * @param ans
     */
    protected void finish(Assignment ans) {
        log("Calling Finish with assignment: " + ans + ", Starting shutdown sequence.");
        exec.reportFinalAssignment(ans);
        send(SYS_TERMINATION_MESSAGE).toAll(range(0, getNumberOfVariables() - 1));
    }

    /**
     * stop the execution (send TERMINATION to all agents) without solution - this method should be used in csp problem 
     * as it make sense there, it is the same as calling finish(null)
     */
    protected void finishWithNoSolution() {
        finish(null);
    }

    /**
     * will collect all the partial assignments that got submited (from all the agents) into an assignment 'a' and 
     * then will act as if you called to finish(a) :- see finish(Assignment) for more details.
     */
    protected void finishWithAccumulationOfSubmitedPartialAssignments() {
        finish(pops.getExecution().getPartialResult().getAssignment());
    }

    /**
     * stop execution of <b> current </b> agent - will not affect other agents
     * most of the times the desired function to call is finish(current-assignment) or finish(full-assignment) 
     * this function is here so that you can implement your own shutdown mechanism
     */
    protected void finish() {
        updateAutoStatistics();
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
     * should be used in algorithms that stop on idle detection - using this function the agent can submit its final result 
     * and when the idle detector will stop it this is the assignment that will be considered as the final assignment 
     * @param currentAssignment the assignment to submit
     */
    protected void submitCurrentAssignment(int currentAssignment) {
        exec.reportPartialAssignment(getId(), currentAssignment);
    }

    /**
     * remove the submitted current assignment
     */
    protected void unSubmitCurrentAssignmenr() {
        final Assignment partialAssignment = exec.getPartialResult().getAssignment();
        if (partialAssignment != null) {
            partialAssignment.unassign(this);
        }
    }

    /**
     * @return the last submitted assignment
     * will throw InvalideValueException if called before ever call submitCurrentAssignment
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
     * current implementation only check if this agent id is 0 but later implementations can use 
     * variable arrenger that can change the first agent id
     */
    public boolean isFirstAgent() {
        return this.getId() == 0;
    }

    /**
     * @return true if this is the last agent
     * current implementation just checks if this agent id +1 is num_of_vars  but later implementations can use 
     * variable arrenger that can change the last agent id
     */
    public boolean isLastAgent() {
        return this.getId() + 1 == getNumberOfVariables();
    }

    /**
     * same as calling a.calcCost(getProblem()); accepts - if a is null return infinity
     * @param a
     * @return 
     */
    public double costOf(Assignment a) {
        return (a == null ? Double.MAX_VALUE : a.calcCost(prob));
    }

    /**
     * @param var1
     * @param val1
     * @param var2
     * @param val2
     * @return the cost of assigning var1<-val1 while var2=val2 in the current problem
     */
    public double getConstraintCost(int var1, int val1, int var2, int val2) {
        return getProblem().getConstraintCost(var1, val1, var2, val2);
    }

    /**
     * @param var1
     * @param val1
     * @return the unary cost of assigning var1<-val1 in the current problem
     */
    public double getConstraintCost(int var1, int val1) {
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
     * @return this agents full domain - as immutable set - if you need to change your domain copy this set and then 
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
     * @return true if this agent call one of the finsh methods (or panic.. )
     */
    public boolean isFinished() {
        return finished;
    }

    /**
     * creates new agent statistic (or reuse an old one with the same name)
     * agent static is a statistic that relevant to each agent seperatly
     * for instance: "number of messages sent - per agent"
     * @param name
     * @return 
     */
    protected Statistic createAgentStatistic(String name) {
        return pops.getExecution().getStatisticsTree().getChild(name).getChild("Agent " + getId());
    }

    /**
     * creates new global statistic (or reuse an old one with the same name)
     * a global statistic is a statistic relevant to current execution 
     * for instance: "total number of messages sent" 
     * you should avoid using global statistics as there much slower then the agent statistics
     * most of the global statistics can be replaced with agent statistics and operation on the parent:
     * "total number of messages sent" => "number of messages sent per agent" and the operation sum on all those statistics
     * @param name
     * @return 
     */
    protected Statistic createGlobalStatistic(String name) {
        return pops.getExecution().getStatisticsTree().getChild(name);
    }

    /**
     * send the given message+arguments to all other agents excepts the sender (read the method send javadoc for
     * more details about sending the message)
     * @param msg
     * @param args
     */
    public void broadcast(String msg, Object... args) {
        broadcast(createMessage(msg, args));
    }

    public void broadcast(Message msg){
        final Execution execution = PlatformOperationsExtractor.extract(this).getExecution();
        execution.getMailer().broadcast(msg, mailGroupKey);
    }
    
    /**
     * sends a new message 
     * the message should have a name and any number of arguments
     * the message will be sent received by an agent in the method that 
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
    
    public SendMediator send(Message msg){
        final Execution execution = pops.getExecution();
        return new SendMediator(msg, execution.getMailer(), execution.getGlobalProblem(), mailGroupKey);
    }

    public void onIdleDetected() {
        throw new UnsupportedOperationException("if you are using IdleDetected feature you must implements Agent.onIdleDetected method");
    }

    /**
     * this function called when a SYS_TERMINATION Message Arrived -> it just calls finish on the agent, 
     * you can override it to make your own termination handling.
     */
    @WhenReceived(Agent.SYS_TERMINATION_MESSAGE)
    public void handleTermination() {
        finish();
    }
    
    /**
     * this class contains all the "hidden but public" methods,
     * because the user should extend the agent class all the "platform" operations 
     * can be called mistekanly by him, 
     * instead of making those operations private and then access them via reflection - what will create a decrease in the
     * performance - we just hiding them in this inner class, one object of this class are held by each agent 
     * and its private, in order for the platform to obtain this instance it uses another inner class 'PlatformOperationsExtractor'
     * this class contains a static method that extract the private field - because it also defined inside the agent it not have to use reflection to do so.
     */
    public class PlatformOps {

        private int numberOfSetIdCalls = 0;

        /**
         * attach an execution to this agent - this execution need to already contains global problem 
         * as this is the step that it being taken
         * @param exec
         */
        public void setExecution(Execution exec) {
            Agent.this.exec = exec;
            prob = new AgentProblem();
            ccStatistic = createAgentStatistic(CC_PER_AGENT_STATISTIC);
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

        public String getMailGroupKey() {
            return mailGroupKey;
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
    public class AgentProblem extends Problem {

        @Override
        public int getNumberOfVariables() {
            return exec.getGlobalProblem().getNumberOfVariables();
        }

        @Override
        public ImmutableSet<Integer> getDomainOf(int var) {
            return exec.getGlobalProblem().getDomainOf(var);
        }

        @Override
        public double getConstraintCost(int var1, int val1) {
            nccc++;
            ccStatistic.add(1);
            return exec.getGlobalProblem().getConstraintCost(var1, val1);
        }

        @Override
        public double getConstraintCost(int var1, int val1, int var2, int val2) {
            nccc++;
            ccStatistic.add(1);
            return exec.getGlobalProblem().getConstraintCost(var1, val1, var2, val2);
        }
    }
}
