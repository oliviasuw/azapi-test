/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.dev;

import bgu.dcr.az.api.ano.Variable;
import bgu.dcr.az.api.infra.CorrectnessTester;
import bgu.dcr.az.api.infra.EventPipe;
import bgu.dcr.az.api.infra.Round;
import bgu.dcr.az.api.infra.stat.StatisticCollector;
import bgu.dcr.az.api.pgen.ProblemGenerator;
import bgu.dcr.az.impl.infra.AbstractConfigureable;
import bgu.dcr.az.impl.pgen.MapProblem;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author bennyl
 */
public class DebugRound extends AbstractConfigureable implements Round {

    List<RoundListener> roundListners = new LinkedList<RoundListener>();
    @Variable(name = "seed", description = "the seed of the faild problem")
    long seed = -1;
    ProblemGenerator pgen;
    private RoundResult res = new RoundResult();
    private boolean runed = false;
    private Thread runThread;

    @Override
    public void addListener(RoundListener l) {
        roundListners.add(l);
    }

    @Override
    public void removeListener(RoundListener l) {
        roundListners.remove(l);
    }

    @Override
    public int getLength() {
        return 1;
    }

    @Override
    public String getName() {
        return "DEBUG";
    }

    @Override
    protected void configurationDone() {
        //GOOD FOR HIM .. 
    }

    @Override
    public long getSeed() {
        return seed;
    }

    @Override
    public String getRunningVarName() {
        return "NONE";
    }

    @Override
    public float getVarStart() {
        return 0;
    }

    @Override
    public float getVarEnd() {
        return 0;
    }

    @Override
    public float getTick() {
        return 0;
    }

    @Override
    public int getTickSize() {
        return 0;
    }

    @Override
    public float getCurrentVarValue() {
        return 0;
    }

    @Override
    public int getCurrentExecutionNumber() {
        return 1;
    }

    @Override
    public ProblemGenerator getProblemGenerator() {
        return pgen;
    }

    @Override
    public void registerStatisticCollector(StatisticCollector analyzer) {
        //NOT FOR ME THANKS
    }

    @Override
    public StatisticCollector[] getRegisteredStatisticCollectors() {
        return new StatisticCollector[0];
    }

    @Override
    public RoundResult getResult() {
        return this.res;
    }

    @Override
    public CorrectnessTester getCorrectnessTester() {
        return null;
    }

    @Override
    public void setCorrectnessTester(CorrectnessTester ctester) {
        //NOT FOR ME THANKS..
    }

    @Override
    public void fire(String name, Object... params) {
        //NEED TO BE REMOVED
    }

    @Override
    public EventPipe getEventPipe() {
        return new EventPipe();
    }

    @Override
    public boolean isFinished() {
        return runed;
    }

    @Override
    public void setEventPipe(EventPipe epipe) {
        //NO NEED 
    }

    @Override
    public void stop() {
        if (this.runThread != null){
            this.runThread.interrupt();
        }
    }

    @Override
    public void run() {
        Random r = new Random(seed);
        MapProblem p = new MapProblem();
        pgen.generate(p, r);
        
        
    }
}
