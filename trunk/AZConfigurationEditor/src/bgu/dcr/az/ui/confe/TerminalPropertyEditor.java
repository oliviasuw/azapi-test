/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.ui.confe;

import bgu.dcr.az.anop.conf.Configuration;
import bgu.dcr.az.anop.conf.Property;
import bgu.dcr.az.anop.conf.TypeInfo;
import bgu.dcr.az.anop.conf.impl.FromStringPropertyValue;
import bgu.dcr.az.ui.util.FXUtils;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Control;
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

    public TerminalPropertyEditor() {
        label = new Label();
        BorderPane.setAlignment(label, Pos.CENTER_LEFT);
        setLeft(label);
    }

    public boolean inRange(int x, int a, int b) {
        return x >= a && x <= b;
    }

    @Override
    public void setModel(Property property, boolean readOnly) {
        label.setText(property.name());
        String description = property.doc().description();
        if (description != null && !description.isEmpty()) {
            Tooltip tooltip = new Tooltip(description);
            label.setTooltip(tooltip);
        }

        Class pType = property.typeInfo().getType();
        if (String.class.isAssignableFrom(pType)) {
            setTextInputModel(property, "", "String value (free text)", (x) -> true, readOnly);
        } else if (Number.class.isAssignableFrom(pType)) {
            setTextInputModel(property, "0", pType.getSimpleName() + " number", new NumericalValueTester(property.typeInfo()), readOnly);
        } else if (Character.class.isAssignableFrom(pType)) {
            setTextInputModel(property, "~", "Character", new GeneralValueTester(property.typeInfo()), readOnly);
        } else if (Boolean.class.isAssignableFrom(pType)) {
            setModelBoolean(property, readOnly);
        } else if (pType.isEnum()) {
            setModelEnum(property, readOnly);
        } else {
            throw new RuntimeException("Unsupported type: " + pType.getSimpleName());
        }
    }

    private void setModelBoolean(final Property property, boolean readOnly) {
        CheckBox cb = new CheckBox();
        if (property.get() == null) {
            property.set(new FromStringPropertyValue("true"));
        }
        cb.setSelected(property.stringValue().equals("true"));
        cb.selectedProperty().addListener((ObservableValue<? extends Boolean> p, Boolean ov, Boolean nv) -> property.set(new FromStringPropertyValue(nv.toString())));
        BorderPane.setAlignment(cb, Pos.CENTER_LEFT);
        setCenter(cb);
        cb.setDisable(readOnly);
    }

    private void setModelEnum(final Property property, boolean readOnly) {
        ChoiceBox<String> cb = new ChoiceBox<>();
        cb.setMaxWidth(Control.USE_PREF_SIZE);

        try {
            //            test.delete.me.SomeClass.E.
            Object[] items = (Object[]) property.typeInfo().getType().getMethod("values").invoke(null);
            for (Object i : items) {
                cb.getItems().add(i.toString());
            }
        } catch (Exception ex) {
            Logger.getLogger(TerminalPropertyEditor.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (property.get() == null) {
            property.set(new FromStringPropertyValue(cb.getItems().get(0)));
        }
        cb.setValue(property.stringValue());

        cb.valueProperty().addListener((ObservableValue<? extends String> p, String ov, String nv) -> property.set(new FromStringPropertyValue(nv)));
        setCenter(cb);
        cb.setDisable(readOnly);
    }

    private void setTextInputModel(Property property, String defaultValue, String prompt, ValueTester tester, boolean readOnly) {
        TextField textField = new TextField() {
            @Override
            public void replaceText(int start, int end, String text) {
                final String origin = getText();
                String updated = origin.substring(0, start) + text + origin.substring(end);
                if (tester.isLegal(updated)) {
                    super.replaceText(start, end, text);
                }
            }

            @Override
            public void replaceSelection(String text) {
                if (tester.isLegal(text)) {
                    super.replaceSelection(text);
                }
            }
        };
        if (property.get() == null) {
            property.set(new FromStringPropertyValue(defaultValue));
        }
        textField.setText(property.stringValue());
        textField.textProperty().addListener((ObservableValue<? extends String> p, String ov, String nv) -> property.set(new FromStringPropertyValue(nv)));

        setCenter(textField);
        textField.setPromptText(prompt);
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

    private static interface ValueTester {

        boolean isLegal(String text);
    }

    private static class GeneralValueTester implements ValueTester {

        TypeInfo ti;

        public GeneralValueTester(TypeInfo ti) {
            this.ti = ti;
        }

        @Override
        public boolean isLegal(String text) {
            try {
                new FromStringPropertyValue(text).create(ti);
                return true;
            } catch (Exception ex) {
                return false;
            }
        }
    }

    private static class NumericalValueTester extends GeneralValueTester {

        public NumericalValueTester(TypeInfo ti) {
            super(ti);
        }

        @Override
        public boolean isLegal(String text) {
            if (text.equals("-")) {
                return true;
            }
            
            if ((Float.class.isAssignableFrom(ti.getClass()) || Double.class.isAssignableFrom(ti.getClass())) && text.equals(".")) {
                return true;
            }

            return super.isLegal(text);
        }
    }
}
