/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.dev.frm;

import bc.swing.pfrm.units.EventBusUnit;
import bgu.csp.az.api.exp.ConnectionFaildException;
import bgu.csp.az.api.infra.Execution;
import bgu.csp.az.api.infra.Experiment;
import bgu.csp.az.api.infra.Round;
import bgu.csp.az.dev.db.DatabaseUnit;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bennyl
 */
public enum ExecutionUnit implements Experiment.ExperimentListener {

    UNIT;

    void execute(File xml) {
        try {
            Experiment experiment = TestXMLReader.read(xml);
            DatabaseUnit.UNIT.delete();
            DatabaseUnit.UNIT.connect();
            DatabaseUnit.UNIT.createStatisticDatabase();
            DatabaseUnit.UNIT.startCollectorThread();
            experiment.addListener(this);
            experiment.run();
            experiment.removeListener(this);
            DatabaseUnit.UNIT.stopCollectorThread();
            DatabaseUnit.UNIT.disconnect();
            System.out.println(experiment.getResult().toString());
        } catch (ConnectionFaildException ex) {
            Logger.getLogger(ExecutionUnit.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ExecutionUnit.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(ExecutionUnit.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(ExecutionUnit.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        UNIT.execute(new File("exp.xml"));
    }

    @Override
    public void onExpirementStarted(Experiment source) {
        //TO EVENT BUS
    }

    @Override
    public void onExpirementEnded(Experiment source) {
        //TO EVENT BUS
    }

    @Override
    public void onNewRoundStarted(Experiment source, Round round) {
        //TO EVENT BUS
    }

    @Override
    public void onNewExecutionStarted(Experiment source, Round round, Execution exec) {
        //TO EVENT BUS
    }

    @Override
    public void onExecutionEnded(Experiment source, Round round, Execution exec) {
        DatabaseUnit.UNIT.insertLater(exec.getStatisticsTree());
    }
}
