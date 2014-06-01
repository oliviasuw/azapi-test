

package bgu.dcr.az.conf.autogen;

import bgu.dcr.az.conf.AbstractConfiguration;
import bgu.dcr.az.conf.PropertyImpl;
import bgu.dcr.az.conf.api.JavaDocInfo;
import bgu.dcr.az.conf.api.TypeInfo;
import bgu.dcr.az.conf.registery.Registery;
import bgu.dcr.az.conf.utils.JavaDocParser;
import bgu.dcr.az.conf.utils.JavaTypeParser;
import com.esotericsoftware.reflectasm.ConstructorAccess;
import com.esotericsoftware.reflectasm.MethodAccess;
import bgu.dcr.az.conf.registery.RegistrationMarker;

public class ext_sim_agents_dcop_SBB extends AbstractConfiguration implements RegistrationMarker{
    
    //store type info for each property
    
    
    //store javadoc for each property
    

    //store configured class javadoc
    public static JavaDocInfo DOC = JavaDocParser.parse("");

    //store accessors for fast access
    public static final MethodAccess METHOD_ACCESSOR = MethodAccess.get(ext.sim.agents.dcop.SBB.class);
    public static final ConstructorAccess CONSTRUCTOR_ACCESSOR = ConstructorAccess.get(ext.sim.agents.dcop.SBB.class);

    //store access index for me and each of my configurable parents properties
    

    //registration on class loading time
    static {
        Registery.get().register(ext.sim.agents.dcop.SBB.class, "ALGORITHM.SBB");
    }

    public ext_sim_agents_dcop_SBB() {
        super.type = ext.sim.agents.dcop.SBB.class;
        super.javadoc = DOC;
        super.accessor = METHOD_ACCESSOR;
        super.cAccessor = CONSTRUCTOR_ACCESSOR;

        //insert all properties
        

        
    }
    
}