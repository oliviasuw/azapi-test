/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.ui.confe;

import bgu.dcr.az.anop.conf.Configuration;
import bgu.dcr.az.anop.conf.Property;
import bgu.dcr.az.anop.conf.impl.ConfigurableTypeInfoImpl;
import bgu.dcr.az.anop.conf.impl.FromStringPropertyValue;
import bgu.dcr.az.ui.util.FXUtils;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;

/**
 * FXML Controller class
 *
 * @author Shl
 */
public class TerminalPropertyEditor extends BorderPane implements PropertyController {

    private Label label;

    public TerminalPropertyEditor() {
        label = new Label();
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
            setModelString(property, readOnly);
        } else if (Integer.class.isAssignableFrom(pType)) {
            setModelInteger(property, readOnly);
        } else if (Long.class.isAssignableFrom(pType)) {
            setModelInteger(property, readOnly);
        } else if (Double.class.isAssignableFrom(pType)) {
            setModelDouble(property, readOnly);
        } else if (Float.class.isAssignableFrom(pType)) {
            setModelFloat(property, readOnly);
        } else if (Byte.class.isAssignableFrom(pType)) {
            setModelByte(property, readOnly);
        } else if (Short.class.isAssignableFrom(pType)) {
            setModelShort(property, readOnly);
        } else if (Character.class.isAssignableFrom(pType)) {
            setModelChar(property, readOnly);
        } else if (Boolean.class.isAssignableFrom(pType)) {
            setModelBoolean(property, readOnly);
        } else if (pType.isEnum()) {
            ChoiceBox<String> cb = setModelEnum(property, readOnly);
        }

    }

    private void setModelString(final Property property, boolean readOnly) {
        final TextField textField = new TextField();
        if (property.get() != null) {
            textField.setText(property.stringValue());
        }
        textField.setEditable(!readOnly);
        textField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
                if (newPropertyValue) {
//                        System.out.println("Textfield on focus");
                } else {
                    String value = textField.textProperty().getValue();
                    property.set(new FromStringPropertyValue(value));
                }
            }
        });
        textField.setPromptText("String value (free text)");
//        textField.setStyle("-fx-prompt-text-font-style: italic;");
        setCenter(textField);
    }

    private void setModelBoolean(final Property property, boolean readOnly) {
        CheckBox cb = new CheckBox();
        if (property.get() != null) {
            boolean value = (property.stringValue().equals("true"));
            cb.setSelected(value);
        } else {
            cb.setSelected(false);
        }
        cb.setDisable(readOnly);
        cb.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> ov,
                    Boolean old_val, Boolean new_val) {
                property.set(new FromStringPropertyValue(new_val.toString()));
            }
        });

        BorderPane.setAlignment(cb, Pos.CENTER_LEFT);
        setCenter(cb);
    }

    private ChoiceBox<String> setModelEnum(final Property property, boolean readOnly) {

        ChoiceBox<String> cb = new ChoiceBox<>();

//        cb.prefWidthProperty().bind(widthProperty());
        cb.setMaxWidth(Control.USE_PREF_SIZE);
//        cb.setMinWidth(-1);
        cb.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {

            @Override
            public void changed(ObservableValue ov, Object t, Object t1) {
                property.set(new FromStringPropertyValue(t1.toString()));
            }

        });

        List<ConfigurableTypeInfoImpl> genericParameters = property.typeInfo().getGenericParameters();
        for (ConfigurableTypeInfoImpl gp : genericParameters) {
            gp.toString();
        }
        try {
            //            test.delete.me.SomeClass.E.
            Object[] items = (Object[]) property.typeInfo().getType().getMethod("values").invoke(null);
            for (Object i : items) {
                cb.getItems().add(i.toString());
            }
            if (property.get() != null) {
                String value = property.stringValue();
                cb.setValue(value);
            } else {
                cb.setValue(items[0].toString());
            }
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(TerminalPropertyEditor.class.getName()).log(Level.SEVERE, null, ex);
        }

        cb.setDisable(readOnly);
        setCenter(cb);
        return cb;
    }

    private void setModelChar(final Property property, boolean readOnly) {
        final TextField textField = new TextField();
        if (property.get() != null) {
            textField.setText(property.stringValue());
        }
        textField.setEditable(!readOnly);
        textField.addEventFilter(KeyEvent.KEY_TYPED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (textField.getText().length() > 0) {
                    keyEvent.consume();
                }
            }
        });
        textField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
                if (newPropertyValue) {
//                        System.out.println("Textfield on focus");
                } else {
                    String value = textField.textProperty().getValue();
                    property.set(new FromStringPropertyValue(value));
                }
            }
        });
        setCenter(textField);
        textField.setPromptText("Single character, e.g @");
    }

    private void setModelShort(final Property property, boolean readOnly) {
        final TextField textField = new TextField();
        if (property.get() != null) {
            textField.setText(property.stringValue());
        }
        textField.setEditable(!readOnly);
        textField.addEventFilter(KeyEvent.KEY_TYPED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                final String newchar = keyEvent.getCharacter();
                String text = textField.getText();
                if (text.length() == 0 && !"-0123456789".contains(newchar)
                        || text.length() != 0 && !"0123456789".contains(newchar)
                        || text.length() > 4 && !inRange(Integer.parseInt(text + newchar), -32768, 32767)) {
                    keyEvent.consume();
                }
            }
        });
        textField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
                if (newPropertyValue) {
//                        System.out.println("Textfield on focus");
                } else {
                    String value = textField.textProperty().getValue();
                    property.set(new FromStringPropertyValue(value));
                }
            }
        });
        setCenter(textField);
        textField.setPromptText("Short size number, range [-32768:32767]");
    }

    private void setModelByte(final Property property, boolean readOnly) {
        final TextField textField = new TextField();
        if (property.get() != null) {
            textField.setText(property.stringValue());
        }
        textField.setEditable(!readOnly);

        textField.addEventFilter(KeyEvent.KEY_TYPED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                String text = textField.getText();
                String character = keyEvent.getCharacter();
                if (text.length() == 0 && !"-0123456789".contains(character)
                        || text.length() != 0 && !"0123456789".contains(character)
                        || text.length() > 1 && !inRange(Integer.parseInt(text + character), -128, 127)) {
                    keyEvent.consume();
                }
            }
        });
        textField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
                if (newPropertyValue) {
//                        System.out.println("Textfield on focus");
                } else {
                    String value = textField.textProperty().getValue();
                    property.set(new FromStringPropertyValue(value));
                }
            }
        });
        setCenter(textField);
        textField.setPromptText("Byte size number, range [-128:127]");
    }

    private void setModelFloat(final Property property, boolean readOnly) {
        final TextField textField = new TextField();
        if (property.get() != null) {
            textField.setText(property.stringValue());
        }
        textField.setEditable(!readOnly);

        textField.addEventFilter(KeyEvent.KEY_TYPED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                String text = textField.getText();
                String character = keyEvent.getCharacter();
                if (text.length() == 0 && !"-0123456789".contains(character)
                        || text.equals("-") && !"0123456789".contains(character)
                        || text.length() > 0 && !text.equals("-") && !text.contains(".") && !".f0123456789".contains(character)
                        || text.length() > 0 && !text.equals("-") && text.contains(".") && !"f0123456789".contains(character)
                        || text.contains("f")) {
                    keyEvent.consume();
                }
            }
        });
        textField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
                if (newPropertyValue) {
//                        System.out.println("Textfield on focus");
                } else {
                    String value = textField.textProperty().getValue();
                    property.set(new FromStringPropertyValue(value));
                }
            }
        });
        setCenter(textField);
        textField.setPromptText("Float number");
    }

    private void setModelDouble(final Property property, boolean readOnly) {
        final TextField textField = new TextField();
        if (property.get() != null) {
            textField.setText(property.stringValue());
        }
        textField.setEditable(!readOnly);

        textField.addEventFilter(KeyEvent.KEY_TYPED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                String text = textField.getText();
                String character = keyEvent.getCharacter();
                if (text.length() == 0 && !"-0123456789".contains(character)
                        || text.equals("-") && !"0123456789".contains(character)
                        || text.length() > 0 && !text.equals("-") && !text.contains(".") && !".0123456789".contains(character)
                        || text.length() > 0 && !text.equals("-") && text.contains(".") && !"0123456789".contains(character)) {
                    keyEvent.consume();
                }
            }
        });
        textField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
                if (newPropertyValue) {
//                        System.out.println("Textfield on focus");
                } else {
                    String value = textField.textProperty().getValue();
                    property.set(new FromStringPropertyValue(value));
                }
            }
        });
        setCenter(textField);
        textField.setPromptText("Double (real) number, e.g 22.8");
    }

    private void setModelInteger(final Property property, boolean readOnly) {
        final TextField textField = new TextField();
        if (property.get() != null) {
            textField.setText(property.stringValue());
        }
        textField.setEditable(!readOnly);

        textField.addEventFilter(KeyEvent.KEY_TYPED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                String text = textField.getText();
                String character = keyEvent.getCharacter();
                if (text.length() == 0 && !"-0123456789".contains(character)
                        || text.length() != 0 && !"0123456789".contains(character)) {
                    keyEvent.consume();
                }
            }
        });
        textField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
                if (newPropertyValue) {
//                        System.out.println("Textfield on focus");
                } else {
                    String value = textField.textProperty().getValue();
                    property.set(new FromStringPropertyValue(value));
                }
            }
        });
        setCenter(textField);
        textField.setPromptText("Integer number, e.g 88");
    }

    public float getLabelWidth() {
        return FXUtils.requiredWidthOfLabel(label);
    }

    public void setLabelWidth(double width) {
        label.setPrefWidth(width);
        label.setMaxWidth(width);
        label.setMinWidth(width);
    }

    @Override
    public void setModel(Configuration conf, boolean readOnly) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
