package bgu.dcr.az.pivot.test;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import bgu.dcr.az.common.ui.FXUtils;
import bgu.dcr.az.pivot.ui.viewer.PivotDataViewerController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author User
 */
public class PivotView extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXUtils.PaneWithCTL<PivotDataViewerController> p = FXUtils.loadPane(PivotDataViewerController.class, "PivotDataViewer.fxml");
        p.getController().setModel(PivotTestUtils.readPivotFromCSV(PivotView.class.getResourceAsStream("test.csv")));
        Scene scene = new Scene(p.getPane());
        stage.setScene(scene);
        stage.setOpacity(0.5);
        stage.show();
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
