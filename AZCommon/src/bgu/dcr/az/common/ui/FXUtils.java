/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.common.ui;

import bgu.dcr.az.common.timing.TimingUtils;
import com.sun.javafx.scene.control.skin.TitledPaneSkin;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;

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
     * finds the first parent which accepted by the given predicate stop the
     * search by the given stop condition
     *
     * @param startingNode
     * @param predicate
     * @return null if no such node exists
     */
    public static Node lookupParent(Node startingNode, Predicate<Node> predicate, Predicate<Node> stopCondition) {
        while (startingNode != null) {
            if (predicate.test(startingNode) && !stopCondition.test(startingNode)) {
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

    public static void invokeInUI(Runnable r) {
        if (Platform.isFxApplicationThread()) {
            r.run();
        } else {
            Platform.runLater(r);
        }
    }

    public static void startCSSLiveReloader(Scene scene, String cssFile) {
        invokeInUI(() -> {
            try {
                File f = new File(cssFile);
                long[] lastKnownModification = {0};
                scene.getStylesheets().add(f.toURI().toURL().toExternalForm());
                TimingUtils.scheduleRepeating(() -> {
                    if (f.lastModified() != lastKnownModification[0]) {
                        lastKnownModification[0] = f.lastModified();
                        System.err.println("RELOADING CSS...");
                        reloadSceneStylesheet(scene);
                    }
                }, 1000);
            } catch (MalformedURLException ex) {
                Logger.getLogger(FXUtils.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    private static AnimationTimer timeline = null;

    public static void ensureVisibility(ScrollPane scroll, Node element, boolean forceTop) {
        if (timeline != null) {
            timeline.stop();
        }

        Bounds eBounds = element.localToScene(element.getBoundsInLocal());
        Bounds sBounds = scroll.localToScene(scroll.getBoundsInLocal());

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

    public static void addChildListener(Node startingNode, ListChangeListener<Node> listener) {
        LinkedList<Node> open = new LinkedList<>();
        open.add(startingNode);
        while (!open.isEmpty()) {
            Node item = open.remove();
            if (item instanceof Parent) {
                Parent parent = (Parent) item;
                parent.getChildrenUnmodifiable().addListener(listener);
                open.addAll((parent).getChildrenUnmodifiable());
            }
            if (item instanceof TitledPane) {
                open.add(((TitledPane) item).getContent());
            }
            if (item instanceof ScrollPane) {
                open.add(((ScrollPane) item).getContent());
            }
        }
    }

    public static void removeChildListener(Node startingNode, ListChangeListener<Node> listener) {
        LinkedList<Node> open = new LinkedList<>();
        open.add(startingNode);
        while (!open.isEmpty()) {
            Node item = open.remove();
            if (item instanceof Parent) {
                Parent parent = (Parent) item;
                parent.getChildrenUnmodifiable().removeListener(listener);
                open.addAll((parent).getChildrenUnmodifiable());
            }
            if (item instanceof TitledPane) {
                open.add(((TitledPane) item).getContent());
            }
            if (item instanceof ScrollPane) {
                open.add(((ScrollPane) item).getContent());
            }
        }
    }

    /**
     * finds all the children which accepted by the given predicate
     *
     * @param startingNode
     * @param predicate
     * @return null if no such node exists
     */
    public static LinkedList<Node> lookupDirectChildren(Node startingNode, Predicate<Node> predicate) {
        LinkedList<Node> result = new LinkedList<>();
        LinkedList<Node> open = new LinkedList<>();
        open.add(startingNode);
        while (!open.isEmpty()) {
            Node item = open.remove();
            if (predicate.test(item) && item != startingNode) {
                result.add(item);
                continue;
            }
            if (item instanceof Parent) {
                open.addAll(((Parent) item).getChildrenUnmodifiable());
            }
            if (item instanceof TitledPane) {
                open.add(((TitledPane) item).getContent());
            }
            if (item instanceof ScrollPane) {
                open.add(((ScrollPane) item).getContent());
            }

        }
        return result;
    }

    public static Node getTitledPaneTitleRegion(TitledPane pane) {
        TitledPaneSkin skin = (TitledPaneSkin) pane.getSkin();
        Node node = ((Parent) skin.getNode()).getChildrenUnmodifiable().get(1);
        return node;
    }

}
