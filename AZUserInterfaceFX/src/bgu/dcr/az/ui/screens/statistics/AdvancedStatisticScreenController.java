/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.ui.screens.statistics;

import bgu.dcr.az.orm.api.Data;
import bgu.dcr.az.orm.api.EmbeddedDatabaseManager;
import bgu.dcr.az.orm.api.FieldMetadata;
import bgu.dcr.az.orm.api.QueryDatabase;
import bgu.dcr.az.ui.AppController;
import bgu.dcr.az.ui.screens.dialogs.Notification;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.web.WebView;
import js.JSAnchor;
import netscape.javascript.JSObject;
import resources.img.ResourcesImg;

/**
 * FXML Controller class
 *
 * @author bennyl
 */
public class AdvancedStatisticScreenController implements Initializable {

    public static final Image TABLE_ICON16 = ResourcesImg.png("table");
    public static final Image DATABASE_ICON16 = ResourcesImg.png("database");
    public static final Image FIELD_ICON16 = ResourcesImg.png("field");

    private MainStatisticScreen statisticsScreen;

    @FXML
    TreeView dbTree;

    @FXML
    WebView editor;

    @FXML
    Button execQueryButton;
    @FXML
    Button simpleModeButton;
    @FXML
    Button cleanQueryButton;

    @FXML
    TableView queryResultTable;

    private EmbeddedDatabaseManager dbm;
    private JSObject editorModel;
    private QueryDatabase queryDB;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        dbm = AppController.getDatabaseManager();
        queryDB = dbm.createQueryDatabase();

        simpleModeButton.setOnAction(a -> statisticsScreen.toSimpleMode());
        initializeEditor();
        initializeTableTree();
        initializeResultTable();
    }

    public void setStatisticScreen(MainStatisticScreen statisticsScreen) {
        this.statisticsScreen = statisticsScreen;
    }

    private void initializeTableTree() {
        try {
            TreeItem root = new TreeItem("Embedded Database", new ImageView(DATABASE_ICON16));

            System.out.println("There are: " + dbm.tables().size() + " tables.");

            try {
                dbm.tables().values().stream()
                        .map(v -> {
                            TreeItem t = new TreeItem(v, new ImageView(TABLE_ICON16));

                            for (FieldMetadata f : v.fields()) {
                                t.getChildren().add(new TreeItem(f, new ImageView(FIELD_ICON16)));
                            }

                            return t;
                        }).forEach(root.getChildren()::add);
            } catch (SQLException ex) {
                Notification.exception(ex);
            }

            root.setExpanded(true);
            dbTree.setRoot(root);
        } catch (SQLException ex) {
            Notification.exception(ex);
        }
    }

    private void initializeEditor() {
        
        editor.getEngine().loadContent("<html> <body style='bacground-color:black;'/> </html>");
        
        editor.getEngine().getLoadWorker().stateProperty().addListener((v, o, n) -> {
            if (n == Worker.State.SUCCEEDED) {
                editorModel = (JSObject) editor.getEngine().executeScript("window.editor");

                execQueryButton.setOnAction(a -> {
                    try {
                        Data result = queryDB.query(editorModel.call("getValue").toString());
                        StatisticViewUtils.fillTable(queryResultTable, result);
                    } catch (Exception ex) {
                        cleanTable();
                        AppController.showErrorDialog(ex, "cannot execute query");
                    }
                });
                
                cleanQueryButton.setOnAction(a -> {
                    editorModel.call("setValue", "");
                });
            }
        });

        editor.getEngine().load(JSAnchor.class.getResource("ace/editor.html").toExternalForm());
    }

    private void initializeResultTable() {
        cleanTable();
    }

    private void cleanTable() {
        queryResultTable.getItems().clear();
        queryResultTable.getColumns().clear();
        queryResultTable.getColumns().add(new TableColumn(""));
    }

}
