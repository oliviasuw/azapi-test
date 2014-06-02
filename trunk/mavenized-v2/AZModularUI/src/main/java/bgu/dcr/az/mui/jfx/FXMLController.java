/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.mui.jfx;

import bgu.dcr.az.common.exceptions.UnexpectedException;
import bgu.dcr.az.mui.Controller;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

/**
 *
 * @author bennyl
 */
public abstract class FXMLController extends Controller<Parent> {

    private Parent view;

    @Override
    public Parent _getView() {
        return view;
    }

    public static Controller create(Class<? extends Controller> c) {
        try {
            FXMLLoader loader = new FXMLLoader();
            final String fxml = c.getSimpleName() + ".fxml";
            loader.setLocation(c.getResource(fxml));
            Parent v = loader.load(c.getResourceAsStream(fxml));
            FXMLController controller = loader.getController();
            controller.view = v;

            return controller;
        } catch (IOException ex) {
            throw new UnexpectedException(ex);
        }
    }
}
