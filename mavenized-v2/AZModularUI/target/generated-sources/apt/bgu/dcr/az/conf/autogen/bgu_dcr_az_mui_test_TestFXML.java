

package bgu.dcr.az.conf.autogen;

import bgu.dcr.az.conf.api.JavaDocInfo;
import bgu.dcr.az.conf.utils.JavaDocParser;
import bgu.dcr.az.mui.View;
import bgu.dcr.az.mui.ViewContainer;
import bgu.dcr.az.mui.ViewManipulator;
import bgu.dcr.az.mui.ViewRegistery;
import com.esotericsoftware.reflectasm.ConstructorAccess;
import bgu.dcr.az.conf.registery.RegistrationMarker;

public class bgu_dcr_az_mui_test_TestFXML implements ViewManipulator, RegistrationMarker{
    
    //store configured class javadoc
    public static JavaDocInfo DOC = JavaDocParser.parse("FXML Controller class\n\n @author bennyl");

    //store accessors for fast access
    public static final ConstructorAccess CONSTRUCTOR_ACCESSOR = ConstructorAccess.get(bgu.dcr.az.mui.test.TestFXML.class);

    //registration on class loading time
    static {
        ViewRegistery.get().register(new bgu_dcr_az_mui_test_TestFXML(), "test.fxml.test");
    }

    @Override
    public boolean accept(ViewContainer container) {
        return bgu.dcr.az.mui.test.TestFXML.accept(container);
    }

    @Override
    public View create(ViewContainer container) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public JavaDocInfo doc() {
        return DOC;
    }
    
    @Override
    public String toString() {
        return "bgu.dcr.az.mui.test.TestFXML";
    }
    
}
