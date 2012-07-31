/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.lab.util;

import bgu.dcr.az.db.ent.Code;
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
    private String name;
    private List<Code> agents;
    private List<Code> statisticCollectors;
    private Code problemGenerator;
    private Code messageDelayer;
    private Code correctnessTester;
    private Code limiter;
    private List<VariableDecleration> variables;

    public CreatedTest() {
        id = counter++;
        this.name = "Test" + id;
        this.agents = new LinkedList<Code>();
        this.statisticCollectors = new LinkedList<Code>();
    }

    public CreatedTest(String name) {
        this.name = name;
        this.agents = new LinkedList<Code>();
        this.statisticCollectors = new LinkedList<Code>();
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
        return problemGenerator;
    }

    public Code getMessageDelayer() {
        return messageDelayer;
    }

    public Code getCorrectnessTester() {
        return correctnessTester;
    }

    public Code getLimiter() {
        return limiter;
    }

    public void setAgents(List<Code> agents) {
        this.agents = agents;
    }

    public void setStatisticCollectors(List<Code> statisticCollectors) {
        this.statisticCollectors = statisticCollectors;
    }

    public void setProblemGenerator(Code problemGenerator) {
        this.problemGenerator = problemGenerator;
    }

    public void setMessageDelayer(Code messageDelayer) {
        this.messageDelayer = messageDelayer;
    }

    public void setCorrectnessTester(Code correctnessTester) {
        this.correctnessTester = correctnessTester;
    }

    public void setLimiter(Code limiter) {
        this.limiter = limiter;
    }
}
