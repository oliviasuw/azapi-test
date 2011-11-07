/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.dev.frm;

import bgu.csp.az.impl.AsyncMailer;
import bgu.csp.az.api.infra.Execution;
import bgu.csp.az.api.infra.ExecutionResult;
import bgu.csp.az.api.tools.Assignment;
import bgu.csp.az.dev.alg.IterativeCSPSolver.Status;
import java.util.List;
import bgu.csp.az.dev.Round;
import bgu.csp.az.api.Problem;
import java.io.File;
import bgu.csp.az.api.AlgorithmMetadata;
import bgu.csp.az.api.SearchType;
import bgu.csp.az.api.Statistic;
import bgu.csp.az.impl.infra.AbstractExecution;
import bgu.csp.az.impl.infra.Expirament;
import bgu.csp.az.api.pseq.ProblemSequence;
import bgu.csp.az.api.tools.IdleDetector;
import bgu.csp.az.dev.alg.BranchAndBound;
import bgu.csp.az.dev.alg.MACSolver;
import bgu.csp.az.impl.infra.CompleteSearchExecution;
import bgu.csp.az.impl.lsearch.LocalSearchExecution;
import bgu.csp.az.impl.infra.LogListener;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import nu.xom.Element;
import nu.xom.ParsingException;

import static bc.dsl.XNavDSL.*;
import static bam.utils.JavaUtils.*;

/**
 * This Is An Expirement Designed for Testing porpuse 
 * it executes TestExecutions in rounds
 * a round is a group of random problems and a configuration (like p1, n, d, etc.) 
 * this expirement can be listened to by implementing the inner Listener interface
 * please note that by doing so you have now 3 sources of information: the events that this experiment
 * is raising, the event pipe of a faild execution and the ScenarioLogger of the faild execution 
 * what every one of them contains? 
 * the event pipe of the faild execution contains raw inner data retrived from the framework
 * - most of the time this information can be used to visulize the cuurent algorithm run and because this is a test
 * implementation and it not requires visualization future versions of this class may use the null event pipe to stop 
 * the inner framework from producing such events
 * the scenario logger contains all the information that the testing implementation part of the framework produced
 * and the events that this class produces can help implementers of the ui to watch the progress of this expirement
 * 
 * @author bennyl
 */
public class TestExpirement extends Expirament {

    public static final boolean USE_SCENARIO_LOGGER = false;
    public static final String NEW_PROBLEM_EVENT = "test-expirament-new-problem";
    public static final String NEW_ROUND_EVENT = "test-expirament-new-round";
    public static final String TEMP_SCENARIO_LOG_DB_PATH = "temp/jdb";
    AlgorithmMetadata alg;
    List<Round> roundsLeft;
    List<Round> allRounds;
    String name;
    File failedProblemsDir = null;
    ProblemSequence currentRound;
    boolean saveFaildProblem = true;
    LinkedList<Listener> listeners = new LinkedList<Listener>();
    LinkedList<LogListener> logListeners = new LinkedList<LogListener>();
    ExecutorService es = Executors.newCachedThreadPool();

    /**
     * @param metadata the test metadata - a parsed xml file contains the definitions of all the rounds.
     * @param alg the algorithm to run the rounds on
     * @throws ParsingException 
     */
    public TestExpirement(Element metadata, AlgorithmMetadata alg) throws ParsingException {
        this.alg = alg;
        if (!isa(metadata, "test")) {
            throw new ParsingException("received xml not contain test data");
        }

        name = attr(metadata, "name");

        for (Element c : childs(metadata)) {
            if (isa(c,
                    "rounds")) {
                generateRounds(c);
            } else {
                throw new ParsingException("unknown test element - " + c.getLocalName());
            }
        }
    }

    public void addLogListener(LogListener logListener) {
        logListeners.add(logListener);
    }

    /**
     * @param p - a single problem ( a new round will be constracted from this single proble and be executed)
     * @param alg  - the algorithm to test this proglem against
     */
    public TestExpirement(Problem p, AlgorithmMetadata alg) {
        this.roundsLeft = new LinkedList<Round>();
        Round single = new Round(p);
        this.roundsLeft.add(single);
        this.allRounds = new LinkedList<Round>(roundsLeft);
        this.alg = alg;
        this.saveFaildProblem = false;
    }

    @Override
    public void run() {
        fireExpirementStarted();
        super.run();
    }

    /**
     * if this expirement fails it can save the executed problem 
     * if this variable is set it will get saved in the given directory, 
     * if it set to null no problem will get saved
     * TODO: consider moving this functionality to an upper controller.
     * @param failedProblemsDir 
     */
    public void setFailedProblemsDir(File failedProblemsDir) {
        this.failedProblemsDir = failedProblemsDir;
    }

    @Override
    protected AbstractExecution nextExecution() {

        if (currentRound == null || !currentRound.hasNext()) {
            final Round r = roundsLeft.remove(0);
            currentRound = r.generateProblemSequance(alg);
            fireRoundChanged(r);
        }
        
        AbstractExecution te = null;
        if (alg.getSearchType() == SearchType.COMPLEATE) {
            te = new CompleteSearchExecution(es, currentRound.next(), alg);
        } else {
            te = new LocalSearchExecution(es, currentRound.next(), alg);
        }
        
        for (LogListener ll : logListeners) {
            te.addLogListener(ll);
        }
        fireNewProblemExecuting(te.getGlobalProblem());

        return te;
    }

    @Override
    protected boolean hasMoreExecutions() {
        if (currentRound != null && currentRound.hasNext()) {
            return true;
        }
        if (!roundsLeft.isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * generats rounds from a given test metadata (currently can only got on the constractor)
     * @param c
     * @throws ParsingException 
     */
    private void generateRounds(Element c) throws ParsingException {
        roundsLeft = new LinkedList<Round>();

        int rnum = 1;
        try {
            for (Element p : childs(c)) {
                if (isa(p, "round")) {
                    Round r = new Round(
                            cint(attr(p, "length")),
                            cint(attr(p, "n")),
                            cint(attr(p, "d")),
                            cint(attr(p, "max-cost")),
                            cfloat(attr(p, "p1")),
                            rnum++);

                    if (hasAttr(p, "p2-tick")) {
                        r.setP2Tick(cfloat(attr(p, "p2-tick")));
                    }

                    if (hasAttr(p, "p2-start")) {
                        r.setP2Start(cfloat(attr(p, "p2-start")));
                    }

                    if (hasAttr(p, "p2-end")) {
                        r.setP2End(cfloat(attr(p, "p2-end")));
                    }

                    roundsLeft.add(r);
                } else {
                    throw new ParsingException("dont know how to generate problems from type " + c.getLocalName());
                }
            }
        } catch (Exception ex) {
            throw new ParsingException("cannot create problems - bad parameters: " + ex.getMessage() + " (" + ex.getClass().getSimpleName() + ")", ex);
        }

        allRounds = new LinkedList<Round>(roundsLeft);
    }

    /**
     * @return a list of all the rounds that are executed
     */
    public List<Round> getRounds() {
        return new LinkedList<Round>(allRounds);
    }

    /**
     * @return the algorithm that this expirement is testing.
     */
    public String getTestedAlgorithmName() {
        return alg.getName();
    }

    @Override
    protected ExecutionResult safeSolve(Problem currentProblem) {
        Status stat;
        switch (alg.getProblemType()) {
            case CONNECTED_COP:
            case COP:
                return new ExecutionResult(BranchAndBound.solve(currentProblem));
            
            case CSP:
                final MACSolver msolver = new MACSolver();
                stat = msolver.solve(currentProblem);
                if (stat == Status.imposible) {
                    return new ExecutionResult();
                } else {
                    return new ExecutionResult(msolver.getAssignment());
                }
            default:
                return null;
        }
    }

    @Override
    protected void whenExpirementEndedBecauseExecutionCrushed(Exception ex) {
        fireExecutionCrushed(getCurrentExecution(), ex);
    }

    @Override
    protected void whenExecutionEndedSuccessfully() {
        fireExecutionEndedSuccesfully();
    }

    @Override
    protected void whenExpirementEndedBecauseOfWrongResults(Assignment wrong, Assignment right) {
        System.out.println("Wrong Assignment : " + wrong + ", Optional Good Assignment: " + right);
        if (right == null) {
            System.out.println("Wrong Assignment Cost Was: " + wrong.calcCost(getCurrentExecution().getGlobalProblem()));
            System.out.println("While The Right Solution is 'Imposible'");
        } else if (wrong == null) {
            System.out.println("Wrong Assignment Was 'Imposible'");
            System.out.println("While The Right Solution cost is: " + right.calcCost(getCurrentExecution().getGlobalProblem()));
        } else {
            System.out.println("The Right Solution cost is: " + right.calcCost(getCurrentExecution().getGlobalProblem()));
            System.out.println("The Wrong Solution cost is: " + wrong.calcCost(getCurrentExecution().getGlobalProblem()));
        }
        fireExecutionEndedWrongly(getCurrentExecution(), wrong, right);
    }

    private void fireRoundChanged(Round r) {
        for (Listener l : listeners) {
            l.onNewRoundStarted(r);
        }
    }

    private void fireExecutionEndedWrongly(Execution currentExecution, Assignment wrong, Assignment right) {
        for (Listener l : listeners) {
            l.onExecutionEndedWithWrongResult(currentExecution, wrong, right);
        }
    }

    private void fireNewProblemExecuting(Problem p) {
        for (Listener l : listeners) {
            l.onNewProblemExecuted(p);
        }
    }

    private void fireExecutionCrushed(Execution currentExecution, Exception ex) {
        for (Listener l : listeners) {
            l.onExecutionCrushed(currentExecution, ex);
        }
    }

    private void fireExecutionEndedSuccesfully() {
        for (Listener l : listeners) {
            l.onExpirementEndedSuccessfully();
        }
    }

    public void addListener(Listener l) {
        listeners.addLast(l);
    }

    private void fireExpirementStarted() {
        for (Listener l : listeners) {
            l.onExpirementStarted();
        }
    }

    @Override
    protected void whenSingleExecutionEndedSuccessfully(Execution execu) {
        fireStatisticsRetrived(execu.getStatisticsTree());
    }

    private void fireStatisticsRetrived(Statistic statisticsTree) {
        for (Listener l : listeners) {
            l.onStatisticsRetrived(statisticsTree);
        }
    }

    public int getNumberOfLeftProblems() {
        int sum = 0;
        for (Round r : roundsLeft) {
            sum += r.getLength();
        }

        return sum;
    }

    public static interface Listener {

        void onExpirementEndedSuccessfully();

        void onExecutionEndedWithWrongResult(Execution execution, Assignment wrong, Assignment right);

        void onExecutionCrushed(Execution ex, Exception exc);

        void onExpirementStarted();

        void onNewProblemExecuted(Problem p);

        void onNewRoundStarted(Round r);

        void onStatisticsRetrived(Statistic root);
    }

    public static class Handler implements Listener {

        @Override
        public void onExpirementEndedSuccessfully() {
        }

        @Override
        public void onExecutionEndedWithWrongResult(Execution execution, Assignment wrong, Assignment right) {
        }

        @Override
        public void onExecutionCrushed(Execution ex, Exception exc) {
        }

        @Override
        public void onExpirementStarted() {
        }

        @Override
        public void onNewProblemExecuted(Problem p) {
        }

        @Override
        public void onNewRoundStarted(Round r) {
        }

        @Override
        public void onStatisticsRetrived(Statistic root) {
        }
    }
}
