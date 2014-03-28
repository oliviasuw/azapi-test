/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.conf.ui;

import bgu.dcr.az.conf.api.Configuration;
import bgu.dcr.az.conf.api.Property;
import java.util.function.Predicate;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import resources.img.ResourcesImg;

/**
 *
 * @author Shl
 */
public interface PropertyEditor {

    public static final Image INFO_ICON = ResourcesImg.png("info");

    public void setModel(Configuration conf, boolean readOnly, Predicate filter);

    public void setModel(Property property, boolean readOnly, Predicate filter);

    public Property getModel();

    public static void updateInfo(Label infoContainer, Property model) {
        infoContainer.setText("");
        infoContainer.setGraphic(null);
        infoContainer.setTooltip(null);

        if (model == null || model.doc() == null) {
            return;
        }
        String iconPath = model.doc().first("icon");

        if (iconPath != null) {
            if (iconPath.startsWith("#")) {
                infoContainer.setGraphic(new ImageView(ResourcesImg.png(iconPath.substring(1))));
            } else {
                infoContainer.setGraphic(new ImageView(new Image(iconPath)));
            }
        }

        String info = model.doc().description();

        if (info != null && !info.isEmpty()) {
            if (infoContainer.getGraphic() == null) {
                infoContainer.setGraphic(new ImageView(INFO_ICON));
            }
            Tooltip tooltip = new Tooltip(info);
            infoContainer.setTooltip(tooltip);
        }
    }
}
