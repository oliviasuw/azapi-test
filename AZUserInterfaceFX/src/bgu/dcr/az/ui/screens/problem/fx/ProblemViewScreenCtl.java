/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.ui.screens.problem.fx;

import bc.ui.swing.useful.DataPanel;
import bc.ui.swing.visuals.Visual;
import bgu.dcr.az.api.prob.ImmutableProblem;
import bgu.dcr.az.api.prob.Problem;
import bgu.dcr.az.common.ui.FXUtils;
import bgu.dcr.az.common.ui.panels.FXMessagePanel;
import bgu.dcr.az.mas.cp.CPExperimentTest;
import bgu.dcr.az.mas.exp.Experiment;
import bgu.dcr.az.ui.screens.dialogs.MessageDialog;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.IntStream;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import resources.img.ResourcesImg;

/**
 * FXML Controller class
 *
 * @author Zovadi
 */
public class ProblemViewScreenCtl implements Initializable {

    private final static Image AGENT1_ICON = ResourcesImg.png("agent1");
    private final static Image AGENT2_ICON = ResourcesImg.png("agent2");
    private final static Image CONSTRAINTS_ICON = ResourcesImg.png("all-constraints");
    private final static Image PROBLEM_ICON = ResourcesImg.png("problem");

    private final static int COLUMN_WIDTH = 50;
    private final static int COLUMN_HEIGHT = 25;

    @FXML
    private BorderPane data;

    @FXML
    private SplitPane split;

    @FXML
    private Label problemViewingDescription;

    @FXML
    private Hyperlink changeProblemHyperlink;

    @FXML
    private ComboBox testSelect;

    @FXML
    private TextField pnumSelect;

    @FXML
    private Button viewProblemBtn;

    @FXML
    private TreeView tree;

    @FXML
    private TableView table;

    @FXML
    private FlowPane problemChooser;

    private ConstraintCalcConsoleCtl calc;

    public static final String PROBLEM = "Problem";
    public static final String CONSTRAINT_MATRIX = "Constraints Matrix";
    private ImmutableProblem p;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        FXUtils.PaneWithCTL<ConstraintCalcConsoleCtl> calculator = FXUtils.loadFXML(ConstraintCalcConsoleCtl.class);
        calc = calculator.getController();

        split.getItems().add(calculator.getPane());
        split.setDividerPosition(0, 0.85);

        tree.getSelectionModel().selectedItemProperty().addListener((p, ov, nv) -> {
            if (nv == null) {
                return;
            }

            Object value = ((TreeItem) nv).getValue();
            if (PROBLEM.equals(value)) {
                data.setCenter(FXMessagePanel.createNoDataPanel("No data to view."));
            } else if (CONSTRAINT_MATRIX.equals(value)) {
                showConstraintsMatrix();
            } else {
                final Object value1 = ((TreeItem) nv).getParent().getValue();
                if (!PROBLEM.equals(value1)) {
                    AgentInfo ai = (AgentInfo) value1;
                    AgentInfo aj = (AgentInfo) value;
                    showConstraintsCosts(ai.getId(), aj.getId());
                } else {
                    data.setCenter(FXMessagePanel.createNoDataPanel("No data to view."));
                }
            }
        });

        data.setCenter(FXMessagePanel.createNoDataPanel("No data to view."));

//        problemChooser.setMinHeight(0);
//        problemChooser.setPrefHeight(0);
//        problemChooser.setMaxHeight(0);
//        Pane header = (Pane) table.lookup("TableHeaderRow");
//        header.setVisible(false);
//        table.setLayoutY(-header.getHeight());
//        table.autosize();
    }

    private void showConstraintsMatrix() {
        int numVars = p.getNumberOfVariables();
        table.getColumns().clear();

        TableColumn[] columns = new TableColumn[numVars + 1];
        for (int i = 0; i < columns.length; i++) {
            columns[i] = new TableColumn("" + i);
            columns[i].setMinWidth(COLUMN_WIDTH);
            columns[i].setPrefWidth(COLUMN_WIDTH);
            columns[i].setMaxWidth(COLUMN_WIDTH);
            columns[i].setCellFactory(p -> {
                return new TableCell() {
                    @Override
                    protected void updateItem(Object t, boolean bln) {
                        super.updateItem(t, bln); //To change body of generated methods, choose Tools | Templates.
                        if (t != null) {
                            ConstraintTypes e = (ConstraintTypes) t;
                            setAlignment(Pos.CENTER);
                            setText(e.toString());
                            getStyleClass().clear();
                            getStyleClass().add(e.getStyleClass());
                            setMinHeight(COLUMN_HEIGHT);
                            setPrefHeight(COLUMN_HEIGHT);
                            setMaxHeight(COLUMN_HEIGHT);
                        }
                    }

                };
            });
            int fi = i;
            columns[i].setCellValueFactory(new PropertyValueFactory<Integer, Object>("") {
                @Override
                public ObservableValue<Object> call(TableColumn.CellDataFeatures<Integer, Object> cdf) {
                    int j = cdf.getValue();
                    if (j == 0 && fi == 0) {
                        return new SimpleObjectProperty<>(ConstraintTypes.Header.setHeaderName(""));
                    }
                    if (j == 0) {
                        return new SimpleObjectProperty<>(ConstraintTypes.Header.setHeaderName("" + (fi - 1)));
                    }
                    if (fi == 0) {
                        return new SimpleObjectProperty<>(ConstraintTypes.Header.setHeaderName("" + (j - 1)));
                    }
                    return new SimpleObjectProperty<>(ConstraintTypes.Data.setDataValue(p.isConstrained(fi - 1, j - 1) ? 1 : 0));
                }
            });
            table.getColumns().add(i, columns[i]);
        }
        ObservableList<Object> rows = FXCollections.observableArrayList();
        IntStream.range(0, numVars + 1).forEach(rows::add);
        table.setItems(rows);
        table.setMinSize((numVars + 1) * COLUMN_WIDTH + 15, (numVars + 1) * COLUMN_HEIGHT);
        table.setPrefSize((numVars + 1) * COLUMN_WIDTH + 15, (numVars + 1) * COLUMN_HEIGHT);
        table.setMaxSize((numVars + 1) * COLUMN_WIDTH + 15, (numVars + 1) * COLUMN_HEIGHT);
        data.setCenter(table);
    }

    private void showConstraintsCosts(int ai, int aj) {
        int aiDomainSize = p.getDomainSize(ai);
        int ajDomainSize = p.getDomainSize(aj);
        table.getColumns().clear();

        TableColumn[] columns = new TableColumn[aiDomainSize + 1];
        for (int i = 0; i < columns.length; i++) {
            columns[i] = new TableColumn("" + i);
            columns[i].setMinWidth(COLUMN_WIDTH);
            columns[i].setPrefWidth(COLUMN_WIDTH);
            columns[i].setMaxWidth(COLUMN_WIDTH);
            columns[i].setCellFactory(p -> {
                return new TableCell() {
                    @Override
                    protected void updateItem(Object t, boolean bln) {
                        super.updateItem(t, bln); //To change body of generated methods, choose Tools | Templates.
                        if (t != null) {
                            ConstraintTypes e = (ConstraintTypes) t;
                            setAlignment(Pos.CENTER);
                            setText(e.toString());
                            getStyleClass().clear();
                            getStyleClass().add(e.getStyleClass());
                            setMinHeight(COLUMN_HEIGHT);
                            setPrefHeight(COLUMN_HEIGHT);
                            setMaxHeight(COLUMN_HEIGHT);                            
                        }
                    }

                };
            });
            int fi = i;
            columns[i].setCellValueFactory(new PropertyValueFactory<Integer, Object>("") {
                @Override
                public ObservableValue<Object> call(TableColumn.CellDataFeatures<Integer, Object> cdf) {
                    int j = cdf.getValue();
                    if (j == 0 && fi == 0) {
                        return new SimpleObjectProperty<>(ConstraintTypes.Header.setHeaderName("" + aj + " / " + ai));
                    }
                    if (j == 0) {
                        return new SimpleObjectProperty<>(ConstraintTypes.Header.setHeaderName("" + (fi - 1)));
                    }
                    if (fi == 0) {
                        return new SimpleObjectProperty<>(ConstraintTypes.Header.setHeaderName("" + (j - 1)));
                    }
                    return new SimpleObjectProperty<>(ConstraintTypes.Data.setDataValue(p.getConstraintCost(ai, fi - 1, aj, j - 1)));
                }
            });
            table.getColumns().add(i, columns[i]);
        }
        ObservableList<Object> rows = FXCollections.observableArrayList();
        IntStream.range(0, ajDomainSize + 1).forEach(rows::add);
        table.setItems(rows);
        table.setMinSize((aiDomainSize + 1) * COLUMN_WIDTH + 15, (ajDomainSize + 1) * COLUMN_HEIGHT);
        table.setPrefSize((aiDomainSize + 1) * COLUMN_WIDTH + 15, (ajDomainSize + 1) * COLUMN_HEIGHT);
        table.setMaxSize((aiDomainSize + 1) * COLUMN_WIDTH + 15, (ajDomainSize + 1) * COLUMN_HEIGHT);        
        data.setCenter(table);
    }

    public void setModel(Experiment exp) {
        Platform.runLater(() -> {
            Visual.populate(testSelect, Visual.adapt(exp.subExperiments(), it -> {
                CPExperimentTest r = (CPExperimentTest) it;
                return new Visual(it, r.getName(), "", null);
            }));

            if (exp.numberOfExecutions() == 0) {
                DataPanel msg = new DataPanel();
                msg.setNoDataText("There are no problems to view");
                data.setCenter(null);
            } else {
                pnumSelect.setText("1");
                switchProblemView();
            }
        });
    }

    private void switchProblemView() {
        Integer pnum = null;
        CPExperimentTest c = null;
        try {
            c = (CPExperimentTest) Visual.getSelected(testSelect);
            pnum = Integer.parseInt(pnumSelect.getText());
            Problem p = c.getProblem(pnum);
            showProblem(p);
            problemViewingDescription.setText("Showing problem " + pnum + " of test " + c.getName());
        } catch (Exception ex) {
            MessageDialog.showFail("cannot load problem (did you defined a problem generator?): ", ex.getMessage());
        }
    }

    private void showProblem(final ImmutableProblem p) {
        if (p.type().isBinary()) {
            this.p = p;
            prepareTree();
            calc.setProblem(p);
//            problemChangePan.setVisible(false);
        }
    }

    private void prepareTree() {
        TreeItem root = new TreeItem(PROBLEM, new ImageView(PROBLEM_ICON));
        root.setExpanded(true);
        tree.setRoot(root);

        TreeItem cMatrix = new TreeItem(CONSTRAINT_MATRIX, new ImageView(CONSTRAINTS_ICON));
        root.getChildren().add(cMatrix);

        int varNum = p.getNumberOfVariables();
        for (int i = 0; i < varNum; i++) {
            TreeItem a1 = new TreeItem(new AgentInfo(i), new ImageView(AGENT1_ICON));
            for (Integer j : p.getNeighbors(i)) {
                TreeItem a2 = new TreeItem(new AgentInfo(j), new ImageView(AGENT2_ICON));
                a1.getChildren().add(a2);
            }
            root.getChildren().add(a1);
        }
    }

    private static class AgentInfo {

        private final String name;
        private final int id;

        public AgentInfo(int id) {
            this.id = id;
            this.name = "Agent " + id;
        }

        public AgentInfo(String name) {
            this.name = name;
            this.id = -1;
        }

        @Override
        public String toString() {
            return this.name;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }

    private static enum ConstraintTypes {

        Header {
                    @Override
                    String getStyleClass() {
                        return "Header";
                    }

                    @Override
                    public String toString() {
                        return this.name;
                    }
                }, Data {
                    @Override
                    String getStyleClass() {
                        return "Data";
                    }

                    @Override
                    public String toString() {
                        return "" + value;
                    }
                };

        String name;
        int value;

        abstract String getStyleClass();

        ConstraintTypes setHeaderName(String name) {
            this.name = name;
            return this;
        }

        ConstraintTypes setDataValue(int value) {
            this.value = value;
            return this;
        }
    }

}
