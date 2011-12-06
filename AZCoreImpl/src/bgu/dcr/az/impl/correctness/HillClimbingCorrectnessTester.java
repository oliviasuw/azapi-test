/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.impl.correctness;

import bgu.dcr.az.api.ano.Register;
import bgu.dcr.az.api.ano.Variable;
import bgu.dcr.az.api.infra.Execution;
import bgu.dcr.az.api.infra.ExecutionResult;
import bgu.dcr.az.api.infra.stat.Database;
import bgu.dcr.az.impl.db.DatabaseUnit;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bennyl
 */
@Register(name = "hill-climbing-tester")
public class HillClimbingCorrectnessTester extends AbstractCorrectnessTester {

    @Variable(name="check-maximization", description="check that the algorithm find result with more(true)/less(false) cost in each tick")
    boolean maxi = true;
    
    @Override
    public TestResult test(Execution exec, ExecutionResult result) {
        try {
            DatabaseUnit.UNIT.awaitStatistics();
            Database db = DatabaseUnit.UNIT.getDatabase();

            String sign = (maxi? ">": "<");
            ResultSet res = db.query(""
                    + "SELECT * FROM "
                    + "SOLUTION_QUALITY as a, "
                    + "SOLUTION_QUALITY as b "
                    + "WHERE a.ticknum > b.ticknum "
                    + "AND a.solquality " + sign + " b.solquality");
            if (res.next()) {
                return new TestResult(null, false);
            } else {
                return new TestResult(null, true);
            }


        } catch (SQLException ex) {
            Logger.getLogger(HillClimbingCorrectnessTester.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        return null;
    }
}
