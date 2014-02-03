/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.ui.confe.util;

import javafx.scene.control.Label;

/**
 *
 * @author Shl
 */
public class FXUtils {

    public static float requiredWidthOfLabel(Label gc) {
        return com.sun.javafx.tk.Toolkit.getToolkit().getFontLoader().computeStringWidth(gc.getText(), gc.getFont());
    }

    public static float requiredHeightOfLabel(Label gc) {
        return com.sun.javafx.tk.Toolkit.getToolkit().getFontLoader().getFontMetrics(gc.getFont()).getLineHeight();
    }
}
