/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.ui.screens.problem;

import bc.ui.swing.useful.DataPanel;
import bc.ui.swing.visuals.Visual;
import bgu.dcr.az.common.ui.FXUtils;
import bgu.dcr.az.common.ui.panels.FXMessagePanel;
import bgu.dcr.az.dcr.api.problems.Problem;
import bgu.dcr.az.dcr.execution.CPExperimentTest;
import bgu.dcr.az.execs.api.experiments.Experiment;
import bgu.dcr.az.ui.screens.dialogs.MessageDialog;
import bgu.dcr.az.ui.screens.problem.graph.GraphDrawer;
import bgu.dcr.az.ui.screens.problem.graph.ProblemCircleLayout;
import bgu.dcr.az.ui.screens.problem.graph.ProblemFRLayout;
import bgu.dcr.az.ui.screens.problem.graph.ProblemGraphLayout;
import bgu.dcr.az.ui.screens.problem.graph.ProblemISOMLayout;
import bgu.dcr.az.ui.screens.problem.graph.ProblemKKLayout;
import bgu.dcr.az.ui.screens.problem.graph.ProblemSpringLayout;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
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

    private final static int COLUMN_WIDTH = 80;
    private final static int ROW_HEIGHT = 25;

    @FXML
    private BorderPane container;

    @FXML
    private VBox caption;

    @FXML
    private FlowPane top;

    @FXML
    private FlowPane bot;

    @FXML
    private SplitPane split;

    @FXML
    private SplitPane secondSplit;

    @FXML
    private BorderPane data;

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

    private ConstraintCalcConsoleCtl calc;

    public static final String PROBLEM = "Problem";
    public static final String CONSTRAINT_MATRIX = "Constraints Matrix";
    private Problem p;
    private SlidingAnimator slider;

    private BorderPane graphLayoutsPane;
    private ComboBox graphLayoutCombo;
    private SlidingAnimator graphLayoutAnimator;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        graphLayoutsPane = new BorderPane();
        graphLayoutCombo = new ComboBox();
        graphLayoutCombo.getItems().addAll(new ProblemSpringLayout(), new ProblemCircleLayout(), new ProblemFRLayout(), new ProblemISOMLayout(), new ProblemKKLayout());
        graphLayoutCombo.getSelectionModel().selectFirst();
        BorderPane.setAlignment(graphLayoutCombo, Pos.CENTER);
        graphLayoutAnimator = new SlidingAnimator(graphLayoutCombo, null, false);
        graphLayoutsPane.setTop(graphLayoutAnimator);

        graphLayoutCombo.setOnAction(eh -> showProblemGraph());
        TextField t = pnumSelect;
        top.getChildren().remove(pnumSelect);
        pnumSelect = new TextField() {
            @Override
            public void replaceText(int start, int end, String text) {
                final String origin = getText();
                String updated = origin.substring(0, start) + text + origin.substring(end);
                if (!updated.isEmpty()) {
                    try {
                        int num = Integer.parseInt(updated);
                        CPExperimentTest c = (CPExperimentTest) Visual.getSelected(testSelect);
                        if (num > c.getLooper().count()) {
                            return;
                        }
                    } catch (Exception ex) {
                    }
                }
                super.replaceText(start, end, text);
            }

            @Override
            public void replaceSelection(String text) {
                if (!text.isEmpty()) {
                    try {
                        int num = Integer.parseInt(text);
                        CPExperimentTest c = (CPExperimentTest) Visual.getSelected(testSelect);
                        if (num > c.getLooper().count()) {
                            return;
                        }
                    } catch (Exception ex) {
                    }
                }
                super.replaceSelection(text);
            }
        };

        pnumSelect.getStyleClass().addAll(t.getStyleClass());
        top.getChildren().addAll(3, Arrays.asList(pnumSelect));

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
                showProblemGraph();
            } else if (CONSTRAINT_MATRIX.equals(value)) {
                showConstraintsMatrix();
            } else {
                final Object value1 = ((TreeItem) nv).getParent().getValue();
                if (!PROBLEM.equals(value1)) {
                    AgentInfo ai = (AgentInfo) value1;
                    AgentInfo aj = (AgentInfo) value;
                    showConstraintsCosts(ai.getId(), aj.getId());
                } else {
                    showProblemGraph();
                }
            }

        });

        testSelect.valueProperty().addListener((p, ov, nv) -> pnumSelect.setText("1"));
        final List<String> styleClass = new LinkedList<>(top.getParent().getStyleClass());
        slider = new SlidingAnimator(top, bot, true);
        slider.getStyleClass().addAll(styleClass);
        container.setTop(slider);

        changeProblemHyperlink.setOnAction(eh -> slider.scrollDown());
        viewProblemBtn.setOnAction(eh -> {
            slider.scrollUp();
            switchProblemView();
        });
    }

    private void showProblemGraph() {
        Object value = ((TreeItem) tree.getSelectionModel().getSelectedItems().get(0)).getValue();
        ProblemGraphLayout gl = (ProblemGraphLayout) graphLayoutCombo.getSelectionModel().getSelectedItem();

        if (PROBLEM.equals(value)) {
            gl.setProblem(ProblemViewScreenCtl.this.p);
        } else {
            gl.setProblem(p, ((AgentInfo) value).id);
        }

        if (!graphLayoutCombo.isShowing()) {
            graphLayoutAnimator.scrollUp();
        }

        showProblemLayout(gl);

    }

    private void showProblemLayout(ProblemGraphLayout gl) {
        StackPane screen = new StackPane();
        screen.setOnMouseMoved(eh -> {
            if (eh.getY() < 50) {
                graphLayoutAnimator.scrollDown();
            } else {
                if (!graphLayoutCombo.isShowing()) {
                    graphLayoutAnimator.scrollUp();
                }
            }
        });
        screen.setOnMouseExited(eh -> {
            if (!graphLayoutCombo.isShowing()) {
                graphLayoutAnimator.scrollUp();
            }
        });
        data.setCenter(screen);
        final GraphDrawer gd = new GraphDrawer();
        screen.getChildren().addAll(gd, graphLayoutsPane);

        gl.steps(1000);
        gd.draw(gl);

        screen.widthProperty().addListener((p, ov, nv) -> {
            gl.setDimentions(nv.doubleValue(), screen.getHeight());
            gl.steps(1000);
            gd.setDimentions(nv.doubleValue(), screen.getHeight());
            gd.draw(gl);
        });
        screen.heightProperty().addListener((p, ov, nv) -> {
            gl.setDimentions(screen.getWidth(), nv.doubleValue());
            gl.steps(1000);
            gd.setDimentions(screen.getWidth(), nv.doubleValue());
            gd.draw(gl);
        });

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
                            if (e.toString().equals("0")) {
                                getStyleClass().add("zero");
                            }

                            setMinHeight(ROW_HEIGHT);
                            setPrefHeight(ROW_HEIGHT);
                            setMaxHeight(ROW_HEIGHT);
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
        table.setMinSize((numVars + 1) * COLUMN_WIDTH + 4, (numVars + 2) * ROW_HEIGHT + 4);
        table.setPrefSize((numVars + 1) * COLUMN_WIDTH + 4, (numVars + 2) * ROW_HEIGHT + 4);
        table.setMaxSize((numVars + 1) * COLUMN_WIDTH + 4, (numVars + 2) * ROW_HEIGHT + 4);
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
                            setMinHeight(ROW_HEIGHT);
                            setPrefHeight(ROW_HEIGHT);
                            setMaxHeight(ROW_HEIGHT);
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
        table.setMinSize((aiDomainSize + 1) * COLUMN_WIDTH + 4, (ajDomainSize + 2) * ROW_HEIGHT + 4);
        table.setPrefSize((aiDomainSize + 1) * COLUMN_WIDTH + 4, (ajDomainSize + 2) * ROW_HEIGHT + 4);
        table.setMaxSize((aiDomainSize + 1) * COLUMN_WIDTH + 4, (ajDomainSize + 2) * ROW_HEIGHT + 4);
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
                container.setTop(null);
                //container.setLeft(null);
                container.setCenter(FXMessagePanel.createNoDataPanel("No data to view."));
            } else {
                pnumSelect.setText("1");
                container.setTop(slider);
                //container.setLeft(tree);
                container.setCenter(secondSplit);
                switchProblemView();
            }
        });
    }

    private void switchProblemView() {
        Integer pnum = null;
        CPExperimentTest c = null;
        try {
            data.setCenter(FXMessagePanel.createNoDataPanel("No data to view."));
            c = (CPExperimentTest) Visual.getSelected(testSelect);
            pnum = Integer.parseInt(pnumSelect.getText());
            Problem p = c.getProblem(pnum);
            showProblem(p);
            problemViewingDescription.setText("Showing problem " + pnum + " of test " + c.getName());
        } catch (Exception ex) {
            MessageDialog.showFail("cannot load problem (did you defined a problem generator?): ", ex.getMessage());
        }
    }

    private void showProblem(Problem p) {
        if (p.type().isBinary()) {
            this.p = p;
            calc.setProblem(p);
            prepareTree();
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

        tree.getSelectionModel().select(root);
    }

    private static class AgentInfo {

        private final String name;
        private final int id;

        public AgentInfo(int id) {
            this.id = id;
            this.name = "Variable " + id;
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
                        return "header-cell";
                    }

                    @Override
                    public String toString() {
                        return this.name;
                    }
                },
        Data {
                    @Override
                    String getStyleClass() {
                        return "data-cell";
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
