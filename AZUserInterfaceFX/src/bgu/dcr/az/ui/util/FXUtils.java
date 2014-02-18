/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.ui.util;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.AnimationTimer;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.util.Duration;

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

    /**
     * finds the first parent which accepted by the given predicate
     *
     * @param startingNode
     * @param predicate
     * @return null if no such node exists
     */
    public static Node lookupParent(Node startingNode, Predicate<Node> predicate) {
        while (startingNode != null) {
            if (predicate.test(startingNode)) {
                return startingNode;
            }
            startingNode = startingNode.getParent();
        }

        return null;
    }

    /**
     * finds all the parents which accepted by the given predicate, stopping if
     * no parent exists or stop condition achieved
     *
     * @param startingNode
     * @param predicate
     * @param stopCondition
     * @return
     */
    public static List<Node> lookupParents(Node startingNode, Predicate<Node> predicate, Predicate<Node> stopCondition) {
        List<Node> result = new LinkedList<>();
        while (startingNode != null && !stopCondition.test(startingNode)) {
            if (predicate.test(startingNode)) {
                result.add(startingNode);
            }
            startingNode = startingNode.getParent();
        }

        return result;
    }

    /**
     * finds the first child which accepted by the given predicate
     *
     * @param startingNode
     * @param predicate
     * @return null if no such node exists
     */
    public static Node lookupChild(Node startingNode, Predicate<Node> predicate) {
        LinkedList<Node> open = new LinkedList<>();
        open.add(startingNode);
        while (!open.isEmpty()) {
            Node item = open.remove();
            if (predicate.test(item)) {
                return item;
            }
            if (item instanceof Parent) {
                open.addAll(((Parent) item).getChildrenUnmodifiable());
            }
        }
        return null;
    }

    /**
     * taken from:
     * https://bitbucket.org/narya/jfx78/src/423cf238579d6106c07601f635575e4c479f5183/apps/scenebuilder/SceneBuilderKit/src/com/oracle/javafx/scenebuilder/kit/util/Deprecation.java?at=default
     * directly from the scenebuilder source - uses a deprecated API - but this
     * is sadly the only way to do so..
     *
     * @param scene
     */
    public static void reloadSceneStylesheet(Scene scene) {
        Platform.runLater(() -> {
            com.sun.javafx.css.StyleManager.getInstance().forget(scene);
            scene.getRoot().impl_reapplyCSS();
        });

    }

    private static AnimationTimer timeline = null;

    public static void ensureVisibility(ScrollPane scroll, Node element, boolean forceTop) {
        if (timeline != null) {
            timeline.stop();
        }

        Bounds eBounds = element.localToScene(element.getBoundsInLocal());
        Bounds sBounds = scroll.localToScene(scroll.getBoundsInLocal());
        
            System.out.println("SBOUNDS " + sBounds + "\nEBOUNDS" + eBounds);
        if (!forceTop && sBounds.contains(new Point2D(eBounds.getMinX(), eBounds.getMinY()))) {
            return;
        }

        timeline = new AnimationTimer() {
            long startTime = System.nanoTime();

            @Override
            public void handle(long l) {
                double delta = Math.min(1, (l - startTime) / 1000000000.0);

                double vvalue = scroll.getVvalue();
                double target = calculateScrollingTarget(element, scroll);

                scroll.setVvalue(vvalue + (target - vvalue) * delta);

                if (delta == 1) {
                    stop();
                }
            }
        };

        timeline.start();
    }

    private static double calculateScrollingTarget(Node element, ScrollPane scroll) {
        Bounds eBounds = element.localToScene(element.getBoundsInLocal());
        Bounds sBounds = scroll.localToScene(scroll.getContent().getBoundsInLocal());
        double scrollHeight = sBounds.getHeight() - scroll.getViewportBounds().getHeight();
        double scrollLocation = eBounds.getMinY() - sBounds.getMinY() + scrollHeight * scroll.vvalueProperty().get();
        return scrollHeight == 0 ? 0 : scrollLocation / scrollHeight;
    }
}
