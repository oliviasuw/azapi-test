/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.ui.confe;

import bgu.dcr.az.anop.conf.Property;
import bgu.dcr.az.anop.conf.impl.FromConfigurationPropertyValue;
import bgu.dcr.az.anop.conf.impl.FromStringPropertyValue;
import bgu.dcr.az.anop.reg.RegisteryUtils;
import java.net.URL;
import java.util.Collection;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javax.swing.event.DocumentEvent;

/**
 * FXML Controller class
 *
 * @author Shl
 */
public class ConfigurationPropertyEditorController implements Initializable {

    @FXML
    ConfigurationEditorController confEditorController;

    @FXML
    ChoiceBox choiceBox;

    @FXML
    TitledPane titledPane;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println("ConfEditor is : " + confEditorController);
    }

    public void setModel(final Property property) {
        titledPane.setText(property.name());
        Tooltip tooltip = new Tooltip(property.doc().description());
        titledPane.setTooltip(tooltip);

        Class pType = property.typeInfo().getType();
        Collection<Class> implementors = RegisteryUtils.getDefaultRegistery().getImplementors(pType);
        choiceBox.getItems().clear();
        for (Class implementor : implementors) {
            choiceBox.getItems().add(RegisteryUtils.getDefaultRegistery().getRegisteredClassName(implementor));
        }
        
        choiceBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue ov, Object t, Object t1) {
                try {
                    FromConfigurationPropertyValue confv = new FromConfigurationPropertyValue(RegisteryUtils.getDefaultRegistery().getConfiguration(t1.toString()));
                    property.set(confv);
                    confEditorController.setModel(confv.getValue());
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(ConfigurationPropertyEditorController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        });

    }

}
