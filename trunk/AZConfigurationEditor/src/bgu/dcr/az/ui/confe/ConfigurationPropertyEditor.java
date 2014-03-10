/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.ui.confe;

import bgu.dcr.az.anop.conf.Configuration;
import bgu.dcr.az.anop.conf.Property;
import bgu.dcr.az.anop.conf.PropertyValue;
import bgu.dcr.az.anop.conf.impl.FromConfigurationPropertyValue;
import bgu.dcr.az.anop.reg.RegisteryUtils;
import bgu.dcr.az.common.ui.FXUtils;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author Shl
 */
public class ConfigurationPropertyEditor extends TitledPane implements PropertyEditor, Selectable {

    private static final int LABEL_MARGING = 10;

    private final ConfigurationEditor parent;

    private final ConfigurationEditorInternal confEditor;
    private final ChoiceBox<String> choiceBox;
    private final Label infoContainer;
    private Property property;
    private final BorderPane implementorsBorderPane;
    private boolean readOnly;
    private final Property parentCollection;
    private final VBox editorVBox;
    
    private Predicate filter;

    private final BooleanProperty selected;

    @Override
    public BooleanProperty selectedProperty() {
        return selected;
    }

    public ConfigurationPropertyEditor(ConfigurationEditor parent, Property collection) {
        this.parent = parent;
        this.parentCollection = collection;

        getStyleClass().add("configuration-property-editor");

        selected = new SimpleBooleanProperty(false);
        
        addEventFilter(MouseEvent.MOUSE_RELEASED, eh -> {
            eh.consume();
        });
        
        addEventFilter(MouseEvent.MOUSE_PRESSED, eh -> {
            Node node = FXUtils.getTitledPaneTitleRegion(this);
            
            if (node != null && node.getParent() == this
                    && node.localToScene(node.getBoundsInLocal()).contains(eh.getSceneX(), eh.getSceneY())) {
                eh.consume();
                
                if (!selected.get()) {
                    selected.set(true);
                } else {
//                    System.out.println("set expended "  + (!isExpanded()));
                    setExpanded(!isExpanded());
                }
            }
        });

        selected.addListener((p, ov, nv) -> {
            if (nv && parent != null) {
                parent.select(this);
            }
        });

        infoContainer = new Label("");
        confEditor = new ConfigurationEditorInternal(parent, this);

        implementorsBorderPane = new BorderPane();
        implementorsBorderPane.getStyleClass().add("implementors");

        Label label = new Label("type :");
        label.setPadding(new Insets(0, LABEL_MARGING, 0, 0));
        BorderPane.setAlignment(label, Pos.CENTER_LEFT);
        implementorsBorderPane.setLeft(label);

        choiceBox = new ChoiceBox<>();
//        choiceBox.setMaxWidth(Double.MAX_VALUE);
        choiceBox.valueProperty().addListener((ObservableValue<? extends String> p, String ov, String nv) -> {
            try {
                if (property != null) {
                    Configuration value = null;
                    if (!nv.equals("NULL")) {
                        value = RegisteryUtils.getRegistery().getConfiguration(nv);
                    }
                    property.set(value == null ? null : new FromConfigurationPropertyValue(value));
                    setRepresentativeName();
                    confEditor.setModel(value, readOnly, filter);
                }
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(ConfigurationPropertyEditor.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        BorderPane.setAlignment(choiceBox, Pos.CENTER_LEFT);
        implementorsBorderPane.setCenter(choiceBox);

        editorVBox = new VBox();
        editorVBox.getChildren().addAll(implementorsBorderPane, confEditor);
        setContent(editorVBox);

        setGraphic(infoContainer);
        setExpanded(false);
    }

    @Override
    public void setModel(Property property, boolean readOnly, Predicate filter) {
        this.readOnly = readOnly;
        if (this.property != property) {
            editorVBox.getChildren().removeAll(implementorsBorderPane, confEditor);
        }

        if (property == null) {
            this.property = null;
            PropertyEditor.updateInfo(infoContainer, this.property);
            return;
        }

        if (this.property == property) {
            return;
        }

        editorVBox.getChildren().removeAll(implementorsBorderPane, confEditor);
        extractImplementors(property);
        this.filter = filter;
        this.property = property;
        updateModelValue();
        setRepresentativeName();

        setContent(null);
        setContent(editorVBox);
        choiceBox.setDisable(readOnly);
        PropertyEditor.updateInfo(infoContainer, this.property);
        editorVBox.getChildren().add(confEditor);
    }

    private void updateModelValue() {
        if (property.get() == null || ! (property.get() instanceof FromConfigurationPropertyValue)) {
            try {
                String defaultName = (String) choiceBox.getItems().get(0);
                Configuration defaultValue = RegisteryUtils.getRegistery().getConfiguration(defaultName);
                property.set(new FromConfigurationPropertyValue(defaultValue));
            } catch (Exception ex) {
                Logger.getLogger(ConfigurationPropertyEditor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        
        FromConfigurationPropertyValue fcpv = (FromConfigurationPropertyValue) property.get();

        if (fcpv != null && fcpv.getValue() != null) { //in case property is a dummy (came from collection property editor)
            Class implementor = fcpv.getValue().typeInfo().getType();
            String className = RegisteryUtils.getRegistery().getRegisteredClassName(implementor);
            Property temp = property;
            property = null;
            choiceBox.getSelectionModel().select(className);
            property = temp;
            if (parentCollection != null && choiceBox.getItems().size() <= 1) {
                setText(className);
            }
            confEditor.setModel(fcpv.getValue(), readOnly, filter);
        } else {
            choiceBox.getSelectionModel().select("NULL");
        }
    }

    private void extractImplementors(Property property) {
        if (this.property == property) {
            return;
        }
        this.property = null;
        Class pType = property.typeInfo().getType();
        Collection<Class> implementors = RegisteryUtils.getRegistery().getImplementors(pType);
        choiceBox.getItems().clear();
        if (parentCollection == null) {
            choiceBox.getItems().add("NULL");
        }

        implementors.forEach((i) -> choiceBox.getItems().add(RegisteryUtils.getRegistery().getRegisteredClassName(i)));

        if (!readOnly && (parentCollection == null || implementors.size() > 1)) {
            editorVBox.getChildren().add(implementorsBorderPane);
        }        
    }

    @Override
    public void setModel(Configuration conf, boolean readOnly, Predicate filter) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Property getModel() {
        return property;
    }

    public final void setRepresentativeName() {
        if (property == null) {
            return;
        }
        PropertyValue value = property.get();

        if (value == null) {
            setText(property.name());
        } else {
            if (value instanceof FromConfigurationPropertyValue) {
                String name = "";
                FromConfigurationPropertyValue fcpv = (FromConfigurationPropertyValue) value;
                Property confName = fcpv.getValue().get("name") == null ? fcpv.getValue().get("Name") : fcpv.getValue().get("name");
                if (confName != null) {
                    name = " [" + (confName.get() == null ? "" : confName.get().stringValue()) + "]";
                }
                setText(fcpv.getValue().registeredName() + name);
            } else {
                setText("??? SOMETHING ???");
            }
        }

    }
}
