

package bgu.dcr.az.conf.autogen;

import bgu.dcr.az.conf.api.JavaDocInfo;
import bgu.dcr.az.conf.utils.JavaDocParser;
import bgu.dcr.az.mui.BaseController;
import bgu.dcr.az.mui.Controller;
import bgu.dcr.az.mui.ControllerManipulator;
import bgu.dcr.az.mui.ControllerRegistery;
import bgu.dcr.az.conf.registery.RegistrationMarker;

public class bgu_dcr_az_mui_scr_log_LogPage implements ControllerManipulator, RegistrationMarker{
    
    //store configured class javadoc
    public static JavaDocInfo DOC = JavaDocParser.parse("FXML Controller class\n\n @author user\n @title Logger\n @tabIndex 1");

    //registration on class loading time
    static {
        ControllerRegistery.get().register(new bgu_dcr_az_mui_scr_log_LogPage(), "main.pages.loggers");
    }

    @Override
    public boolean accept(BaseController container) {
        return bgu.dcr.az.mui.scr.log.LogPage.accept(container);
    }

    @Override
    public Controller create(BaseController c) {
        Controller cc = bgu.dcr.az.mui.scr.log.LogPage.create(bgu.dcr.az.mui.scr.log.LogPage.class);
        if (c != null) c.install(cc);
        return cc;
    }

    @Override
    public JavaDocInfo doc() {
        return DOC;
    }
    
    @Override
    public String toString() {
        return "bgu.dcr.az.mui.scr.log.LogPage";
    }

    @Override
    public Class controllerClass() {
        return bgu.dcr.az.mui.scr.log.LogPage.class;
    }
    
}
