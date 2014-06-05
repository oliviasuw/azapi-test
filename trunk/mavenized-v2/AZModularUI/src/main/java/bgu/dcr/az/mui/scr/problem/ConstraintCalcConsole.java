/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.mui.scr.problem;

import bgu.dcr.az.common.events.EventListeners;
import bgu.dcr.az.dcr.api.Assignment;
import bgu.dcr.az.dcr.api.problems.ImmutableProblem;
import bgu.dcr.az.mui.RegisterController;
import bgu.dcr.az.mui.jfx.FXMLController;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author Zovadi
 */
@RegisterController("problem.console")
public class ConstraintCalcConsole extends FXMLController {

    @FXML
    private TextArea outputConsole;

    @FXML
    private TextField inputConsole;

    EventListeners<ConstraintShowListener> listeners = EventListeners.create(ConstraintShowListener.class);
    private static final Pattern COMPATEBILITY_PATTERN_1 = Pattern.compile("(\\s*((\\d+)\\s*=\\s*(\\d+))\\s*,*)*\\s*((\\d+)\\s*=\\s*(\\d+))\\s*");
    private static final Pattern SPLITER = Pattern.compile(",*\\s*\\d+\\s*=\\s*\\d+");
    private static final Pattern SEPERATOR = Pattern.compile(",*\\s*(\\d+)\\s*=\\s*(\\d+)");
    private static final Pattern COMPATEBILITY_PATTERN_2 = Pattern.compile("\\s*(\\d+)\\s*vs\\.{0,1}\\s*(\\d+)\\s*");
    private ImmutableProblem p;

    public EventListeners<ConstraintShowListener> getListeners() {
        return listeners;
    }

    public void setInitialConsoleText(String text) {
        outputConsole.setText(text);
    }

    public void appendConsoleText(String text) {
        outputConsole.appendText(text);
//        this.outputConsole.setCaretPosition(this.outputConsole.getText().length());
    }

    public String getInitialConsoleText() {
        return outputConsole.getText();
    }

    private void textActionPerformed() {
        if (p == null) {
            return;
        }
        String t = this.inputConsole.getText();
        if (COMPATEBILITY_PATTERN_1.matcher(t).matches()) {
            calcCost(t);
        } else if (COMPATEBILITY_PATTERN_2.matcher(t).matches()) {
            calcConstraint(t);
        } else {
            //TODO crying DIMA
        }
        this.inputConsole.setText("");
        appendConsoleText("");
    }

    private void calcCost(String t) {
        Matcher m = SPLITER.matcher(t);
        HashMap<Integer, Integer> values = new HashMap<>();
        while (m.find()) {
            String g0 = m.group();
            Matcher mm = SEPERATOR.matcher(g0);
            mm.find();
            values.put(Integer.valueOf(mm.group(1)), Integer.valueOf(mm.group(2)));
        }
        Assignment ass = new Assignment();
        StringBuilder output = new StringBuilder("Calculating cost for: ");
        for (Integer i : values.keySet()) {
            final Integer value = values.get(i);
            if (!p.getDomainOf(i).contains(value)) {
                this.outputConsole.appendText("There is no value " + value + " in the domain of agent " + i + "\n");
            }
            output.append("[").append(i).append("=").append(value).append("],");
            ass.assign(i, value);
        }
//        String substring = output.substring(0, output.length()-1);
        output.deleteCharAt(output.length() - 1);
        int cost = ass.calcCost(p);
        output.append("\nCost is: ").append(cost).append("\n");
        appendConsoleText(output.toString());
    }

    private void calcConstraint(String t) {
        Matcher m = COMPATEBILITY_PATTERN_2.matcher(t);
        m.find();
        String g0 = m.group();
//        mm.find();
        Integer i = Integer.valueOf(m.group(1));
        Integer j = Integer.valueOf(m.group(2));
        if (p.isConstrained(i, j)) {
            listeners.fire().onConstraintShowRequested(i, j);
            String output = "Showing constraint table for Agent " + i + " and agent " + j + "\n";
            appendConsoleText(output);
        } else {
            String output = "There is no constraint table for Agent " + i + " and agent " + j + "\n";
            appendConsoleText(output);
        }
    }

    public void setProblem(ImmutableProblem p) {
        this.p = p;
    }

    public void addListener(ConstraintShowListener ls) {
        this.listeners.add(ls);
    }

    @Override
    protected void onLoadView() {
        inputConsole.setOnAction(eh -> textActionPerformed());
    }

    public static interface ConstraintShowListener {

        void onConstraintShowRequested(int i, int j);
    }

}
