
package bgu.dcr.az.algo.autogen;

import bgu.dcr.az.conf.api.JavaDocInfo;
import static bgu.dcr.az.conf.autogen.bgu_dcr_az_mui_app_SBB.METHOD_ACCESSOR;
import bgu.dcr.az.dcr.execution.manipulators.AgentManipulator;
import bgu.dcr.az.conf.utils.JavaTypeParser;
import bgu.dcr.az.conf.utils.JavaDocParser;
import bgu.dcr.az.dcr.execution.manipulators.HandlerInfo;
import bgu.dcr.az.dcr.execution.manipulators.ParameterInfo;
import bgu.dcr.az.execs.sim.Agent;

public class bgu_dcr_az_mui_app_SBB extends bgu_dcr_az_dcr_api_SimpleAgent{
    
    //store message related methods
    public static final int HANDLECPA_CPA_IDX = METHOD_ACCESSOR.getIndex("handleCPA", new Class[]{bgu.dcr.az.dcr.api.Assignment.class});
    public static final int HANDLEBACKTRACK_BACKTRACK_IDX = METHOD_ACCESSOR.getIndex("handleBACKTRACK", new Class[]{bgu.dcr.az.dcr.api.Assignment.class, bgu.dcr.az.dcr.api.Assignment.class});

    //store handler javadoc
    public static final JavaDocInfo HANDLECPA_CPA_DOC = JavaDocParser.parse("");
    public static final JavaDocInfo HANDLEBACKTRACK_BACKTRACK_DOC = JavaDocParser.parse("");

    //store handler parameter info
    public static final ParameterInfo HANDLECPA_CPA_CPA = new ParameterInfo("cpa", JavaTypeParser.parse("bgu.dcr.az.dcr.api.Assignment"));
    public static final ParameterInfo HANDLEBACKTRACK_BACKTRACK_CPA = new ParameterInfo("cpa", JavaTypeParser.parse("bgu.dcr.az.dcr.api.Assignment"));
    public static final ParameterInfo HANDLEBACKTRACK_BACKTRACK_BEST = new ParameterInfo("best", JavaTypeParser.parse("bgu.dcr.az.dcr.api.Assignment"));

    //store handler info
    public static final HandlerInfo HANDLECPA_CPA_HINFO = new HandlerInfo(HANDLECPA_CPA_DOC, "CPA", new ParameterInfo[]{HANDLECPA_CPA_CPA});
    public static final HandlerInfo HANDLEBACKTRACK_BACKTRACK_HINFO = new HandlerInfo(HANDLEBACKTRACK_BACKTRACK_DOC, "BACKTRACK", new ParameterInfo[]{HANDLEBACKTRACK_BACKTRACK_CPA, HANDLEBACKTRACK_BACKTRACK_BEST});

    public bgu_dcr_az_mui_app_SBB(){
        super.configurationDelegate = new bgu.dcr.az.conf.autogen.bgu_dcr_az_mui_app_SBB();
        super.algorithmName = "_SBB";

        super.handlers.add(HANDLECPA_CPA_HINFO);
        super.handlers.add(HANDLEBACKTRACK_BACKTRACK_HINFO);
    }

    @Override
    public void handle(Agent a, String messageName, Object[] arguments) {
        switch (messageName){
            case "CPA":
                METHOD_ACCESSOR.invoke(a, HANDLECPA_CPA_IDX, arguments);
                break;
            case "BACKTRACK":
                METHOD_ACCESSOR.invoke(a, HANDLEBACKTRACK_BACKTRACK_IDX, arguments);
                break;
            default:
                super.handle(a, messageName, arguments);
        }
    }

}