/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import java.util.logging.Level;
import java.util.logging.Logger;
import models.Item;
import models.Item.ItemResolveException;
import models.Item.UnknownItemTypeException;
import models.ServerError;
import play.mvc.Controller;

/**
 *
 * @author bennyl
 */
public class ExperimentCreator extends Controller{
    
    public static void getItems(String of){
        try {
            renderJSON(Item.getItems(of));
        } catch (Exception ex) {
            ex.printStackTrace();
            renderJSON(new ServerError(ex));
        } 
    }
    
    
    public static void expc(){
        render();
    }
}
