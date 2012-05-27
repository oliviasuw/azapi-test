/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vdev.ui;

import bgu.dcr.az.api.exen.mdef.VisualizationDrawer;
import bgu.dcr.az.api.exen.vis.VisualExecutionRunner;
import bgu.dcr.az.api.exen.vis.VisualizationBuffer;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author Administrator
 */
public class PlayScreen implements Initializable {
    ////////////////////////////////////////////////////////////////////////////
    ///                             UI FIELDS                                ///
    ////////////////////////////////////////////////////////////////////////////

    public Button fullScreenButton;
    public Pane canvas;
    ////////////////////////////////////////////////////////////////////////////
    ///                           NORMAL FIELDS                              ///
    ////////////////////////////////////////////////////////////////////////////
    private Stage stage;
    private VisualizationDrawer drawer;
    ////////////////////////////////////////////////////////////////////////////
    ///                        INITIALIZATION CODE                           ///
    ////////////////////////////////////////////////////////////////////////////

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeMediaControllers();
    }

    public void setup(Stage stage, final VisualExecutionRunner ver) {
        try {
            this.stage = stage;

            this.drawer = (VisualizationDrawer) ver.getLoadedVisualization().getViewType().newInstance();
            this.drawer.init(canvas, ver.getRunningExecution());

            Timeline timeline = new Timeline(new KeyFrame(Duration.millis(1000 / 5), new EventHandler<ActionEvent>() {
                private VisualizationBuffer buf = ver.getLoadedVisualizationBuffer();
                private Object next = null;

                @Override
                public void handle(ActionEvent arg0) {
                    if (next == null) {
                        next = buf.nextState();
                    }
                    
                    if (next != null) {
                        if (drawer.play(canvas, next)){
                            next = null;
                        }
                    }
                }
            }));

            timeline.setCycleCount(Timeline.INDEFINITE);
            timeline.setDelay(Duration.seconds(1));
            timeline.play();
        } catch (InstantiationException ex) {
            Logger.getLogger(PlayScreen.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(PlayScreen.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void initializeMediaControllers() {
        fullScreenButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                onFullScreenButtonClicked();
            }
        });
    }

    ////////////////////////////////////////////////////////////////////////////
    ///                        EVENT HANDLERS                                ///
    ////////////////////////////////////////////////////////////////////////////
    private void onFullScreenButtonClicked() {
        stage.setFullScreen(true);
    }
}
