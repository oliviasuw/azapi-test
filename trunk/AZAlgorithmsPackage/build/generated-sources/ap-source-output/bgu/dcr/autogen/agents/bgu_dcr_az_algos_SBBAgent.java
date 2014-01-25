package bgu.dcr.autogen.agents;

import bgu.dcr.az.anop.algo.ParameterInfo;
import bgu.dcr.az.anop.algo.impl.AbstractAgentManipulator;
import bgu.dcr.az.anop.algo.impl.HanlerInfoImpl;
import bgu.dcr.az.anop.algo.impl.ParameterInfoImpl;
import bgu.dcr.az.anop.conf.JavaDocInfo;
import bgu.dcr.az.anop.utils.JavaDocParser;
import bgu.dcr.az.anop.utils.JavaTypeParser;
import java.util.LinkedList;

    
public class bgu_dcr_az_algos_SBBAgent extends AbstractAgentManipulator{
    
    public static final ParameterInfo[] HANDLE_C_P_A_PARAM_INFO = {
        
        new ParameterInfoImpl("cpa", JavaTypeParser.parse("bgu.dcr.az.api.tools.Assignment"))
        
    };
    public static final JavaDocInfo HANDLE_C_P_A_JAVADOC_INFO = JavaDocParser.parse("");    
    
    public static final ParameterInfo[] HANDLE_B_A_C_K_T_R_A_C_K_PARAM_INFO = {
        
        new ParameterInfoImpl("cpa", JavaTypeParser.parse("bgu.dcr.az.api.tools.Assignment"))
        ,
        new ParameterInfoImpl("best", JavaTypeParser.parse("bgu.dcr.az.api.tools.Assignment"))
        
    };
    public static final JavaDocInfo HANDLE_B_A_C_K_T_R_A_C_K_JAVADOC_INFO = JavaDocParser.parse("");    
    
    public static final ParameterInfo[] HANDLE_TERMINATION_PARAM_INFO = {
        
    };
    public static final JavaDocInfo HANDLE_TERMINATION_JAVADOC_INFO = JavaDocParser.parse("");    
    
    
    public bgu_dcr_az_algos_SBBAgent() {
        super.algorithmName = "SBB";
        super.configurationDelegate = new bgu.dcr.autogen.bgu_dcr_az_algos_SBBAgent();
        super.handlers = new LinkedList<>();
        
         
        handlers.add(new HanlerInfoImpl(HANDLE_C_P_A_JAVADOC_INFO, "CPA", HANDLE_C_P_A_PARAM_INFO));
         
        handlers.add(new HanlerInfoImpl(HANDLE_B_A_C_K_T_R_A_C_K_JAVADOC_INFO, "BACKTRACK", HANDLE_B_A_C_K_T_R_A_C_K_PARAM_INFO));
         
        handlers.add(new HanlerInfoImpl(HANDLE_TERMINATION_JAVADOC_INFO, "__TERMINATE__", HANDLE_TERMINATION_PARAM_INFO));
        
    }

    @Override
    public void callHandler(Object agentInstance, String name, Object[] arguments) {
        bgu.dcr.az.algos.SBBAgent agent = (bgu.dcr.az.algos.SBBAgent)agentInstance;
        
        switch(name) {
             
            case "CPA":
                agent.handleCPA((bgu.dcr.az.api.tools.Assignment)arguments[0]);
                break;
             
            case "BACKTRACK":
                agent.handleBACKTRACK((bgu.dcr.az.api.tools.Assignment)arguments[0], (bgu.dcr.az.api.tools.Assignment)arguments[1]);
                break;
             
            case "__TERMINATE__":
                agent.handleTermination();
                break;
            
            default:
                throw new RuntimeException("Unsupported message: " + name + "received");
        }

    }
    
}
