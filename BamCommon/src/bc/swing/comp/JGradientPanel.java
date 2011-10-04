/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.swing.comp;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;

/**
 *
 * @author bennyl
 */
public class JGradientPanel extends JPanel {

    boolean gradient = true;

    public void setGradient(boolean gradient) {
        this.gradient = gradient;
    }

    public boolean isGradient() {
        return gradient;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        if (!isOpaque() || !isGradient()) {
            super.paintComponent(g);
            return;
        }

        Graphics2D g2d = (Graphics2D) g;
        int w = getWidth();
        int h = getHeight();


        Color color1 = getBackground();
        Color color2 = color1.darker();
        GradientPaint gp = new GradientPaint(0, 0, color1, 0, h, color2);

        g2d.setPaint(gp);
        g2d.fillRect(0, 0, w, h);

        setOpaque(false);
        super.paintComponent(g);
        setOpaque(true);
    }
}
