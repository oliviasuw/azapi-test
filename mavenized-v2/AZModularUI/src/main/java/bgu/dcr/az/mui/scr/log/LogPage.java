/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.mui.scr.log;

import bgu.dcr.az.execs.api.loggers.LogManager;
import bgu.dcr.az.execs.exps.ExecutionTree;
import bgu.dcr.az.execs.exps.ModularExperiment;
import bgu.dcr.az.execs.exps.prog.DefaultExperimentProgress;
import bgu.dcr.az.mui.BaseController;
import bgu.dcr.az.mui.RegisterController;
import bgu.dcr.az.mui.jfx.FXMLController;
import bgu.dcr.az.mui.modules.SyncPulse;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableStringValue;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import org.controlsfx.control.MasterDetailPane;
import org.controlsfx.control.PopOver;

/**
 * FXML Controller class
 *
 * @author user
 * @title Logger
 * @tabIndex 1
 */
@RegisterController("main.pages.loggers")
public class LogPage extends FXMLController {

    @FXML
    private TreeView<FeaturedTreeNode> experimentTree;

    @FXML
    private Button backwardBtn;

    @FXML
    private Button forwardBtn;

    @FXML
    private Hyperlink changeExperimentLink;

    @FXML
    private MasterDetailPane logView;

    private TreeTableView logTreeView;

    private LogManager logManager;
    private ObjectProperty<ExecutionTree> selectedExperiment;
    private IntegerProperty selectedSubExperimentIndex;
    private DefaultExperimentProgress progress;

    public static boolean accept(BaseController c) {
        return c.isInstalled(ModularExperiment.class);
    }

    @Override
    protected void onLoadView() {
        ExecutionTree experimentRoot = require(ModularExperiment.class).execution();
        logManager = experimentRoot.require(LogManager.class);
        progress = experimentRoot.require(DefaultExperimentProgress.class);

        installControls();
        intstallExperimentTree(experimentRoot);

        infoStream().listen(SyncPulse.Sync.class, s -> {
//            selectedSubExperimentIndex.set(progress.getNumOfFinishedExecutionsInCurrentTest());
            //loadLogRecords();
        });
    }

    private void installControls() {
        installLogTreeView();

        selectedExperiment = new SimpleObjectProperty<>();
        selectedSubExperimentIndex = new SimpleIntegerProperty();

        experimentTree.getSelectionModel().selectedItemProperty().addListener((o, ov, nv) -> {
            if (ov != nv) {
                selectedExperiment.set((ExecutionTree) nv.getValue().getObject());
            }
        });

        selectedExperiment.addListener((o, ov, nv) -> {
            selectedSubExperimentIndex.set(1);
        });

        selectedSubExperimentIndex.addListener((o, ov, nv) -> {
            if (nv.intValue() == -1) {
                return;
            }
            backwardBtn.setDisable(selectedSubExperimentIndex.get() <= 1);

            forwardBtn.setDisable(selectedSubExperimentIndex.get() >= selectedExperiment.get().numChildren());

            changeExperimentLink.setText(selectedExperiment.get().child(selectedSubExperimentIndex.get()).getName());
            loadLogRecords();
        });

        backwardBtn.setOnAction(ae -> selectedSubExperimentIndex.set(selectedSubExperimentIndex.get() - 1));
        forwardBtn.setOnAction(ae -> selectedSubExperimentIndex.set(selectedSubExperimentIndex.get() + 1));

        PopOver po = createChangeExperimentPopOver();

        changeExperimentLink.setOnAction(ae -> {
            po.show(changeExperimentLink);
        });
    }

    private void installLogTreeView() {
        logTreeView = new TreeTableView();
        logTreeView.setShowRoot(false);
        logTreeView.setRoot(new TreeItem());

        TreeTableColumn<LogManager.LogRecord, String> time = new TreeTableColumn<>("Time");
        time.setCellValueFactory(p -> new SimpleObjectProperty<>("" + p.getValue().getValue().time));

        TreeTableColumn<LogManager.LogRecord, String> sid = new TreeTableColumn<>("Sequence Id");
        sid.setCellValueFactory(p -> new SimpleStringProperty("" + p.getValue().getValue().sharedIndex));

        TreeTableColumn<LogManager.LogRecord, String> aid = new TreeTableColumn<>("Agent Id");
        aid.setCellValueFactory(p -> new SimpleObjectProperty<>("" + p.getValue().getValue().aid));

        TreeTableColumn<LogManager.LogRecord, String> lrec = new TreeTableColumn<>("Log record");
        lrec.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getValue().toString()));
        logTreeView.getColumns().addAll(time, sid, aid, lrec);
        logView.setMasterNode(logTreeView);
    }

    private PopOver createChangeExperimentPopOver() {
        PopOver po = new PopOver();
        po.setDetachable(false);
        po.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);

        TextField selectionTxt = new TextField();
        Button selectionBtn = new Button("Select");
        selectionBtn.setOnAction(ae -> {
            selectedSubExperimentIndex.set(Integer.valueOf(selectionTxt.getText()));
            po.hide();
        });

        po.contentNodeProperty().set(new HBox(new Label("Select experiment"), selectionTxt, selectionBtn));
        return po;
    }

    private void intstallExperimentTree(ExecutionTree experimentRoot) {
        TreeItem<FeaturedTreeNode> root = new TreeItem(new FeaturedTreeNode(experimentRoot, o -> "experiment"));
        root.setExpanded(true);
        experimentTree.setRoot(root);

        for (int i = 0; i < experimentRoot.numChildren(); i++) {
            int fi = i + 1;
            ExecutionTree exec = experimentRoot.child(i);
            TreeItem<FeaturedTreeNode> node = new TreeItem(new FeaturedTreeNode<>(exec, o -> "Test " + fi + " [" + o.getName() + "]"));
            root.getChildren().add(node);
        }

        experimentTree.getSelectionModel().selectFirst();
    }

    private void loadLogRecords() {
        try {
            Iterable<LogManager.LogRecord> res = logManager.getRecords(selectedExperiment.get().getName(), selectedSubExperimentIndex.get());

            TreeItem root = logTreeView.getRoot();
            root.getChildren().clear();

            res.forEach(r -> root.getChildren().add(new TreeItem(r)));

        } catch (Exception ex) {
            System.err.println("Error loading log: " + ex);
        }
    }

    private static class FeaturedTreeNode<T> {

        private final T object;
        private final Stringanazer<T> stringanazer;

        public FeaturedTreeNode(T object, Stringanazer<T> stringanazer) {
            this.object = object;
            this.stringanazer = stringanazer;
        }

        public T getObject() {
            return object;
        }

        public FeaturedTreeNode(T object) {
            this.object = object;
            this.stringanazer = o -> o.toString();
        }

        @Override
        public String toString() {
            return stringanazer.toString(object);
        }

        private static interface Stringanazer<T> {

            String toString(T obj);
        }
    }
}
