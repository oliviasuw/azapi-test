

package bgu.dcr.az.conf.autogen;

import bgu.dcr.az.conf.api.JavaDocInfo;
import bgu.dcr.az.conf.utils.JavaDocParser;
import bgu.dcr.az.mui.BaseController;
import bgu.dcr.az.mui.Controller;
import bgu.dcr.az.mui.ControllerManipulator;
import bgu.dcr.az.mui.ControllerRegistery;
import bgu.dcr.az.conf.registery.RegistrationMarker;

public class bgu_dcr_az_mui_scr_statistics_adv_AdvancedStatisticScreen implements ControllerManipulator, RegistrationMarker{
    
    //store configured class javadoc
    public static JavaDocInfo DOC = JavaDocParser.parse("FXML Controller class\n\n @author bennyl\n @title advanced\n @index 1000");

    //registration on class loading time
    static {
        ControllerRegistery.get().register(new bgu_dcr_az_mui_scr_statistics_adv_AdvancedStatisticScreen(), "statistics.pages.advanced");
    }

    @Override
    public boolean accept(BaseController container) {
        return true;
    }

    @Override
    public Controller create(BaseController c) {
        Controller cc = bgu.dcr.az.mui.scr.statistics.adv.AdvancedStatisticScreen.create(bgu.dcr.az.mui.scr.statistics.adv.AdvancedStatisticScreen.class);
        if (c != null) c.install(cc);
        return cc;
    }

    @Override
    public JavaDocInfo doc() {
        return DOC;
    }
    
    @Override
    public String toString() {
        return "bgu.dcr.az.mui.scr.statistics.adv.AdvancedStatisticScreen";
    }

    @Override
    public Class controllerClass() {
        return bgu.dcr.az.mui.scr.statistics.adv.AdvancedStatisticScreen.class;
    }
    
}
