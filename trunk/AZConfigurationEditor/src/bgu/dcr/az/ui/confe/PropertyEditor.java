/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.ui.confe;

import bgu.dcr.az.anop.conf.Configuration;
import bgu.dcr.az.anop.conf.Property;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import resources.img.R;

/**
 *
 * @author Shl
 */
public interface PropertyEditor {

    public static final Image INFO_ICON = new Image(R.class.getResourceAsStream("info.png"));

    public void setModel(Configuration conf, boolean readOnly);

    public void setModel(Property property, boolean readOnly);

    public Property getModel();

    public default void updateInfo(Label infoContainer) {
        infoContainer.setText("");
        infoContainer.setGraphic(null);
        infoContainer.setTooltip(null);

        if (getModel() == null || getModel().doc() == null) {
            return;
        }
        String iconPath = getModel().doc().first("icon");

        if (iconPath != null) {
            if (iconPath.startsWith("#")) {
                infoContainer.setGraphic(new ImageView(new Image(R.class.getResourceAsStream(iconPath.substring(1)))));
            } else {
                infoContainer.setGraphic(new ImageView(new Image(iconPath)));
            }
        }

        String info = getModel().doc().description();

        if (info != null && !info.isEmpty()) {
            if (infoContainer.getGraphic() == null) {
                infoContainer.setGraphic(new ImageView(INFO_ICON));
            }
            Tooltip tooltip = new Tooltip(info);
            infoContainer.setTooltip(tooltip);
        }
    }
}
