/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.ui.confe;

import bgu.dcr.az.anop.conf.Configuration;
import bgu.dcr.az.anop.conf.Property;
import bgu.dcr.az.anop.conf.impl.FromStringPropertyValue;
import bgu.dcr.az.ui.util.FXUtils;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;

/**
 * FXML Controller class
 *
 * @author Shl
 */
public class TerminalPropertyEditor extends BorderPane implements PropertyEditor {

    private static final int LABEL_MARGING = 10;
    private final Label label;
    private final Label infoContainer;
    private final TextField textField;
    private final CheckBox checkBox;
    private final ChoiceBox<String> choiceBox;

    private Property property;

    public TerminalPropertyEditor() {
        infoContainer = new Label("");
        label = new Label();
        label.setGraphic(infoContainer);
        BorderPane.setAlignment(label, Pos.CENTER_LEFT);

        textField = new TextField() {
            @Override
            public void replaceText(int start, int end, String text) {
                final String origin = getText();
                String updated = origin.substring(0, start) + text + origin.substring(end);
                if (isLegal(updated)) {
                    super.replaceText(start, end, text);
                }
            }

            @Override
            public void replaceSelection(String text) {
                if (isLegal(text)) {
                    super.replaceSelection(text);
                }
            }
        };
        textField.textProperty().addListener((ObservableValue<? extends String> p, String ov, String nv) -> {
            if (property != null) {
                property.set(new FromStringPropertyValue(nv));
            }
        });

        checkBox = new CheckBox();
        BorderPane.setAlignment(checkBox, Pos.CENTER_LEFT);
        checkBox.selectedProperty().addListener((ObservableValue<? extends Boolean> p, Boolean ov, Boolean nv) -> {
            if (property != null) {
                property.set(new FromStringPropertyValue(nv.toString()));
            }
        });

        choiceBox = new ChoiceBox<>();
        choiceBox.setMaxWidth(Double.MAX_VALUE);
        choiceBox.valueProperty().addListener((ObservableValue<? extends String> p, String ov, String nv) -> {
            if (property != null) {
                property.set(new FromStringPropertyValue(nv));
            }
        });
    }

    @Override
    public void setModel(Property property, boolean readOnly) {
        if (this.property != property) {
            setLeft(null);
            setCenter(null);
        }

        if (property == null) {
            this.property = null;
            updateInfo(infoContainer);
            return;
        }
        
        if (this.property == property) {
            return;
        }

        label.setText(property.name());
        setLeft(label);

        Class pType = property.typeInfo().getType();
        if (String.class.isAssignableFrom(pType)) {
            setTextInputModel(property, "", "String value (free text)", readOnly);
        } else if (Number.class.isAssignableFrom(pType)) {
            setTextInputModel(property, "0", pType.getSimpleName() + " number", readOnly);
        } else if (Character.class.isAssignableFrom(pType)) {
            setTextInputModel(property, "~", "Character", readOnly);
        } else if (Boolean.class.isAssignableFrom(pType)) {
            setModelBoolean(property, readOnly);
        } else if (pType.isEnum()) {
            setModelEnum(property, readOnly);
        } else {
            setTextInputModel(property, "", pType.getSimpleName(), readOnly);
//            throw new RuntimeException("Unsupported type: " + pType.getSimpleName());
        }
        updateInfo(infoContainer);
    }

    private void setModelBoolean(final Property property, boolean readOnly) {
        this.property = property;
        if (property.get() == null) {
            property.set(new FromStringPropertyValue("true"));
        }

        checkBox.setSelected(property.stringValue().equals("true"));
        setCenter(checkBox);
        checkBox.setDisable(readOnly);
    }

    private void setModelEnum(final Property property, boolean readOnly) {
        if (this.property != property) {
            try {
                Object[] items = (Object[]) property.typeInfo().getType().getMethod("values").invoke(null);
                this.property = null;
                choiceBox.getItems().clear();
                for (Object i : items) {
                    choiceBox.getItems().add(i.toString());
                }
            } catch (Exception ex) {
                Logger.getLogger(TerminalPropertyEditor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        this.property = property;

        if (property.get() == null) {
            property.set(new FromStringPropertyValue(choiceBox.getItems().get(0)));
        }

        choiceBox.setValue(property.stringValue());
        setCenter(choiceBox);
        choiceBox.setDisable(readOnly);
    }

    private void setTextInputModel(Property property, String defaultValue, String prompt, boolean readOnly) {
        this.property = property;
        if (property.get() == null) {
            property.set(new FromStringPropertyValue(defaultValue));
        }
        textField.setText(property.stringValue());
        textField.setPromptText(prompt);
        textField.setTooltip(new Tooltip("" + property.typeInfo().getType().getSimpleName() + " value"));
        setCenter(textField);
        textField.setDisable(readOnly);
    }

    public float getLabelWidth() {
        return FXUtils.requiredWidthOfLabel(label);
    }

    public void setLabelWidth(double width) {
        label.setPrefWidth(width + LABEL_MARGING);
        label.setMaxWidth(width + LABEL_MARGING);
        label.setMinWidth(width + LABEL_MARGING);
    }

    @Override
    public void setModel(Configuration conf, boolean readOnly) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public boolean isLegal(String text) {
        if (text.isEmpty()) {
            return true;
        }

        Class c = property.typeInfo().getType();

        if (Number.class.isAssignableFrom(c) && text.equals("-")) {
            return true;
        }

        if ((Float.class.isAssignableFrom(c.getClass()) || Double.class.isAssignableFrom(c.getClass())) && text.equals(".")) {
            return true;
        }

        try {
            new FromStringPropertyValue(text).create(property.typeInfo());
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public Property getModel() {
        return property;
    }
}
