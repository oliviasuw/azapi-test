package ext.sim.modules;

import bgu.dcr.az.api.Agent;
import bgu.dcr.az.api.ano.Register;
import bgu.dcr.az.api.exen.Execution;
import bgu.dcr.az.api.exen.Test;
import bgu.dcr.az.api.exen.stat.DBRecord;
import bgu.dcr.az.api.exen.stat.Database;
import bgu.dcr.az.api.exen.stat.VisualModel;
import bgu.dcr.az.exen.stat.AbstractStatisticCollector;

@Register(name = "${MODULE_NAME}")
public class ${MODULE_NAME_CC} extends AbstractStatisticCollector<${MODULE_NAME_CC}.${MODULE_NAME_CC}Record> {

    @Override
    public VisualModel analyze(Database db, Test r) {
        //FIRST CREATE A QUERY LIKE THIS:
    	//String query = "select AVG(cc) as avg, rVar, algorithm from ${MODULE_NAME_CC} where TEST = '" + r.getName() + "' group by algorithm";

    	//THEN YOU SHOULD CREATE A GRAPH TO REPRESENT THE QUERY IN LIKE THIS:
    	//LineVisualModel line = new LineVisualModel(r.getRunningVarName(), "Avg(data)", "data");
        
    	//NOW FILL THE GRAPH WITH THE QUERY RESULT LIKE THIS:
    	//try {
        //    ResultSet rs = db.query(query);
        //    while (rs.next()) {
        //        line.setPoint(rs.getString("algorithm"), rs.getFloat("???"), rs.getFloat("avg"));
        //    }
        //    return line;
        //} catch (SQLException ex) {
        //    Logger.getLogger(NCCCStatisticCollector.class.getName()).log(Level.SEVERE, null, ex);
        //}
        return null;
    }

    @Override
    public void hookIn(final Agent[] agents, final Execution ex) {

    	//HOOK INTO THE GIVEN EXECUTION TO COLLECT THE NEEDED DATA FOR YOUR STATISTIC
    	//YOU CAN SEE WHAT HOOKS ARE AVAILABLE IN THE TUTORIAL
    	//TAKE INTO CONSIDERATION THAT THERE IS ONE STATISTIC COLLECTOR OF THIS TYPE 
    	//FOR ALL THE EXECUTIONS IN A TEST
    	//EXAMPLE OF A HOOK:
    	//ex.hookIn(new TerminationHook() {
    	//
        //    @Override
        //    public void hook() {
        //        int sum = 0;
        //        for (Agent ag : agents) {
        //            sum += ag.getNumberOfConstraintChecks();
        //        }
    	//			
    	//	      DONT FORGET TO SUBMIT YOUR RECORDS TO THE DATABASE USING THE SUBMIT FUNCTION
        //        submit(new ${MODULE_NAME_CC}Record(ex.getTest().getCurrentVarValue(), sum, agents[0].getAlgorithmName()));
        //    }
        //});
    }

    @Override
    public String getName() {
        //YOU CAN CHANGE THE RETURNED VALUE SO THAT YOUR STATISTIC COLLECTOR NAME IN THE UI WILL BE MORE PLESENT
    	return "${MODULE_NAME}";
    }

    //THIS IS YOUR RECORD IN THE DATABASE DEFINITION
    public static class ${MODULE_NAME_CC}Record extends DBRecord {
    	
        //YOU CAN ADD "PRIMITIVE" FIELDS AND STRING FIELDS ONLY IN THE RECORD FOR EXAMPLE:
        //double cc;
        String algorithm;

        public ${MODULE_NAME_CC}Record(String algorithm) {
            this.algorithm = algorithm;
        }

        @Override
        public String provideTableName() {
            return "${MODULE_NAME_CC}";
        }
    }
}
