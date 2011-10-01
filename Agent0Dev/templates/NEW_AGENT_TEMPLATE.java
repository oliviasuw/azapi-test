package ext.sim.agents;

import bgu.csp.az.api.agt.SimpleAgent;
import bgu.csp.az.api.ano.Algorithm;
import bgu.csp.az.api.ano.WhenReceived;
import bgu.csp.az.api.tools.Assignment;

@Algorithm("${ALGORITHM_NAME}")
public class ${ALGORITHM_NAME}Agent extends SimpleAgent {

    @Override
    public void start() {
    	//TODO: ADD INITIALIZING CODE HERE - DO NOT INITIALIZE ANYTHING IN THE CONSTRACTOR!
        if (isFirstAgent()) {
            //TODO: KICK START THE ALGORITHM
        }
    }

    /* This is an example of message handling, 
     * with Simple Agent you are not required to define new message classes 
     * all you have to do is to define what to do when a message with some name arrived 
     * and what fields it should contain
     *     
     * @WhenReceived("MESSAGE_NAME")
     * public void handleCPA(Type1 field1, Type2 field2, ..., TypeN fieldN) {
 	 * 		Handling code... 
     * }
     * 
     * when you want to send a message all you have to do is call: 
     * send("MESSAGE_NAME", field1, field2, ..., fieldN).to(RECEIVING_AGENT_ID);
     */
}
