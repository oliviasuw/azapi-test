

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

public class bgu_dcr_az_mui_app_SBB extends AbstractConfiguration implements RegistrationMarker{
    
    //store type info for each property
    
    
    //store javadoc for each property
    

    //store requested name for each attribute
    

    //store configured class javadoc
    public static JavaDocInfo DOC = JavaDocParser.parse("");

    //store accessors for fast access
    public static final MethodAccess METHOD_ACCESSOR = MethodAccess.get(bgu.dcr.az.mui.app.SBB.class);
    public static final ConstructorAccess CONSTRUCTOR_ACCESSOR = ConstructorAccess.get(bgu.dcr.az.mui.app.SBB.class);

    //store access index for me and each of my configurable parents properties
    

    //registration on class loading time
    static {
        Registery.get().register(bgu.dcr.az.mui.app.SBB.class, "ALGORITHM.SBB");
    }

    protected bgu_dcr_az_mui_app_SBB(int i){
        //do nothing on porpose!
    }

    public bgu_dcr_az_mui_app_SBB() {
        super();
        super.type = bgu.dcr.az.mui.app.SBB.class;
        super.javadoc = DOC;
        super.accessor = METHOD_ACCESSOR;
        super.cAccessor = CONSTRUCTOR_ACCESSOR;

        //insert all properties
        

        
    }
    
}