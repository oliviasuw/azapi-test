

package bgu.dcr.az.conf.autogen;

import bgu.dcr.az.conf.api.JavaDocInfo;
import bgu.dcr.az.conf.utils.JavaDocParser;
import bgu.dcr.az.mui.Controller;
import bgu.dcr.az.mui.ControllerManipulator;
import bgu.dcr.az.mui.ControllerRegistery;
import bgu.dcr.az.conf.registery.RegistrationMarker;

public class bgu_dcr_az_mui_test_TestFXML implements ControllerManipulator, RegistrationMarker{
    
    //store configured class javadoc
    public static JavaDocInfo DOC = JavaDocParser.parse("FXML Controller class\n\n @title test");

    //registration on class loading time
    static {
        ControllerRegistery.get().register(new bgu_dcr_az_mui_test_TestFXML(), "main.pages.test");
    }

    @Override
    public boolean accept(Controller container) {
        return true;
    }

    @Override
    public Controller create(Controller c) {
        Controller cc = bgu.dcr.az.mui.test.TestFXML.create(bgu.dcr.az.mui.test.TestFXML.class);
        if (c != null) c.manage(cc);
        return cc;
    }

    @Override
    public JavaDocInfo doc() {
        return DOC;
    }
    
    @Override
    public String toString() {
        return "bgu.dcr.az.mui.test.TestFXML";
    }

    @Override
    public Class controllerClass() {
        return bgu.dcr.az.mui.test.TestFXML.class;
    }
    
}
