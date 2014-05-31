/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.mui.jfx;

import bgu.dcr.az.mui.Controller;
import bgu.dcr.az.mui.ControllerRegistery;
import bgu.dcr.az.mui.RootController;
import java.io.IOException;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author bennyl
 */
public class NewFXMain extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        RootController root = new RootController();
        Controller p = root.findAndManage("main");

        root.loadView();
        Scene scene = new Scene((Parent) p.getView());

        primaryStage.setTitle("Hello World!");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
