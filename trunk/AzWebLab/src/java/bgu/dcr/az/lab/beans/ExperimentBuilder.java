/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.lab.beans;

import bgu.dcr.az.db.DBManager;
import bgu.dcr.az.db.ent.Code;
import bgu.dcr.az.db.ent.CodeType;
import bgu.dcr.az.lab.util.CreatedTest;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import org.primefaces.event.SelectEvent;

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
    private List<Code> statisticCollectors;
    private List<Code> limiters;
    private List<Code> problemGenerators;
    private List<Code> messageDelayers;
    private List<Code> correctnessTesters;
    private Code[] selectedAgentsFromAll;
    private Code[] selectedStatisticCollectorsFromAll;
    private Code selectedProblemGeneratorFromAll;
    private Code selectedLimiterFromAll;
    private Code selectedMessageDelayerFromAll;
    private Code selectedCorrectnessTesterFromAll;
    private Code selectedAgent;
    private Code selectedStatisticCollector;
    private Code selectedProblemGenerator;
    private Code selectedLimiter;
    private Code selectedMessageDelayer;
    private Code selectedCorrectnessTester;
    private boolean problemGen;
    private boolean msgDelayer;
    private boolean limiter;
    private boolean correctTester;
    private CreatedTest currentTest;
    private Code selectedCode;

    public void updateCurrentTest(CreatedTest test) {
        currentTest = test;
        selectedCode = test.getTest();
        System.out.println("selected Test is" + test);
        System.out.println("selected Tests agents are" + test.getAgents());
    }

    public void addNewSyncTest() {
        CreatedTest test = new CreatedTest("", "Sync");
        tests.add(test);
        updateCurrentTest(test);
    }
    
    public void addNewAsyncTest() {
        CreatedTest test = new CreatedTest("", "Async");
        tests.add(test);
        updateCurrentTest(test);
    }

    public Code getSelectedCode() {
        if (selectedCode == null) {
            selectedCode = new Code();
        }
        System.out.println("selected Code is" + selectedCode.getName());
        return selectedCode;
    }

    public void setSelectedCode(Code selectedCode) {
        this.selectedCode = selectedCode;
        System.out.println("selected Code is" + selectedCode.getName());
    }

    public DBManager getDb() {
        return db;
    }

    public void removeAgent() {
        currentTest.getAgents().remove(selectedAgent);
    }

    public void removeStatCol(Code statCol) {
        currentTest.getStatisticCollectors().remove(selectedStatisticCollector);
    }

    public void removeProblemGen() {
        currentTest.setProblemGenerator(null);
    }

    public void removeMsgDelayer() {
        currentTest.setMessageDelayer(null);
    }

    public void removeCorrectTester() {
        currentTest.setCorrectnessTester(null);
    }

    public void removeLimiter() {
        currentTest.setLimiter(null);
    }

    public List<CreatedTest> getTests() {
        if (tests == null) {
            tests = new LinkedList<CreatedTest>();
            tests.add(new CreatedTest("Inna", "Sync"));
            tests.add(new CreatedTest("Dima", "Async"));
            tests.add(new CreatedTest("Benny", "Sync"));
        }
        return tests;
    }

    public boolean isProblemGen() {
        if (currentTest == null) {
            problemGen = true;
        } else {
            problemGen = (currentTest.getProblemGenerator() == null);
        }
        return problemGen;
    }

    public boolean isMsgDelayer() {
        if (currentTest == null) {
            msgDelayer = true;
        } else {
            msgDelayer = (currentTest.getMessageDelayer() == null);
        }
        return msgDelayer;
    }

    public boolean isLimiter() {
        if (currentTest == null) {
            limiter = true;
        } else {
            limiter = (currentTest.getLimiter() == null);
        }
        return limiter;
    }

    public boolean isCorrectTester() {
        if (currentTest == null) {
            correctTester = true;
        } else {
            correctTester = (currentTest.getCorrectnessTester() == null);
        }
        return correctTester;
    }

    public void saveAgents() {
        System.out.println("test " + currentTest.getName() + "before saving agents: " + currentTest.getAgents());
        
        this.currentTest.getAgents().addAll(Arrays.asList(selectedAgentsFromAll));
        HashSet<Code> temp  = new HashSet<Code>(this.currentTest.getAgents());
        this.currentTest.setAgents(new LinkedList<Code>(temp));
        
        System.out.println("test " + currentTest.getName() + "before saving agents: " + currentTest.getAgents());
        clean(selectedAgentsFromAll);
    }

    public void saveStatisticCollectors() {
        this.currentTest.getStatisticCollectors().addAll(Arrays.asList(selectedStatisticCollectorsFromAll));
        clean(selectedStatisticCollectorsFromAll);
    }

    public void saveProblemGenerator() {
        this.currentTest.setProblemGenerator(selectedProblemGeneratorFromAll);
        selectedProblemGeneratorFromAll = null;
    }

    public void saveLimiter() {
        this.currentTest.setLimiter(selectedLimiterFromAll);
        selectedLimiterFromAll = null;
    }

    public void saveMessageDelayer() {
        this.currentTest.setMessageDelayer(selectedMessageDelayerFromAll);
        selectedMessageDelayerFromAll = null;
    }

    public void saveCorrectnessTester() {
        this.currentTest.setCorrectnessTester(selectedCorrectnessTesterFromAll);
        selectedCorrectnessTesterFromAll = null;
    }

    private void clean(Code[] what) {
        for (int i = 0; i < what.length; i++) {
            what[i] = null;
        }
    }

    public List<Code> getAgents() {
        if (agents == null) {
            agents = Code.getAllCodesByType(db, CodeType.AGENT);
        }
        return agents;
    }

    public List<Code> getStatisticCollectors() {
        if (statisticCollectors == null) {
            statisticCollectors = Code.getAllCodesByType(db, CodeType.STATISTIC_COLLECTOR);
        }
        return statisticCollectors;
    }

    public List<Code> getProblemGenerators() {
        if (problemGenerators == null) {
            problemGenerators = Code.getAllCodesByType(db, CodeType.PROBLEM_GENERATOR);
        }
        return problemGenerators;
    }

    public List<Code> getLimiters() {
        if (limiters == null) {
            limiters = Code.getAllCodesByType(db, CodeType.LIMITER);
        }
        return limiters;
    }

    public List<Code> getMessageDelayers() {
        if (messageDelayers == null) {
            messageDelayers = Code.getAllCodesByType(db, CodeType.MESSAGE_DELAYER);
        }
        return messageDelayers;
    }

    public List<Code> getCorrectnessTesters() {
        if (correctnessTesters == null) {
            correctnessTesters = Code.getAllCodesByType(db, CodeType.CORRECTNESS_TESTER);
        }
        return correctnessTesters;
    }

    public Code[] getSelectedAgentsFromAll() {
        if (selectedAgentsFromAll == null) {
            selectedAgentsFromAll = new Code[getAgents().size()];
        }
        return selectedAgentsFromAll;
    }

    public Code[] getSelectedStatisticCollectorsFromAll() {
        if (selectedStatisticCollectorsFromAll == null) {
            selectedStatisticCollectorsFromAll = new Code[getStatisticCollectors().size()];
        }
        return selectedStatisticCollectorsFromAll;
    }

    public Code getSelectedProblemGeneratorFromAll() {
        return selectedProblemGeneratorFromAll;
    }

    public Code getSelectedLimiterFromAll() {
        return selectedLimiterFromAll;
    }

    public Code getSelectedMessageDelayerFromAll() {
        return selectedMessageDelayerFromAll;
    }

    public Code getSelectedCorrectnessTesterFromAll() {
        return selectedCorrectnessTesterFromAll;
    }

    public Code getSelectedAgent() {
        return selectedAgent;
    }

    public Code getSelectedStatisticCollector() {
        return selectedStatisticCollector;
    }

    public Code getSelectedProblemGenerator() {
        return selectedProblemGenerator;
    }

    public Code getSelectedLimiter() {
        return selectedLimiter;
    }

    public Code getSelectedMessageDelayer() {
        return selectedMessageDelayer;
    }

    public Code getSelectedCorrectnessTester() {
        return selectedCorrectnessTester;
    }

    public CreatedTest getCurrentTest() {
        if (currentTest == null) {
            currentTest = new CreatedTest("", "Sync");
        }
        return currentTest;
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

    public void setLimiters(List<Code> limiters) {
        this.limiters = limiters;
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

    public void setSelectedAgentsFromAll(Code[] selectedAgentsFromAll) {
        this.selectedAgentsFromAll = selectedAgentsFromAll;
    }

    public void setSelectedStatisticCollectorsFromAll(Code[] selectedStatisticCollectorsFromAll) {
        this.selectedStatisticCollectorsFromAll = selectedStatisticCollectorsFromAll;
    }

    public void setSelectedProblemGeneratorFromAll(Code selectedProblemGeneratorFromAll) {
        this.selectedProblemGeneratorFromAll = selectedProblemGeneratorFromAll;
    }

    public void setSelectedLimiterFromAll(Code selectedLimiterFromAll) {
        this.selectedLimiterFromAll = selectedLimiterFromAll;
    }

    public void setSelectedMessageDelayerFromAll(Code selectedMessageDelayerFromAll) {
        this.selectedMessageDelayerFromAll = selectedMessageDelayerFromAll;
    }

    public void setSelectedCorrectnessTesterFromAll(Code selectedCorrectnessTesterFromAll) {
        this.selectedCorrectnessTesterFromAll = selectedCorrectnessTesterFromAll;
    }

    public void setSelectedAgent(Code selectedAgent) {
        this.selectedAgent = selectedAgent;
        this.selectedCode = selectedAgent;
    }

    public void setSelectedStatisticCollector(Code selectedStatisticCollectors) {
        this.selectedStatisticCollector = selectedStatisticCollectors;
        this.selectedCode = selectedStatisticCollectors;
    }

    public void setSelectedProblemGenerator(Code selectedProblemGenerator) {
        this.selectedProblemGenerator = selectedProblemGenerator;
        this.selectedCode = selectedProblemGenerator;
    }

    public void setSelectedLimiter(Code selectedLimiter) {
        this.selectedLimiter = selectedLimiter;
        this.selectedCode = selectedLimiter;
    }

    public void setSelectedMessageDelayer(Code selectedMessageDelayer) {
        this.selectedMessageDelayer = selectedMessageDelayer;
        this.selectedCode = selectedMessageDelayer;
    }

    public void setSelectedCorrectnessTester(Code selectedCorrectnessTester) {
        this.selectedCorrectnessTester = selectedCorrectnessTester;
        this.selectedCode = selectedCorrectnessTester;
    }

    public void setProblemGen(boolean problemGen) {
        this.problemGen = problemGen;
    }

    public void setMsgDelayer(boolean msgDelayer) {
        this.msgDelayer = msgDelayer;
    }

    public void setLimiter(boolean limiter) {
        this.limiter = limiter;
    }

    public void setCorrectTester(boolean correctTester) {
        this.correctTester = correctTester;
    }

    public void setCurrentTest(CreatedTest currentTest) {
        this.currentTest = currentTest;
        this.selectedCode = this.currentTest.getTest();
    }

    public void onRowSelect(SelectEvent event) {
        System.out.println("got selection event, object is " + event.getObject());
        if (event.getObject() instanceof Code) {
            Code code = (Code) event.getObject();
            setSelectedCode(code);
        } else if (event.getObject() instanceof CreatedTest) {
            CreatedTest test = (CreatedTest) event.getObject();
            setSelectedCode(test.getTest());
            currentTest = test;
        }
    }
}
