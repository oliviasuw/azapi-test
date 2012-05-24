/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.lab;

import java.io.File;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;

/**
 *
 * @author Inna
 */
@ManagedBean(eager = true)
@ApplicationScoped
public class PicList {
    private String[][] pics = {{"1", "2", "3"},{"4", "5", "6"},{"7", "8", "9"}};

    public String[][] getPics() {
        return pics;
    }

    public String gotoOther() {
        return "main/images/pics/other";
    }

    public String picName(String name) {
        return "/AzLaboratory/faces/images/pic/" + name + ".png";
    }

}
