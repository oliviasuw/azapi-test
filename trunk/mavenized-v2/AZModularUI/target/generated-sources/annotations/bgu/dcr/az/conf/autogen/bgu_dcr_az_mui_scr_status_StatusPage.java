

package bgu.dcr.az.conf.autogen;

import bgu.dcr.az.conf.api.JavaDocInfo;
import bgu.dcr.az.conf.utils.JavaDocParser;
import bgu.dcr.az.mui.BaseController;
import bgu.dcr.az.mui.Controller;
import bgu.dcr.az.mui.ControllerManipulator;
import bgu.dcr.az.mui.ControllerRegistery;
import bgu.dcr.az.conf.registery.RegistrationMarker;

public class bgu_dcr_az_mui_scr_status_StatusPage implements ControllerManipulator, RegistrationMarker{
    
    //store configured class javadoc
    public static JavaDocInfo DOC = JavaDocParser.parse("FXML Controller class\n\n @title Status\n @index 0000");

    //registration on class loading time
    static {
        ControllerRegistery.get().register(new bgu_dcr_az_mui_scr_status_StatusPage(), "main.pages.status");
    }

    @Override
    public boolean accept(BaseController container) {
        return bgu.dcr.az.mui.scr.status.StatusPage.accept(container);
    }

    @Override
    public Controller create(BaseController c) {
        Controller cc = bgu.dcr.az.mui.scr.status.StatusPage.create(bgu.dcr.az.mui.scr.status.StatusPage.class);
        if (c != null) c.install(cc);
        return cc;
    }

    @Override
    public JavaDocInfo doc() {
        return DOC;
    }
    
    @Override
    public String toString() {
        return "bgu.dcr.az.mui.scr.status.StatusPage";
    }

    @Override
    public Class controllerClass() {
        return bgu.dcr.az.mui.scr.status.StatusPage.class;
    }
    
}
