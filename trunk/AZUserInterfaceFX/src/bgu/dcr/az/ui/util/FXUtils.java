/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.ui.util;

import java.io.IOException;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import org.scenicview.ScenicView;

/**
 *
 * @author User
 */
public class FXUtils {

    public static class JFXPanelWithCTL<T> extends JFXPanel {

        T ctl;

        public T getController() {
            return ctl;
        }
    }

    public static class PaneWithCTL<T> {

        Pane pane;
        T ctl;

        public T getController() {
            return ctl;
        }

        public Pane getPane() {
            return pane;
        }

    }

    public static <T> JFXPanelWithCTL<T> load(final Class<T> ctl, final String fxml) {
        try {
            final JFXPanelWithCTL result = new JFXPanelWithCTL();
            final Semaphore lock = new Semaphore(0);
            Platform.runLater(() -> {
                try {
                    final FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(ctl.getResource(fxml));
                    Pane pane = (Pane) loader.load(ctl.getResource(fxml).openStream());
                    result.ctl = loader.getController();
                    Scene scene = new Scene(pane);

//                    ScenicView.show(scene);
                    scene.setFill(Paint.valueOf("transparent"));
                    result.setScene(scene);
                } catch (IOException ex) {
                    Logger.getLogger(FXUtils.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    lock.release();
                }
            });

            lock.acquire();
            return result;
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            return null;
        }
    }

    public static <T> PaneWithCTL<T> loadPane(final Class<T> ctl, final String fxml) {
        final Semaphore lock = new Semaphore(0);
        final PaneWithCTL[] result = {new PaneWithCTL<>()};

        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> {
                result[0] = loadPane(ctl, fxml);
                lock.release();
            });
            try {
                lock.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(FXUtils.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                final FXMLLoader loader = new FXMLLoader();
                loader.setLocation(ctl.getResource(fxml));
                Pane pane = (Pane) loader.load(ctl.getResource(fxml).openStream());
                result[0].ctl = loader.getController();
                result[0].pane = pane;
            } catch (IOException ex) {
                Logger.getLogger(FXUtils.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return result[0];
    }

    public static Scene createScene(final Pane pane) {
        try {
            final Scene[] sceneBox = {null};
            final Semaphore lock = new Semaphore(0);
            Platform.runLater(new Runnable() {

                @Override
                public void run() {
                    sceneBox[0] = new Scene(pane);
                    sceneBox[0].setFill(Paint.valueOf("transparent"));
                    lock.release();
                }
            });

            lock.acquire();
            return sceneBox[0];
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            return null;
        }
    }

    public static float requiredWidthOfLabel(Label gc) {
        return com.sun.javafx.tk.Toolkit.getToolkit().getFontLoader().computeStringWidth(gc.getText(), gc.getFont());
    }

    public static float requiredHeightOfLabel(Label gc) {
        return com.sun.javafx.tk.Toolkit.getToolkit().getFontLoader().getFontMetrics(gc.getFont()).getLineHeight();
    }

}
