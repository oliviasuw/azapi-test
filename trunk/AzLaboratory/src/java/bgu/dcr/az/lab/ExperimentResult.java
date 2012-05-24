/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.lab;

import java.util.Date;

/**
 *
 * @author Inna
 */
public class ExperimentResult {
    String title;
    String imgUrl;
    Date date;

    public Date getDate() {
        return date;
    }

    public String getTitle() {
        return title;
    }

    public String getImgUrl() {
        return imgUrl;
    }
    
    

    public ExperimentResult(String title, String imgUrl, Date date) {
        this.title = title;
        this.imgUrl = imgUrl;
        this.date = date;
    }
    
    
    
}
