/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.ui.confe;

import bgu.dcr.az.anop.conf.Configuration;
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
public class ConfigurationPropertyEditorController implements Initializable, PropertyController {
    
    ConfigurationEditor confEditor;
    
    @FXML
    ChoiceBox choiceBox;
    
    @FXML
    TitledPane titledPane;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
//        System.out.println("ConfEditor is : " + confEditorController);
        confEditor = new ConfigurationEditor();
    }
    
    @Override
    public void setModel(final Property property, final boolean readOnly) {
        titledPane.setText(property.name());
        String description = property.doc().description();
        if (description != null && !description.isEmpty()) {
            Tooltip tooltip = new Tooltip(description);
            titledPane.setTooltip(tooltip);
        }
        Class pType = property.typeInfo().getType();
        Collection<Class> implementors = RegisteryUtils.getDefaultRegistery().getImplementors(pType);
        choiceBox.getItems().clear();
        for (Class implementor : implementors) {
            String registeredClassName = RegisteryUtils.getDefaultRegistery().getRegisteredClassName(implementor);
            choiceBox.getItems().add(registeredClassName);
        }
        
        if (property.get() != null) {
            FromConfigurationPropertyValue confv = (FromConfigurationPropertyValue) property.get();
            if (confv != null && confv.getValue() != null) { //in case property is a dummy (came from collection property editor)
                Class implementor = confv.getValue().typeInfo().getType();
                String classname = RegisteryUtils.getDefaultRegistery().getRegisteredClassName(implementor);
                choiceBox.getSelectionModel().select(classname);
                confEditor.setModel(confv.getValue(), readOnly);
            }
        }
        
        choiceBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue ov, Object t, Object t1) {
                try {
                    FromConfigurationPropertyValue confv = new FromConfigurationPropertyValue(RegisteryUtils.getDefaultRegistery().getConfiguration(t1.toString()));
                    property.set(confv);
                    confEditor.setModel(confv.getValue(), readOnly);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(ConfigurationPropertyEditorController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
        });
        choiceBox.setDisable(readOnly);
    }
    
    @Override
    public void setModel(Configuration conf, boolean readOnly) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
