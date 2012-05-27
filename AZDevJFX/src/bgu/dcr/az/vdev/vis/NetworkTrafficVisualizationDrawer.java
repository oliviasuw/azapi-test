/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vdev.vis;

import bgu.dcr.az.api.exen.Execution;
import bgu.dcr.az.api.exen.mdef.VisualizationDrawer;
import bgu.dcr.az.vdev.vis.NetworkTrafficVisualization.MessageData;
import com.sun.javafx.geom.Point2D;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.graph.SparseGraph;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import javafx.animation.PathTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.CacheHint;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.Glow;
import javafx.scene.effect.InnerShadowBuilder;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 *
 * @author Administrator
 */
public class NetworkTrafficVisualizationDrawer implements VisualizationDrawer<Image, Pane, NetworkTrafficVisualization.State> {

    private static Image agentImage = new Image(NetworkTrafficVisualizationDrawer.class.getResourceAsStream("_agent.png"));
    private static Image messageImage = new Image(NetworkTrafficVisualizationDrawer.class.getResourceAsStream("_msg.png"));
    private static Color[] colors = {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.PURPLE};
    private AgentView[] agentViews;
    private SparseGraph<AgentView, Link> graph = new SparseGraph<>();
    private CircleLayout<AgentView, Link> layout = new CircleLayout<>(graph);
    private List<Link> edges;
    private Link[][] edgesMatrix;
    private Group linkDrawingGroup;
    private ConcurrentLinkedQueue<PathTransition> animations = new ConcurrentLinkedQueue<>();
    private HashMap<String, Color> messageColors = new HashMap<>();

    @Override
    public void init(final Pane canvas, Execution state) {
        canvas.getStylesheets().add(NetworkTrafficVisualizationDrawer.class.getResource("__net.css").toExternalForm());
        canvas.getStyleClass().add("canvas");
        canvas.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
                layout(canvas);
            }
        });
        final int n = state.getGlobalProblem().getNumberOfVariables();

        agentViews = new AgentView[n];
        for (int i = 0; i < n; i++) {
            agentViews[i] = new AgentView(i);
            graph.addVertex(agentViews[i]);
        }

        edgesMatrix = new Link[n][n];
        edges = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                //if (state.neighbores[i][j]) {
                final Link link = new Link(i, j);
                graph.addEdge(link, agentViews[i], agentViews[j]);
                edges.add(link);
                edgesMatrix[i][j] = link;
                //}
            }
        }


        for (int i = 0; i < agentViews.length; i++) {
            agentViews[i].initialize(canvas);
        }

        linkDrawingGroup = new Group();
        for (Link link : edges) {
            link.initialize(canvas);
        }
        canvas.getChildren().add(linkDrawingGroup);
        linkDrawingGroup.toBack();

        linkDrawingGroup.setCache(true);
        linkDrawingGroup.setCacheHint(CacheHint.QUALITY);
        layout(canvas);
    }

    private Color getMessageColor(String msg) {
        Color c = messageColors.get(msg);
        if (c == null) {
            c = colors[messageColors.size()];
            messageColors.put(msg, c);
        }

        return c;
    }

    private void layout(Pane canvas) {
        final Dimension dimension = new Dimension((int) canvas.getWidth() - 48, (int) canvas.getHeight() - 48);
        //positioning 
        layout.setSize(dimension);
        layout.setRadius(-1);
        layout.initialize();
        for (int i = 0; i < agentViews.length; i++) {
            agentViews[i].layout(canvas);
        }

        for (Link link : edges) {
            link.layout(canvas);
        }
    }

    @Override
    public synchronized boolean play(final Pane canvas, NetworkTrafficVisualization.State state) {
        if (animationIsRunning()) {
            return false;
        }

//        Set<MessageData> animated = new HashSet<>();

        for (MessageData e : state.sentInThisFrame) {
//            System.out.println("" + e);
//            if (!animated.contains(e)) {
                createAnimation(canvas, e);
//                animated.add(e);
//            }
        }

        return true;
    }

    @Override
    public void rewind(Pane canvas, NetworkTrafficVisualization.State state) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void fastForward(Pane canvas, NetworkTrafficVisualization.State state) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private void createAnimation(final Pane canvas, MessageData md) {

        final PathTransition pt = new PathTransition();
        double time = Math.random()/9;
        time += 0.8;
        
        pt.setDuration(Duration.seconds(time));
        final Link sel = edgesMatrix[md.from][md.to];//graph.findEdge(agentViews[key], agentViews[value]);
        pt.setPath(sel);
        final ImageView iv = new ImageView(messageImage);
        iv.setEffect(InnerShadowBuilder.create().radius(20).color(getMessageColor(md.name)).height(10).width(10).build());
        iv.setCache(true);
        iv.setCacheHint(CacheHint.SPEED);

        iv.setMouseTransparent(true);
        pt.setNode(iv);
        pt.setDelay(Duration.ZERO);
        canvas.getChildren().add(iv);
        iv.setX(sel.getSourcePoint().x);
        iv.setY(sel.getSourcePoint().y);
        pt.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                canvas.getChildren().remove(iv);
                animations.remove(pt);
            }
        });
        animations.add(pt);
        pt.play();
    }

    @Override
    public Image getThumbnail() {
        return agentImage;
    }

    @Override
    public String getDescriptionURL() {
        return getClass().getResource("NetworkTrafficVisualization.html").toExternalForm();
    }

    private boolean animationIsRunning() {
        return !animations.isEmpty();
    }

    private class Link extends Path {

        int source;
        int dest;
        Point2D sourcePoint;
        Point2D destPoint;

        public Link(int source, int dest) {
            this.source = source;
            this.dest = dest;
        }

        public void initialize(Pane canvas) {

            setStroke(Color.ANTIQUEWHITE);
            linkDrawingGroup.getChildren().add(this);
        }

        public void layout(Pane canvas) {
            sourcePoint = new Point2D((float) agentViews[source].getX() + 24, (float) agentViews[source].getY() + 24);
            destPoint = new Point2D((float) agentViews[dest].getX() + 24, (float) agentViews[dest].getY() + 24);

            getElements().clear();
            getElements().addAll(
                    new MoveTo(sourcePoint.x, sourcePoint.y),
                    new LineTo(destPoint.x, destPoint.y));

        }

        public Point2D getSourcePoint() {
            return sourcePoint;
        }

        public Point2D getDestPoint() {
            return destPoint;
        }
    }

    private class AgentView extends Group {

        private Tooltip tooltip;
        int id;
        private ImageView iv;
        private Text text;

        public AgentView(int id) {
            this.id = id;
        }

        public void initialize(Pane canvas) {
            //Image
            iv = new ImageView(agentImage);
            iv.setFitHeight(48);
            iv.setFitWidth(48);
            canvas.getChildren().add(iv);
            //tooltip
            tooltip = new Tooltip("Outgoing: 15 | Incoming: 150");

            final EventHandler<MouseEvent> eventHandler = new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent e) {
                    if (tooltip.isShowing()) {
                        tooltip.hide();
                    } else {
                        tooltip.show(iv, e.getScreenX(), e.getScreenY());
                    }
                }
            };

            iv.setOnMouseClicked(eventHandler);
            iv.setCursor(Cursor.HAND);

            iv.setOnMouseEntered(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent e) {
                    for (Link l : graph.getInEdges(AgentView.this)) {
                        l.setStroke(Color.CORAL);
                        l.setStrokeWidth(3);
                        iv.setEffect(new Glow(0.7));
                    }
                }
            });

            iv.setOnMouseExited(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent arg0) {
                    for (Link l : graph.getInEdges(AgentView.this)) {
                        l.setStroke(Color.ANTIQUEWHITE);
                        l.setStrokeWidth(1);
                        iv.setEffect(null);
                    }
                }
            });


            //Text
            text = new Text("" + id);
            text.setFill(Color.CORAL);

            text.setFont(Font.font("Verdana", 17));
            canvas.getChildren().add(text);
            text.setMouseTransparent(true);

        }

        public void layout(Pane canvas) {
            iv.setX(layout.getX(this));
            iv.setY(layout.getY(this));

            //Id test
            if (id >= 10) {
                text.setX(iv.getX() + 13);
            } else {
                text.setX(iv.getX() + 20);
            }

            text.setY(iv.getY() + 40);

        }

        public final double getY() {
            return iv.getY();
        }

        public final double getX() {
            return iv.getX();
        }
    }
}
