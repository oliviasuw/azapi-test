package ext.sim.modules;

import bgu.dcr.az.api.Message;
import bgu.dcr.az.api.ano.Register;
import bgu.dcr.az.api.ano.Variable;
import bgu.dcr.az.api.infra.Execution;
import bgu.dcr.az.api.mdelay.MessageDelayer;
import java.util.Random;

@Register(name = "${MODULE_NAME}")
public class ${MODULE_NAME_CC} implements MessageDelayer {

    //ADD ANY VARIABLES YOU NEED HERE LIKE THIS:
    //@Variable(name = "maximum-delay",
    //          description = "the maximum delay that the delayer can produce for a message",
    //          defaultValue = "100")
    //int maximumDelay;
	
	
    @Override
    public int getInitialTime() {
        return 0;
    }

    @Override
    public int extractTime(Message m) {
    	//TODO: EXTRACT AND RETURN THE TIME FROM THE MESSAGE METADATA
    	return 0;
    }

    @Override
    public void addDelay(Message m, int from, int to) {
    	//TODO: CHANGE THE TIME THAT IS SAVED IN THE MESSAGE METADATA TO A DELAYED TIME
    }

    @Override
    public void initialize(Execution ex) {
    	//TODO: INITIALIZE THE MESSAGE DELAYER FOR THE GIVEN EXECUTION
    	//NOTE THAT THERE IS ONLY ONE MESSAGE DELAYER FOR ALL THE EXECUTIONS IN A TEST
    }
}
