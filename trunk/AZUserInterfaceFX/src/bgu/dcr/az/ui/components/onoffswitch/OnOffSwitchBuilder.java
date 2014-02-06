/*
 * Copyright (c) 2013 by Gerrit Grunwald
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package bgu.dcr.az.ui.components.onoffswitch;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Dimension2D;
import javafx.scene.paint.Color;

import java.util.HashMap;

/**
 * User: hansolo Date: 08.10.13 Time: 14:06
 */
public class OnOffSwitchBuilder<B extends OnOffSwitchBuilder<B>> {

    private HashMap<String, Property> properties = new HashMap<>();

    // ******************** Constructors **************************************
    protected OnOffSwitchBuilder() {
    }

    // ******************** Methods *******************************************
    public final static OnOffSwitchBuilder create() {
        return new OnOffSwitchBuilder();
    }

    public final OnOffSwitchBuilder styleClass(final String STYLE_CLASS) {
        properties.put("styleClass", new SimpleStringProperty(STYLE_CLASS));
        return this;
    }

    public final OnOffSwitchBuilder switchColor(final Color SWITCH_COLOR) {
        properties.put("switchColor", new SimpleObjectProperty<>(SWITCH_COLOR));
        return this;
    }

    public final OnOffSwitchBuilder textColorOn(final Color TEXT_COLOR_ON) {
        properties.put("textColorOn", new SimpleObjectProperty<>(TEXT_COLOR_ON));
        return this;
    }

    public final OnOffSwitchBuilder textColorOff(final Color TEXT_COLOR_OFF) {
        properties.put("textColorOff", new SimpleObjectProperty<>(TEXT_COLOR_OFF));
        return this;
    }

    public final OnOffSwitchBuilder thumbColor(final Color THUMB_COLOR) {
        properties.put("thumbColor", new SimpleObjectProperty<>(THUMB_COLOR));
        return this;
    }

    public final OnOffSwitchBuilder on(final boolean ON) {
        properties.put("on", new SimpleBooleanProperty(ON));
        return this;
    }

    public final B prefSize(final double WIDTH, final double HEIGHT) {
        properties.put("prefSize", new SimpleObjectProperty<>(new Dimension2D(WIDTH, HEIGHT)));
        return (B) this;
    }

    public final B minSize(final double WIDTH, final double HEIGHT) {
        properties.put("minSize", new SimpleObjectProperty<>(new Dimension2D(WIDTH, HEIGHT)));
        return (B) this;
    }

    public final B maxSize(final double WIDTH, final double HEIGHT) {
        properties.put("maxSize", new SimpleObjectProperty<>(new Dimension2D(WIDTH, HEIGHT)));
        return (B) this;
    }

    public final B prefWidth(final double PREF_WIDTH) {
        properties.put("prefWidth", new SimpleDoubleProperty(PREF_WIDTH));
        return (B) this;
    }

    public final B prefHeight(final double PREF_HEIGHT) {
        properties.put("prefHeight", new SimpleDoubleProperty(PREF_HEIGHT));
        return (B) this;
    }

    public final B minWidth(final double MIN_WIDTH) {
        properties.put("minWidth", new SimpleDoubleProperty(MIN_WIDTH));
        return (B) this;
    }

    public final B minHeight(final double MIN_HEIGHT) {
        properties.put("minHeight", new SimpleDoubleProperty(MIN_HEIGHT));
        return (B) this;
    }

    public final B maxWidth(final double MAX_WIDTH) {
        properties.put("maxWidth", new SimpleDoubleProperty(MAX_WIDTH));
        return (B) this;
    }

    public final B maxHeight(final double MAX_HEIGHT) {
        properties.put("maxHeight", new SimpleDoubleProperty(MAX_HEIGHT));
        return (B) this;
    }

    public final B scaleX(final double SCALE_X) {
        properties.put("scaleX", new SimpleDoubleProperty(SCALE_X));
        return (B) this;
    }

    public final B scaleY(final double SCALE_Y) {
        properties.put("scaleY", new SimpleDoubleProperty(SCALE_Y));
        return (B) this;
    }

    public final B layoutX(final double LAYOUT_X) {
        properties.put("layoutX", new SimpleDoubleProperty(LAYOUT_X));
        return (B) this;
    }

    public final B layoutY(final double LAYOUT_Y) {
        properties.put("layoutY", new SimpleDoubleProperty(LAYOUT_Y));
        return (B) this;
    }

    public final B translateX(final double TRANSLATE_X) {
        properties.put("translateX", new SimpleDoubleProperty(TRANSLATE_X));
        return (B) this;
    }

    public final B translateY(final double TRANSLATE_Y) {
        properties.put("translateY", new SimpleDoubleProperty(TRANSLATE_Y));
        return (B) this;
    }

    public final OnOffSwitch build() {
        final OnOffSwitch CONTROL = new OnOffSwitch();
        for (String key : properties.keySet()) {
            switch (key) {
                case "prefSize": {
                    Dimension2D dim = ((ObjectProperty<Dimension2D>) properties.get(key)).get();
                    CONTROL.setPrefSize(dim.getWidth(), dim.getHeight());
                    break;
                }
                case "minSize": {
                    Dimension2D dim = ((ObjectProperty<Dimension2D>) properties.get(key)).get();
                    CONTROL.setPrefSize(dim.getWidth(), dim.getHeight());
                    break;
                }
                case "maxSize": {
                    Dimension2D dim = ((ObjectProperty<Dimension2D>) properties.get(key)).get();
                    CONTROL.setPrefSize(dim.getWidth(), dim.getHeight());
                    break;
                }
                case "prefWidth":
                    CONTROL.setPrefWidth(((DoubleProperty) properties.get(key)).get());
                    break;
                case "prefHeight":
                    CONTROL.setPrefHeight(((DoubleProperty) properties.get(key)).get());
                    break;
                case "minWidth":
                    CONTROL.setMinWidth(((DoubleProperty) properties.get(key)).get());
                    break;
                case "minHeight":
                    CONTROL.setMinHeight(((DoubleProperty) properties.get(key)).get());
                    break;
                case "maxWidth":
                    CONTROL.setMaxWidth(((DoubleProperty) properties.get(key)).get());
                    break;
                case "maxHeight":
                    CONTROL.setMaxHeight(((DoubleProperty) properties.get(key)).get());
                    break;
                case "scaleX":
                    CONTROL.setScaleX(((DoubleProperty) properties.get(key)).get());
                    break;
                case "scaleY":
                    CONTROL.setScaleY(((DoubleProperty) properties.get(key)).get());
                    break;
                case "layoutX":
                    CONTROL.setLayoutX(((DoubleProperty) properties.get(key)).get());
                    break;
                case "layoutY":
                    CONTROL.setLayoutY(((DoubleProperty) properties.get(key)).get());
                    break;
                case "translateX":
                    CONTROL.setTranslateX(((DoubleProperty) properties.get(key)).get());
                    break;
                case "translateY":
                    CONTROL.setTranslateY(((DoubleProperty) properties.get(key)).get());
                    break;
                case "styleClass":
                    CONTROL.getStyleClass().setAll("on-off-switch", ((StringProperty) properties.get(key)).get());
                    break;
                case "switchColor":
                    CONTROL.setSwitchColor(((ObjectProperty<Color>) properties.get(key)).get());
                    break;
                case "textColorOn":
                    CONTROL.setTextColorOn(((ObjectProperty<Color>) properties.get(key)).get());
                    break;
                case "textColorOff":
                    CONTROL.setTextColorOff(((ObjectProperty<Color>) properties.get(key)).get());
                    break;
                case "thumbColor":
                    CONTROL.setThumbColor(((ObjectProperty<Color>) properties.get(key)).get());
                    break;
                case "on":
                    CONTROL.setSelected(((BooleanProperty) properties.get(key)).get());
                    break;
            }
        }
        return CONTROL;
    }
}
