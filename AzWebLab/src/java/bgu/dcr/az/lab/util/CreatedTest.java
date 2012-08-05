/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.lab.util;

import bgu.dcr.az.db.ent.Code;
import bgu.dcr.az.db.ent.CodeType;
import bgu.dcr.az.db.ent.VariableDecleration;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Inka
 */
public class CreatedTest {

    private static int counter = 0;
    private int id;
    private Code test;
    private String name;
    private List<Code> agents;
    private List<Code> statisticCollectors;
    private List<Code> problemGenerators;
    private List<Code> messageDelayers;
    private List<Code> correctnessTesters;
    private List<Code> limiters;
    private List<VariableDecleration> variables;
    private String type;

    public CreatedTest(String name, String type) {
        id = counter++;
        if (name.equals("")) {
            name = "Test" + id;
        }
        this.type = type;
        this.name = name;
        this.agents = new LinkedList<Code>();
        this.statisticCollectors = new LinkedList<Code>();
        this.test = new Code();
        this.test.setName(name);
        this.test.setType(CodeType.TEST);
        this.test.addVariable(new VariableDecleration("Name","",name,""));
    }

    public void setTest(Code test) {
        this.test = test;
    }

    public String getType() {
        return type;
    }

    public void setProblemGenerators(List<Code> problemGenerators) {
        this.problemGenerators = problemGenerators;
    }

    public void setMessageDelayers(List<Code> messageDelayers) {
        this.messageDelayers = messageDelayers;
    }

    public void setCorrectnessTesters(List<Code> correctnessTesters) {
        this.correctnessTesters = correctnessTesters;
    }

    public void setLimiters(List<Code> limiters) {
        this.limiters = limiters;
    }

    public void setType(String type) {
        this.type = type;
    }

    
    
    public Code getTest() {
        return test;
    }

    public int getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setVariables(List<VariableDecleration> variables) {
        this.variables = variables;
    }

    public String getName() {
        return name;
    }

    public List<VariableDecleration> getVariables() {
        return variables;
    }

    public List<Code> getAgents() {
        return agents;
    }

    public List<Code> getStatisticCollectors() {
        return statisticCollectors;
    }

    public Code getProblemGenerator() {
        if (problemGenerators==null){
            return null;
        }
        return problemGenerators.get(0);
    }

    public Code getMessageDelayer() {
        if (messageDelayers==null){
            return null;
        }
        return messageDelayers.get(0);
    }

    public Code getCorrectnessTester() {
        if (correctnessTesters==null){
            return null;
        }
        return correctnessTesters.get(0);
    }

    public List<Code> getProblemGenerators() {
        return problemGenerators;
    }

    public List<Code> getMessageDelayers() {
        return messageDelayers;
    }

    public List<Code> getCorrectnessTesters() {
        return correctnessTesters;
    }

    public Code getLimiter() {
        if (limiters==null){
            return null;
        }
        return limiters.get(0);
    }

    public List<Code> getLimiters() {
        return limiters;
    }
    public void setAgents(List<Code> agents) {
        this.agents = agents;
    }

    public void setStatisticCollectors(List<Code> statisticCollectors) {
        this.statisticCollectors = statisticCollectors;
    }

    public void setProblemGenerator(Code problemGenerator) {
        this.problemGenerators.add(0, problemGenerator);
    }

    public void setMessageDelayer(Code messageDelayer) {
        this.messageDelayers.add(0, messageDelayer);
    }

    public void setCorrectnessTester(Code correctnessTester) {
        this.correctnessTesters.add(0, correctnessTester);
    }

    public void setLimiter(Code limiter) {
        this.limiters.add(0, limiter);
    }
}
