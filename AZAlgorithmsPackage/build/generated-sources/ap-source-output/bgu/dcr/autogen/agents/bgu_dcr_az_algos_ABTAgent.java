package bgu.dcr.autogen.agents;

import bgu.dcr.az.anop.algo.ParameterInfo;
import bgu.dcr.az.anop.algo.impl.AbstractAgentManipulator;
import bgu.dcr.az.anop.algo.impl.HanlerInfoImpl;
import bgu.dcr.az.anop.algo.impl.ParameterInfoImpl;
import bgu.dcr.az.anop.conf.JavaDocInfo;
import bgu.dcr.az.anop.utils.JavaDocParser;
import bgu.dcr.az.anop.utils.JavaTypeParser;
import java.util.LinkedList;

    
public class bgu_dcr_az_algos_ABTAgent extends AbstractAgentManipulator{
    
    public static final ParameterInfo[] HANDLE_A_D_D__L_I_N_K_PARAM_INFO = {
        
        new ParameterInfoImpl("neighbor", JavaTypeParser.parse("java.lang.Integer"))
        
    };
    public static final JavaDocInfo HANDLE_A_D_D__L_I_N_K_JAVADOC_INFO = JavaDocParser.parse("");    
    
    public static final ParameterInfo[] HANDLE_N_O__G_O_O_D_PARAM_INFO = {
        
        new ParameterInfoImpl("var", JavaTypeParser.parse("java.lang.Integer"))
        ,
        new ParameterInfoImpl("nogood", JavaTypeParser.parse("bgu.dcr.az.tools.Explanation"))
        
    };
    public static final JavaDocInfo HANDLE_N_O__G_O_O_D_JAVADOC_INFO = JavaDocParser.parse("");    
    
    public static final ParameterInfo[] HANDLE_O_K_PARAM_INFO = {
        
        new ParameterInfoImpl("var", JavaTypeParser.parse("java.lang.Integer"))
        ,
        new ParameterInfoImpl("val", JavaTypeParser.parse("java.lang.Integer"))
        
    };
    public static final JavaDocInfo HANDLE_O_K_JAVADOC_INFO = JavaDocParser.parse("");    
    
    public static final ParameterInfo[] HANDLE_TERMINATION_PARAM_INFO = {
        
    };
    public static final JavaDocInfo HANDLE_TERMINATION_JAVADOC_INFO = JavaDocParser.parse("");    
    
    
    public bgu_dcr_az_algos_ABTAgent() {
        super.algorithmName = "ABT";
        super.configurationDelegate = new bgu.dcr.autogen.bgu_dcr_az_algos_ABTAgent();
        super.handlers = new LinkedList<>();
        
         
        handlers.add(new HanlerInfoImpl(HANDLE_A_D_D__L_I_N_K_JAVADOC_INFO, "ADD_LINK", HANDLE_A_D_D__L_I_N_K_PARAM_INFO));
         
        handlers.add(new HanlerInfoImpl(HANDLE_N_O__G_O_O_D_JAVADOC_INFO, "NO_GOOD", HANDLE_N_O__G_O_O_D_PARAM_INFO));
         
        handlers.add(new HanlerInfoImpl(HANDLE_O_K_JAVADOC_INFO, "OK?", HANDLE_O_K_PARAM_INFO));
         
        handlers.add(new HanlerInfoImpl(HANDLE_TERMINATION_JAVADOC_INFO, "__TERMINATE__", HANDLE_TERMINATION_PARAM_INFO));
        
    }

    @Override
    public void callHandler(Object agentInstance, String name, Object[] arguments) {
        bgu.dcr.az.algos.ABTAgent agent = (bgu.dcr.az.algos.ABTAgent)agentInstance;
        
        switch(name) {
             
            case "ADD_LINK":
                agent.handleADD_LINK((java.lang.Integer)arguments[0]);
                break;
             
            case "NO_GOOD":
                agent.handleNO_GOOD((java.lang.Integer)arguments[0], (bgu.dcr.az.tools.Explanation)arguments[1]);
                break;
             
            case "OK?":
                agent.handleOK((java.lang.Integer)arguments[0], (java.lang.Integer)arguments[1]);
                break;
             
            case "__TERMINATE__":
                agent.handleTermination();
                break;
            
            default:
                throw new RuntimeException("Unsupported message: " + name + "received");
        }

    }
    
}
