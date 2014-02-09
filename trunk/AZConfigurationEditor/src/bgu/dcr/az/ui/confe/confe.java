/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.ui.confe;

import bgu.dcr.az.anop.conf.Configuration;
import bgu.dcr.az.anop.conf.ConfigurationException;
import bgu.dcr.az.anop.conf.ConfigurationUtils;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import test.delete.me.SomeClass;

/**
 *
 * @author Shl
 */
public class confe extends Application {

    @Override
    public void start(Stage stage) throws Exception {
//        Parent root = FXMLLoader.load(getClass().getResource("ConfigurationPropertyEditor.fxml"));
//         Parent root = FXMLLoader.load(getClass().getResource("TerminalPropertyEditor.fxml"));

        SomeClass c = new SomeClass();
        c.setJ("bla bla benjamin button");
        c.setK(true);
        c.setE(SomeClass.E.MEH);
        SomeClass.ExtendedSomeType2 inside = new SomeClass.ExtendedSomeType2();
        inside.setI(2);
        inside.setX(4);
        inside.setOther(new SomeClass.ExtendedSomeType1());
        c.setComplex(inside);
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("benny");
        arrayList.add("vadim");
        arrayList.add("clint eastwood");
        c.setNames(arrayList);
        final Configuration conf = ConfigurationUtils.load(c);
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ConfigurationEditor.fxml"));
        VBox vbox = (VBox)loader.load();
        ConfigurationEditorController controller = (ConfigurationEditorController)loader.getController();
        controller.setModel(conf, true);

        Button button = new Button(":/");
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    System.out.println("" + ConfigurationUtils.toXML(conf).toXML());
                } catch (ConfigurationException ex) {
                    Logger.getLogger(confe.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        vbox.getChildren().add(button);

        Scene scene = new Scene(vbox);

        stage.setScene(scene);
        stage.show();
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
