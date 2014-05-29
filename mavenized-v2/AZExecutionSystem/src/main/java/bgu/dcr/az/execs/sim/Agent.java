/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.execs.sim;

import static bgu.dcr.az.execs.sim.Agt0DSL.panic;
import bgu.dcr.az.execs.sim.net.Message;
import java.util.Map;

/**
 *
 * @author bennyl
 */
public class Agent extends Agt0DSL {

    static ThreadLocal<Agent> currentAgent = new ThreadLocal<Agent>();
    private Internals internals;

    public Agent() {
        internals = new Internals();
    }

    protected Agent(Internals internals) {
        this.internals = internals;
    }

    public int getId() {
        return internals.id;
    }

    public boolean terminated() {
        return internals.isFinished();
    }

    public void terminate() {
        internals.setFinished(true);
    }

    public static class Internals {

        private int id = -1;
        private boolean finished = false;
        private Agent a;

        public void initialize(int id, SimulatedMachine ac, Agent a, Map<String, String> args) {
            if (id == -1) {
                panic("agent id must be greater than or equal to zero (received: " + id + ")");
            }
            if (this.id != -1) {
                panic("agent double initialization found!");
            }
            this.id = id;
            this.a = a;
        }

        public boolean isFinished() {
            return finished;
        }

        public void setFinished(boolean finished) {
            this.finished = finished;
        }

        public int getId() {
            return id;
        }

        public Agent getAgent() {
            return a;
        }

        public void handleMessage(Message m) {
        }

        public void handleIdle() {
        }

        public void handleTick(int tick) {
        }

        public void start() {
        }
    }

    static Internals internalsOf(Agent a) {
        return a.internals;
    }

    public static Agent current() {
        return currentAgent.get();
    }

}
