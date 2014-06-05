/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.mui.scr.statistics.adv;

import bgu.dcr.az.execs.exps.ModularExperiment;
import bgu.dcr.az.execs.orm.api.Data;
import bgu.dcr.az.execs.orm.api.EmbeddedDatabaseManager;
import bgu.dcr.az.execs.orm.api.FieldMetadata;
import bgu.dcr.az.execs.orm.api.QueryDatabase;
import bgu.dcr.az.mui.RegisterController;
import bgu.dcr.az.mui.jfx.FXMLController;
import bgu.dcr.az.mui.jfx.ToolbarMultiviewController;
import bgu.dcr.az.mui.scr.statistics.base.StatisticViewUtils;
import java.sql.SQLException;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToolBar;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import org.controlsfx.dialog.Dialogs;
import resources.img.ResourcesImg;

/**
 * FXML Controller class
 *
 * @author bennyl
 * @title advanced
 * @index 1000
 */
@RegisterController("statistics.pages.advanced")
public class AdvancedStatisticScreen extends FXMLController implements ToolbarMultiviewController.WithSubToolbar {

    public static final Image TABLE_ICON16 = ResourcesImg.png("table");
    public static final Image DATABASE_ICON16 = ResourcesImg.png("database");
    public static final Image FIELD_ICON16 = ResourcesImg.png("field");
    @FXML
    TreeView dbTree;

    @FXML
    WebView editor;

    @FXML
    Button execQueryButton;

    @FXML
    Button cleanQueryButton;

    @FXML
    TableView queryResultTable;

    @FXML
    ToolBar toolbar;

    private EmbeddedDatabaseManager dbm;
    private JSObject editorModel;
    private QueryDatabase queryDB;

    @Override
    protected void onLoadView() {
        dbm = require(ModularExperiment.class).require(EmbeddedDatabaseManager.class);
        queryDB = dbm.createQueryDatabase();

        initializeEditor();
        initializeResultTable();

    }

    @Override
    public ToolBar getToolbar() {
        return toolbar;
    }

    @Override
    public void onShow() {
        initializeTableTree();
    }

    private void initializeTableTree() {
        TreeItem root = new TreeItem("Embedded Database", new ImageView(DATABASE_ICON16));

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
            Dialogs.create().showException(ex);
        }

        root.setExpanded(true);
        dbTree.setRoot(root);
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
                        Dialogs.create().showException(ex);
                    }
                });

                cleanQueryButton.setOnAction(a -> {
                    editorModel.call("setValue", "");
                });
            }
        });

        editor.getEngine().load(getClass().getResource("/js/sqlace/editor.html").toExternalForm());
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
