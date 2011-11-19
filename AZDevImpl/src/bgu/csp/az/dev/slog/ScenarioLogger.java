/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.dev.slog;

import bgu.csp.az.api.Constraint;
import bgu.csp.az.api.Message;
import bgu.csp.az.api.pgen.Problem;
import bgu.csp.az.api.infra.stat.Statistic;
import bgu.csp.az.api.agt.SimpleAgent;
import bgu.csp.az.api.infra.Execution;
import bgu.csp.az.api.tools.Assignment;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.db.DerbyEmbeddedDatabaseType;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.table.TableUtils;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bennyl
 */
public class ScenarioLogger {

    public static final Class[] DB_TABLES = new Class[]{
        AgentLogLog.class,
        MessageArgumentLog.class,
        MessageLog.class,
        MessageTypeLog.class,
        ScenarioPartLog.class,
        ProblemConstraintsLog.class,
        ResultLog.class
    };
    private int maxScenarioSize = 10000;
    Execution exec;
    private List[] parts; //one log list for each agent - to remove synchronization.
    Statistic currentCC;
    private int[] nextId;
    private int nextIdMultiplier;
    private HashMap<String, Byte> messageTypes;
    byte nextMessageId = 0;
    private long[] lastScenarioPartIdPerAgent;

    public ScenarioLogger(Execution exec, int numOfAgents) {
        this.exec = exec;
        this.parts = new List[numOfAgents];
        nextIdMultiplier = (int) Math.pow(10, ((int) Math.log10(numOfAgents)) + 1);
        nextId = new int[numOfAgents];

        this.currentCC = exec.getStatisticsTree().getChild(SimpleAgent.CC_PER_AGENT_STATISTIC);
        for (int i = 0; i < this.parts.length; i++) {
            this.parts[i] = new LinkedList();
        }
        messageTypes = new HashMap<String, Byte>();
        lastScenarioPartIdPerAgent = new long[numOfAgents];
    }

    private static JdbcConnectionSource createDerbyJdbcDataSource(String path, boolean create) throws SQLException {
        String creation = create ? ";create=true" : "";
        DerbyEmbeddedDatabaseType dbtype = new DerbyEmbeddedDatabaseType() {

            @Override
            public void appendEscapedEntityName(StringBuilder sb, String word) {
                sb.append(word);
            }
        };
        JdbcConnectionSource connectionSource = new JdbcConnectionSource("jdbc:derby:" + path + creation, dbtype);
        connectionSource.setUsername("AgentZero");
        connectionSource.setPassword("AZPASS");
        return connectionSource;
    }

    private long nextObjectId(int agent) {
        return (((long) (nextId[agent]++)) * nextIdMultiplier) + agent;
    }

    /**
     * returns the scenario part id
     */
    public long logScenarioPart(int agent, long messageid) {
        ScenarioPartLog spart = new ScenarioPartLog();
//        spart.currentCC = currentCC.sumChilds();
        spart.messageId = messageid;
        this.parts[agent].add(spart);
        lastScenarioPartIdPerAgent[agent] = messageid;
        return messageid;
    }

    public void logCrush(Exception ex) {
        ResultLog rlog = new ResultLog();
        final StringWriter str = new StringWriter();
        ex.printStackTrace(new PrintWriter(str));
        rlog.thrownException = str.toString();
        parts[0].add(rlog);
    }

    public void logWrongResult(Assignment result, Assignment rightAssignment) {
        ResultLog rlog = new ResultLog();
        rlog.assignmentProduced = result.toString();
        rlog.assignmentProducedCost = (int) result.calcCost(exec.getGlobalProblem());
        rlog.goodAssignment = rightAssignment.toString();
        rlog.goodAssignmentCost = (int) rightAssignment.calcCost(exec.getGlobalProblem());
        parts[0].add(rlog);
    }

    public long logMessageSent(int sender, int receiver, Message msg) {
        MessageLog message = new MessageLog();
        message.id = nextObjectId(sender);
        message.scenerioPartSentInId = lastScenarioPartIdPerAgent[sender];

        if (messageTypes.containsKey(msg.getName())) {
            message.messageTypeId = messageTypes.get(msg.getName());
        } else {
            messageTypes.put(msg.getName(), nextMessageId);
            message.messageTypeId = nextMessageId++;
            MessageTypeLog mtl = new MessageTypeLog();
            mtl.id = message.messageTypeId;
            mtl.name = msg.getName();
            parts[sender].add(mtl);
        }

        message.receiver = receiver;
        message.sender = sender;
        parts[sender].add(message);

        logMessageArguments(sender, message.id, msg);

        return message.id;
    }

    public void logAgentLog(int agent, String data) {
        AgentLogLog log = new AgentLogLog();
        log.data = data;
        log.scenarioPartId = lastScenarioPartIdPerAgent[agent];
        log.id = nextObjectId(agent);

        parts[agent].add(log);
    }

    private void logMessageArguments(int sendAgent, long id, Message msg) {
        byte idx = 0;
        for (Object arg : msg.getArgs()) {
            MessageArgumentLog mal = new MessageArgumentLog();
            mal.argumentString = arg.toString();
            mal.index = idx++;
            mal.messageId = id;
            mal.type = arg.getClass().getSimpleName();
            parts[sendAgent].add(mal);
        }
    }

    /**
     * get or create database in the given path
     */
    public static JdbcConnectionSource getNewDataBaseConnection(String path) {
        try {
            if (new File(path).exists()) {
                return createDerbyJdbcDataSource(path, false);
            }
            JdbcConnectionSource connectionSource = createDerbyJdbcDataSource(path, true);

            for (Class table : DB_TABLES) {
                TableUtils.createTable(connectionSource, table);
            }

            return connectionSource;
        } catch (SQLException ex) {
            Logger.getLogger(ScenarioLogger.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public void exportToDB(String dbFileName) {
        try {
            final String path = new File(dbFileName).getAbsolutePath();
            JdbcConnectionSource connsource = getNewDataBaseConnection(path);
            Dao dao;
            ProblemConstraintsLog constraintLog;
            List<SimpleEntry<Integer, Integer>> constraintedItems;

            //EXPORT CONSTRAINTS
            Problem p = exec.getGlobalProblem();

            List constraintList = this.parts[0];
            /*for (Constraint constraint : p.getConstraints()) {
                constraintLog = new ProblemConstraintsLog();
                constraintLog.cost = (int) constraint.getCost();
                constraintedItems = constraint.getConstrainted();
                if (constraint.isBinary()) {
                    constraintLog.var1 = constraintedItems.get(0).getKey();
                    constraintLog.val1 = constraintedItems.get(0).getValue();
                    constraintLog.var2 = constraintedItems.get(1).getKey();
                    constraintLog.val2 = constraintedItems.get(1).getValue();
                } else { //assume unary..
                    constraintLog.var1 = constraintedItems.get(0).getKey();
                    constraintLog.val1 = constraintedItems.get(0).getValue();
                    constraintLog.var2 = constraintedItems.get(0).getKey();
                    constraintLog.val2 = constraintedItems.get(0).getValue();
                }

                constraintList.add(constraintLog);
            }*/


            //WRITE TO DB
            for (List part : parts) {
                for (Object l : part) {
//                    dao = DaoManager.createDao(connsource, l.getClass());
                    //                  dao.create(l);
                }
            }

            connsource.close();
        } catch (SQLException ex) {
            Logger.getLogger(ScenarioLogger.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
