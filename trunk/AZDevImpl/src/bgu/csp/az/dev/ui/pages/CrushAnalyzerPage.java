/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.dev.ui.pages;

import com.j256.ormlite.stmt.PreparedQuery;
import java.util.LinkedList;
import java.util.LinkedHashMap;
import bam.utils.JavaUtils;
import bam.utils.ui.mvc.GenericTreeModel.Node;
import static bam.utils.SwingUtils.*;
import bam.utils.ui.mvc.DataExtractor;
import bam.utils.ui.mvc.GenericListModel;
import bam.utils.ui.mvc.GenericTableModel;
import bam.utils.ui.mvc.GenericTreeModel;
import bam.utils.ui.mvc.pages.Page;
import bgu.csp.az.dev.slog.AgentLogLog;
import bgu.csp.az.dev.slog.MessageArgumentLog;
import bgu.csp.az.dev.slog.MessageLog;
import bgu.csp.az.dev.slog.MessageTypeLog;
import bgu.csp.az.dev.slog.ScenarioPartLog;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.RawRowMapper;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.ListModel;

import static bam.utils.JavaUtils.*;

/**
 *
 * @author bennyl
 */
public class CrushAnalyzerPage extends Page {

    CrushAnalyzerView view;
    JdbcConnectionSource dbConnection;
    GenericTableModel<ScenarioPartSummerizedData> scenarioModel2;
    GenericTreeModel menuTreeModel;
    LinkedList<Listener> listeners = new LinkedList<Listener>();
    ScenarioPartFullData currentSPDeepView;

    @SuppressWarnings("LeakingThisInConstructor")
    public CrushAnalyzerPage() {
        super("Crush Analyzer", resIcon("resources/img/eye.png"));

        //LOAD PROBLEM VIEW 
    }

    public void addListener(Listener l) {
        listeners.add(l);
    }

    public ScenarioPartFullData getCurrentSPDeepView() {
        return currentSPDeepView;
    }

    public void setSelectedScenarioPart(ScenarioPartSummerizedData s) {
        if (currentSPDeepView != null && currentSPDeepView.overview.mdata.id == s.mdata.id) {
            return;
        }
        currentSPDeepView = new ScenarioPartFullData(s, dbConnection);
        fireFullDataShownChanged();
    }

    public void generateMenuModel() {
        menuTreeModel = new GenericTreeModel(new MenuNode());

    }

    public GenericTreeModel getMenuTreeModel() {
        return menuTreeModel;
    }

    public GenericTableModel<ScenarioPartSummerizedData> getScenarioModel2() {
        return scenarioModel2;
    }

    @Override
    public JPanel getView() {
        if (view == null) {
            view = new CrushAnalyzerView();
            view.setModel(this);
        }

        return view;
    }

    @Override
    public void disposeView() {
    }

    public void setDBConnection(JdbcConnectionSource connection) {
        this.dbConnection = connection;
        final List<ScenarioPartSummerizedData> scn = ScenarioPartSummerizedData.loadScenario(connection);
        scenarioModel2 = new GenericTableModel<ScenarioPartSummerizedData>(new DataExtractor<ScenarioPartSummerizedData>("CC", "From", "To", "Message") {

            @Override
            public Object getData(String dataName, ScenarioPartSummerizedData from) {
                if (dataName.equals("CC")) {
                    return from.handledCC;
                }
                if (dataName.equals("From")) {
                    return from.mdata.sender;
                }
                if (dataName.equals("To")) {
                    return from.mdata.receiver;
                }
                if (dataName.equals("Message")) {
                    return from.mdata.name;
                }
                return "??????";
            }
        });
        scenarioModel2.fillWith(scn);

        generateMenuModel();
    }

    private static Map<String, String> mapData(String[] columns, String[] values) {
        HashMap<String, String> m = new HashMap<String, String>();
        for (int i = 0; i < columns.length; i++) {
            m.put(columns[i], values[i]);
        }

        return m;
    }

    private void fireFullDataShownChanged() {
        for (Listener l : listeners) {
            l.onScenarioPartFullDataShownChanged(this, currentSPDeepView);
        }
    }

    /**
     * 
     * @param id
     * @return true if there is such a scenario part or false if this message was not handled thus there was no scenario part for here.
     */
    public boolean setSelectedScenarioPartById(long id) {
        if (currentSPDeepView != null && currentSPDeepView.overview.mdata.id == id) {
            return true;
        }
        for (ScenarioPartSummerizedData s : scenarioModel2.getInnerData()) {
            if (s.mdata.id == id) {
                setSelectedScenarioPart(s);
                return true;
            }
        }

        return false;
    }

    public static class MessageData {

        public String sender;
        public String receiver;
        public String name;
        public long id;

        @Override
        public String toString() {
            return "" + name + " To Agent " + receiver;
        }
    }

    public static class ScenarioPartSummerizedData {

        public long handledCC;
        public MessageData mdata;

        public static List<ScenarioPartSummerizedData> loadScenario(final JdbcConnectionSource con) {
            try {
                Dao<ScenarioPartLog, ?> dao = DaoManager.createDao(con, ScenarioPartLog.class);
                String sql = "SELECT sp.MESSAGE_ID, sp.CURRENT_CC, m.SENDER, m.RECEIVER, mt.NAME "
                        + "FROM ScenarioParts as sp, Messages as m, MessageTypes as mt "
                        + "WHERE "
                        + "sp.MESSAGE_ID = m.ID AND "
                        + "m.MESSAGE_TYPE_ID = mt.ID "
                        + "ORDER BY sp.CURRENT_CC";
                GenericRawResults<ScenarioPartSummerizedData> data = dao.queryRaw(sql, new RawRowMapper<ScenarioPartSummerizedData>() {

                    @Override
                    public ScenarioPartSummerizedData mapRow(String[] columnNames, String[] resultColumns) throws SQLException {
                        Map<String, String> m = mapData(columnNames, resultColumns);
                        ScenarioPartSummerizedData ret = new ScenarioPartSummerizedData();
                        ret.handledCC = clong(m.get(ScenarioPartLog.CURRENT_CC_COLUMN));
                        ret.mdata = new MessageData();
                        ret.mdata.id = clong(m.get(ScenarioPartLog.MESSAGE_ID_COLUMN));
                        ret.mdata.name = m.get(MessageTypeLog.NAME_COLUMN);
                        ret.mdata.receiver = m.get(MessageLog.RECEIVER_FIELD);
                        ret.mdata.sender = m.get(MessageLog.SENDER_FIELD);

                        return ret;
                    }
                });

                return data.getResults();

            } catch (SQLException ex) {
                Logger.getLogger(CrushAnalyzerPage.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }
        }

        @Override
        public boolean equals(Object obj) {
            return obj != null
                    && obj instanceof ScenarioPartSummerizedData
                    && ((ScenarioPartSummerizedData) obj).mdata.id == this.mdata.id;
        }
    }

    public static class ScenarioPartFullData {

        public ScenarioPartSummerizedData overview;
        public GenericListModel<String> logs;
        public GenericListModel<MessageArgumentLog> receivedArguments;
        public GenericListModel<MessageData> sentMessages;

        public ScenarioPartFullData(ScenarioPartSummerizedData ovw, ConnectionSource conn) {
            try {
                this.overview = ovw;
                Dao<AgentLogLog, ?> logDao = DaoManager.createDao(conn, AgentLogLog.class);
                String sql = "SELECT l.DATA FROM LOGS as l where l.SCENARIO_PART_ID = " + ovw.mdata.id;
                logs = new GenericListModel<String>();
                logs.fillWith(logDao.queryRaw(sql, new RawRowMapper<String>() {

                    @Override
                    public String mapRow(String[] columnNames, String[] resultColumns) throws SQLException {
                        return resultColumns[0];
                    }
                }).getResults());

                receivedArguments = new GenericListModel<MessageArgumentLog>();
                Dao<MessageArgumentLog, ?> margsDao = DaoManager.createDao(conn, MessageArgumentLog.class);
                PreparedQuery<MessageArgumentLog> query = margsDao.queryBuilder().where().eq(MessageArgumentLog.MESSAGE_ID_COLUMN, ovw.mdata.id).prepare();

                receivedArguments.fillWith(margsDao.query(query));

                sentMessages = new GenericListModel<MessageData>();
                Dao<MessageLog, ?> smDao = DaoManager.createDao(conn, MessageLog.class);
                sql = "SELECT m.ID, m.SENDER, m.RECEIVER, mt.NAME "
                        + "FROM Messages as m, MessageTypes as mt "
                        + "WHERE "
                        + "m.MESSAGE_TYPE_ID = mt.ID AND "
                        + "m." + MessageLog.SCENARIO_PART_SENT_IN_ID_FIELD + " = " + ovw.mdata.id + " "
                        + "ORDER BY m.ID";
                sentMessages.fillWith(smDao.queryRaw(sql, new RawRowMapper<MessageData>() {

                    @Override
                    public MessageData mapRow(String[] columnNames, String[] resultColumns) throws SQLException {
                        Map<String, String> data = mapData(columnNames, resultColumns);
                        MessageData ret = new MessageData();
                        ret.id = JavaUtils.clong(data.get(MessageLog.ID_FIELD));
                        ret.name = data.get(MessageTypeLog.NAME_COLUMN);
                        ret.receiver = data.get(MessageLog.RECEIVER_FIELD);
                        ret.sender = data.get(MessageLog.SENDER_FIELD);
                        return ret;
                    }
                }).getResults());

            } catch (SQLException ex) {
                Logger.getLogger(CrushAnalyzerPage.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static class MenuNode extends Node {

        private static final int ROOT_TAG = 0;
        private static final int MENU_TAG = 1;
        private static final Map<String, ImageIcon> MENU_DATA = JavaUtils.assoc(new LinkedHashMap<String, ImageIcon>(),
                "Scenario Log", resIcon("scenario-log-menu"),
                "Agents Log", resIcon("agents-log-menu"),
                "Problem view", resIcon("problem-log-view"),
                "Generated Pseudo Tree View", resIcon("ptree-log-view"));

        public MenuNode() {
            super("Scenerio Analyzing", null);
            super.setTag(ROOT_TAG);
        }

        @Override
        protected Node createChildNode(Object t) {
            MenuNode ret = new MenuNode();
            ret.setTag(MENU_TAG);
            ret.setIcon(MENU_DATA.get(t.toString()));
            ret.setParent(this);
            ret.setValue(t);
            return ret;
        }

        @Override
        protected Object[] _getChilds() {
            switch (getTag()) {
                case ROOT_TAG:
                    return MENU_DATA.keySet().toArray();
                default:
                    return new Object[0];
            }
        }
    }

    public static interface Listener {

        void onScenarioPartFullDataShownChanged(CrushAnalyzerPage source, ScenarioPartFullData s);
    }
}
