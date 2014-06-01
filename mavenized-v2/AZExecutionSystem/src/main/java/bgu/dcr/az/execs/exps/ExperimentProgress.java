/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.execs.exps;

/**
 *
 * @author bennyl
 */
public class ExperimentProgress {

    private ModularExperiment exp;
    private boolean running = true;

    public ExperimentProgress(ModularExperiment exp) {
        this.exp = exp;
    }

    public <T extends ExperimentProgressEnhancer> T get(Class<T> type) {
        return exp.require((Class<T>) type);
    }

    public boolean has(Class<? extends ExperimentProgressEnhancer> type) {
        return exp.isInstalled(type);
    }

    public boolean isExperimentRunning() {
        return running;
    }

    void setRunning(boolean running) {
        this.running = running;
    }

}
