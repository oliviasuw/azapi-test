/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vis.gui;

/**
 *
 * @author Shlomi
 */
import java.io.File;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class VisGuiController {

    @FXML
    private Text actiontarget;
    @FXML
    private ComboBox<String> maps;
    @FXML
    private TextField agents;
    @FXML
    private TextField ticks;
//    @FXML
//    private TextField seed;
    @FXML
    private TextField electric;
//    @FXML
//    private TextField p2;
    @FXML
    private Button runButton;
    @FXML
    private Button closeButton;
    @FXML
    private Button browseButton;
    @FXML
    private ComboBox<String> mapCombo;

    final FileChooser fileChooser = new FileChooser();

    @FXML
    protected void handleBrowseButtonAction(ActionEvent event) {
        Stage stage = (Stage) browseButton.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            openFile(file);
        }
    }

    @FXML
    protected void handleRunButtonAction(ActionEvent event) {

        actiontarget.setVisible(false);
        actiontarget.setText("Bad values, try again!");
        String mapText = maps.getValue();
        int seednum, agentnum, ticknum, elecnum;
        try {
//            seednum = Integer.parseInt(seed.getText());
            agentnum = Integer.parseInt(agents.getText());
            ticknum = Integer.parseInt(ticks.getText());
            elecnum = Integer.parseInt(electric.getText());
//            p2num = Double.parseDouble(p2.getText());
        } catch (NumberFormatException e) {
            actiontarget.setVisible(true);
            return;
        }
        if (agentnum < 1 || ticknum < 1 || elecnum > 1 || elecnum < 0) {
            actiontarget.setVisible(true);
            return;
        }

        final String map = textToFileName(mapText);
//        AlgorithmData data = null;

        if (map != null) {
//            ProblemGenerator generator = new ProblemGenerator();
//            final Problem problem = generator.generate(agentnum, ticknum, elecnum, p2num, seednum);
////            CPass1GUI.graphPane.setContent(new Label("Running..."));
//            CPass1GUI.setOnPane(new Label("Running..."));
//            AlgorithmRunningTask algoRunTask = new AlgorithmRunningTask(map, problem);
//            algoRunTask.start();
        }
    }

    @FXML
    private String textToFileName(String mapText) {
        String ans = null;
        if (mapText == null) {
            mapText = "nothing";
        }
        switch (mapText) {
            case "Tel-Aviv":
                ans = "..";
                break;
            case "Beer-Sheva":
                ans = "..";
                break;
            case "Manhatten":
                ans = "..";
                break;
            default:
                actiontarget.setVisible(true);
                break;
        }
        return ans;
    }

    @FXML
    protected void handleCloseButtonAction(ActionEvent event) {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    protected void handleGraphChoice(ActionEvent event) {
        int index = textToIndex(mapCombo.getValue());
//        ScrollPane graphPane =(ScrollPane) graphCombo.getScene().lookup("#graphPane");
//        CPass1GUI.graphPane.setContent(CPass1GUI.charts.get(index));
        VisGUI.setOnPane(VisGUI.charts.get(index));
    }

    @FXML
    private int textToIndex(String which) {
        int ans = 0;
        switch (which) {
            case "0.2":
                ans = 0;
                break;
            case "0.3":
                ans = 1;
                break;
            case "0.4":
                ans = 2;
                break;
            case "0.5":
                ans = 3;
                break;
            case "0.6":
                ans = 4;
                break;
            case "0.7":
                ans = 5;
                break;
            case "0.8":
                ans = 6;
                break;
            default:
                actiontarget.setText("Bad values, try again!");
                break;
        }
        return ans;
    }

    private void openFile(File file) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
