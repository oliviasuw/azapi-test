/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.lab.beans;

import bgu.dcr.az.db.DBManager;
import bgu.dcr.az.db.ent.Code;
import bgu.dcr.az.lab.util.CreatedTest;
import java.util.LinkedList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author Inka
 */
@ManagedBean
@SessionScoped
public class ExperimentBuilder {

    @ManagedProperty("#{dbManager}")
    private DBManager db;
    private List<CreatedTest> tests;
    private List<Code> agents;
    private List<Code> selectedAgents;
    private List<Code> statisticCollectors;
    private List<Code> selectedStatisticCollectors;
    private List<Code> problemGenerators;
    private Code selectedProblemGenerators;
    private List<Code> limiters;
    private Code selectedLimiters;
    private List<Code> messageDelayers;
    private Code selectedMessageDelayers;
    private List<Code> correctnessTesters;
    private Code selectedCorrectnessTesters;
    
    public void updateCurrentTest(CreatedTest test){
        System.out.println("test is " + test.getName());
    }
    
    public void addNewTest(){
        CreatedTest test = new CreatedTest();
        System.out.println("adding test" + test.getName());
        tests.add(test);
    }

    public DBManager getDb() {
        return db;
    }

    public List<CreatedTest> getTests() {
        if (tests==null){
            tests = new LinkedList<CreatedTest>();
            tests.add(new CreatedTest("Inna"));
            tests.add(new CreatedTest("Dima"));
            tests.add(new CreatedTest("Benny"));
        }
        return tests;
    }

    public List<Code> getAgents() {
        return agents;
    }

    public List<Code> getStatisticCollectors() {
        return statisticCollectors;
    }

    public List<Code> getProblemGenerators() {
        return problemGenerators;
    }

    public List<Code> getLimiters() {
        return limiters;
    }

    public List<Code> getMessageDelayers() {
        return messageDelayers;
    }

    public List<Code> getCorrectnessTesters() {
        return correctnessTesters;
    }

    public void setDb(DBManager db) {
        this.db = db;
    }

    public void setTests(List<CreatedTest> tests) {
        this.tests = tests;
    }

    public void setAgents(List<Code> agents) {
        this.agents = agents;
    }

    public void setStatisticCollectors(List<Code> statisticCollectors) {
        this.statisticCollectors = statisticCollectors;
    }

    public void setProblemGenerators(List<Code> problemGenerators) {
        this.problemGenerators = problemGenerators;
    }

    public void setLimiters(List<Code> limiters) {
        this.limiters = limiters;
    }

    public void setMessageDelayers(List<Code> messageDelayers) {
        this.messageDelayers = messageDelayers;
    }

    public void setCorrectnessTesters(List<Code> correctnessTesters) {
        this.correctnessTesters = correctnessTesters;
    }

    public List<Code> getSelectedAgents() {
        return selectedAgents;
    }

    public List<Code> getSelectedStatisticCollectors() {
        return selectedStatisticCollectors;
    }

    public Code getSelectedProblemGenerators() {
        return selectedProblemGenerators;
    }

    public Code getSelectedLimiters() {
        return selectedLimiters;
    }

    public Code getSelectedMessageDelayers() {
        return selectedMessageDelayers;
    }

    public Code getSelectedCorrectnessTesters() {
        return selectedCorrectnessTesters;
    }

    public void setSelectedAgents(List<Code> selectedAgents) {
        this.selectedAgents = selectedAgents;
    }

    public void setSelectedStatisticCollectors(List<Code> selectedStatisticCollectors) {
        this.selectedStatisticCollectors = selectedStatisticCollectors;
    }

    public void setSelectedProblemGenerators(Code selectedProblemGenerators) {
        this.selectedProblemGenerators = selectedProblemGenerators;
    }

    public void setSelectedLimiters(Code selectedLimiters) {
        this.selectedLimiters = selectedLimiters;
    }

    public void setSelectedMessageDelayers(Code selectedMessageDelayers) {
        this.selectedMessageDelayers = selectedMessageDelayers;
    }

    public void setSelectedCorrectnessTesters(Code selectedCorrectnessTesters) {
        this.selectedCorrectnessTesters = selectedCorrectnessTesters;
    }
    
    
}
