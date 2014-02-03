/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.ui.confe;

import bgu.dcr.az.anop.conf.Configuration;
import bgu.dcr.az.anop.conf.Property;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.LinkedList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author Shl
 */
public class ConfigurationEditorController implements Initializable {

    @FXML
    private VBox vbox;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    public void setModel(Configuration conf) {
        vbox.getChildren().clear();
        
        vbox.setSpacing(3);
        vbox.setPadding(new Insets(5));

//        Class c = null; 
//        Collection<Class> cs = RegisteryUtils.getDefaultRegistery().getImplementors(c);
//        Configuration confc = RegisteryUtils.getDefaultRegistery().getConfiguration(cs.iterator().next());
        Collection<Property> properties = conf.properties();
        double max = 0;
        LinkedList<TerminalPropertyEditorController> controllerList = new LinkedList<>();

        for (Property property : properties) {
            if (isPrimitive(property)) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("TerminalPropertyEditor.fxml"));
                    Node node = (Node) loader.load();
                    TerminalPropertyEditorController controller = (TerminalPropertyEditorController) loader.getController();
                    controller.setModel(property);
                    double labelWidth = controller.getLabelWidth();
                    if (labelWidth > max) {
                        max = labelWidth;
                    }
                    controllerList.add(controller);
                    vbox.getChildren().add(node);
                } catch (IOException ex) {
                    System.out.println("");
                }

            }
        }
        for (TerminalPropertyEditorController controller : controllerList) {
            controller.setLabelWidth(max);
        }
        
        for (Property property : properties) {
         if (!isPrimitive(property)) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("ConfigurationPropertyEditor.fxml"));
                    Node node = (Node) loader.load();
                    ConfigurationPropertyEditorController controller = (ConfigurationPropertyEditorController) loader.getController();
                    controller.setModel(property);
                    vbox.getChildren().add(node);
                } catch (IOException ex) {
                    Logger.getLogger(ConfigurationEditorController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
    }

    public boolean isPrimitive(Property property) {
        Class pType = property.typeInfo().getType();
        return String.class.isAssignableFrom(pType)
                || Integer.class.isAssignableFrom(pType)
                || Boolean.class.isAssignableFrom(pType)
                || Double.class.isAssignableFrom(pType)
                || Float.class.isAssignableFrom(pType)
                || pType.isEnum()
                || Character.class.isAssignableFrom(pType)
                || Byte.class.isAssignableFrom(pType)
                || Short.class.isAssignableFrom(pType)
                || Long.class.isAssignableFrom(pType);

    }

}
