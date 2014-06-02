/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.mui.scr.status.cp;

import bgu.dcr.az.dcr.modules.progress.CPProgress;
import bgu.dcr.az.execs.exps.ModularExperiment;
import bgu.dcr.az.mui.BaseController;
import bgu.dcr.az.mui.RegisterController;
import bgu.dcr.az.mui.jfx.FXMLController;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;

/**
 * FXML Controller class
 *
 * @author bennyl
 */
@RegisterController("status.minidash")
public class CPMiniDashboard extends FXMLController {

    CPProgress progress;
    
    @Override
    protected void onLoadView() {
        progress = require(ModularExperiment.class).require(CPProgress.class);
        
    }

    public static boolean accept(BaseController c) {
        ModularExperiment me = c.get(ModularExperiment.class);
        if (me == null) {
            return false;
        }
        
        return me.isInstalled(CPProgress.class);
    }

}
