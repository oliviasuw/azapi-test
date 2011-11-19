/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.dev.db;

import bc.utils.FileUtils;
import bgu.csp.az.api.exp.ConnectionFaildException;
import bgu.csp.az.api.infra.stat.Database;
import bgu.csp.az.api.infra.stat.Database.StatisticRecord;
import bgu.csp.az.impl.db.DBConnectionHandler;
import bgu.csp.az.api.infra.stat.StatisticRoot;
import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bennyl
 */
public enum DatabaseUnit {

    UNIT;
    public static final int MAXIMUM_NUMBER_OF_INMEMORY_STATISTICS = 100;
    public static final String DATA_BASE_NAME = "agentzero";
    public static final String STATISTICS_TABLE = "STATISTICS";
    public static final String PARENT_ID_COLUMN = "PARENT_ID";
    public static final String FIELD_COLUMN = "FIELD";
    public static final String ROUND_INDEX_COLUMN = "ROUND_INDEX";
    public static final String ROUND_NAME_COLUMN = "ROUND_NAME";
    public static final String ID_COLUMN = "ID";
    public static final String VAL_COLUMN = "VAL";
    private static final StatisticRecord[] EMPTY_STATISTIC_RECORDS_ARRAY = new StatisticRecord[0];
    private DBConnectionHandler connection;
    private Database statisticDB = null;
    private PreparedStatement insertStatment;
    private Thread collectorThread = null;
    private ArrayBlockingQueue<StatisticRoot> statisticsQueue = new ArrayBlockingQueue<StatisticRoot>(MAXIMUM_NUMBER_OF_INMEMORY_STATISTICS);

    public void connect() throws ConnectionFaildException {
        try {
            connection = new DBConnectionHandler("org.h2.Driver", "jdbc:h2:" + DATA_BASE_NAME, "sa", "");
            connection.connect();
        } catch (SQLException ex) {
            throw new ConnectionFaildException("cannot connect to statistics database", ex);
        } catch (ClassNotFoundException ex) {
            throw new ConnectionFaildException("cannot connect to statistics database", ex);
        }
    }

    public void disconnect() {
        if (connection != null) {
            try {
                connection.disconnect();
            } catch (SQLException ex) {
                Logger.getLogger(DatabaseUnit.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void delete() {
        FileUtils.delete(new File(DATA_BASE_NAME + ".h2.db"));
        FileUtils.delete(new File(DATA_BASE_NAME + ".lock.db"));
        FileUtils.delete(new File(DATA_BASE_NAME + ".trace.db"));
    }

    public Database createStatisticDatabase() {
        if (statisticDB == null) {
            String dropTable = "DROP TABLE " + STATISTICS_TABLE + ";";
            String createTable = "CREATE TABLE " + STATISTICS_TABLE + " (" + ID_COLUMN + " INTEGER NOT NULL, " + ROUND_NAME_COLUMN + " VARCHAR(80) NOT NULL, " + ROUND_INDEX_COLUMN + " INTEGER NOT NULL, " + FIELD_COLUMN + " VARCHAR(80) NOT NULL, " + VAL_COLUMN + " INTEGER NOT NULL, " + PARENT_ID_COLUMN + " INTEGER NOT NULL, PRIMARY KEY (" + ID_COLUMN + "));";
            try {
                //connection.runUpdate(dropTable);
                connection.runUpdate(createTable);
            } catch (SQLException ex) {
                Logger.getLogger(DatabaseUnit.class.getName()).log(Level.SEVERE, null, ex);
            }
            statisticDB = new H2Database();
        }

        return statisticDB;
    }

    public void startCollectorThread() {
        if (collectorThread == null) {
            collectorThread = new Thread(new DBCollector());
            collectorThread.start();
        }
    }

    public void stopCollectorThread() {
        collectorThread.interrupt();
        collectorThread = null;
    }

    public void insert(Database.StatisticRecord record) throws SQLException {
        if (insertStatment == null) {
            insertStatment = connection.prepare("INSERT INTO " + STATISTICS_TABLE + " (" + ID_COLUMN + ", " + ROUND_NAME_COLUMN + ", " + ROUND_INDEX_COLUMN + ", " + FIELD_COLUMN + ", " + VAL_COLUMN + ", " + PARENT_ID_COLUMN + ") VALUES (?,?,?,?,?,?);");
        }

        insertStatment.setInt(1, record.id);
        insertStatment.setString(2, record.roundName);
        insertStatment.setInt(3, record.roundIndex);
        insertStatment.setString(4, record.field);
        insertStatment.setLong(5, record.val);
        insertStatment.setInt(6, record.parentId);
        insertStatment.executeUpdate();
    }

    public void insert(Database.StatisticRecord[] records) throws SQLException {
        connection.startBatch();
        try {
            for (StatisticRecord r : records) {
                insert(r);
            }
        } catch (SQLException ex) {
            connection.rollBackBatch();
            throw ex;
        }
        connection.endAndCommitBatch();
    }

    public void insertLater(StatisticRoot root) {
        try {
            statisticsQueue.put(root);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    public class H2Database implements Database {

        @Override
        public ResultSet query(String query) throws SQLException {
            return connection.runQuery(query);
        }

        @Override
        public List<StatisticRecord> structuredQuery(String query) throws SQLException {
            ResultSet rs = query(query);
            List<StatisticRecord> ret = new LinkedList<StatisticRecord>();
            while (rs.next()) {
                StatisticRecord record = new StatisticRecord(rs.getInt(ID_COLUMN),
                        rs.getString(ROUND_NAME_COLUMN),
                        rs.getInt(ROUND_INDEX_COLUMN),
                        rs.getString(FIELD_COLUMN),
                        rs.getInt(VAL_COLUMN),
                        rs.getInt(PARENT_ID_COLUMN));
                ret.add(record);
            }

            return ret;
        }
    }

    private class DBCollector implements Runnable {

        @Override
        public void run() {
            System.out.println("Statistics Collector Activated");
            List<StatisticRecord> l;
            while (true) {
                try {
                    if (statisticsQueue.isEmpty() && Thread.interrupted()) {
                        return;
                    }

                    StatisticRoot stat = statisticsQueue.take();
                    //OPEN UP THE STATISTICS NODE
                    l = StatisticRecord.process(stat);
                    try {
                        UNIT.insert(l.toArray(EMPTY_STATISTIC_RECORDS_ARRAY));
                    } catch (SQLException ex) {
                        Logger.getLogger(DatabaseUnit.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    public static void main(String[] args) throws ConnectionFaildException, SQLException {
        UNIT.delete();
        UNIT.connect();
        UNIT.createStatisticDatabase();
        UNIT.startCollectorThread();
    }
}
