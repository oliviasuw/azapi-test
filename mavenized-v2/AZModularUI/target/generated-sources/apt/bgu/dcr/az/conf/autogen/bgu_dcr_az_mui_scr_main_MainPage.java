

package bgu.dcr.az.conf.autogen;

import bgu.dcr.az.conf.api.JavaDocInfo;
import bgu.dcr.az.conf.utils.JavaDocParser;
import bgu.dcr.az.mui.Controller;
import bgu.dcr.az.mui.ControllerManipulator;
import bgu.dcr.az.mui.ControllerRegistery;
import bgu.dcr.az.conf.registery.RegistrationMarker;

public class bgu_dcr_az_mui_scr_main_MainPage implements ControllerManipulator, RegistrationMarker{
    
    //store configured class javadoc
    public static JavaDocInfo DOC = JavaDocParser.parse("FXML Controller class\n\n @author bennyl");

    //registration on class loading time
    static {
        ControllerRegistery.get().register(new bgu_dcr_az_mui_scr_main_MainPage(), "main");
    }

    @Override
    public boolean accept(Controller container) {
        return bgu.dcr.az.mui.scr.main.MainPage.accept(container);
    }

    @Override
    public Controller create(Controller c) {
        Controller cc = bgu.dcr.az.mui.scr.main.MainPage.create(bgu.dcr.az.mui.scr.main.MainPage.class);
        if (c != null) c.manage(cc);
        return cc;
    }

    @Override
    public JavaDocInfo doc() {
        return DOC;
    }
    
    @Override
    public String toString() {
        return "bgu.dcr.az.mui.scr.main.MainPage";
    }

    @Override
    public Class controllerClass() {
        return bgu.dcr.az.mui.scr.main.MainPage.class;
    }
    
}
