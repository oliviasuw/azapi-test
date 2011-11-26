/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.impl.db;

import bc.utils.FileUtils;
import bgu.csp.az.api.exp.ConnectionFaildException;
import bgu.csp.az.api.infra.Round;
import bgu.csp.az.api.infra.stat.DBRecord;
import bgu.csp.az.api.infra.stat.Database;
import java.io.File;
import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractMap.SimpleEntry;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Semaphore;
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
    private DBConnectionHandler connection;
    private Thread collectorThread = null;
    private ArrayBlockingQueue<SimpleEntry<DBRecord, Round>> dbQueue = new ArrayBlockingQueue<SimpleEntry<DBRecord, Round>>(MAXIMUM_NUMBER_OF_INMEMORY_STATISTICS);
    private Set<Class<? extends DBRecord>> knownRecords = new HashSet<Class<? extends DBRecord>>();
    private Map<Class<? extends DBRecord>, PreparedStatement> insertStatments = new HashMap<Class<? extends DBRecord>, PreparedStatement>();
    private Map<Signal, Signal> signals = Collections.synchronizedMap(new HashMap<Signal, Signal>());
    private List<SignalListner> signalListeners = new LinkedList<SignalListner>();
    
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

    public void addSignalListener(SignalListner sl){
        signalListeners.add(sl);
    }
    
    public void removeSignalListener(SignalListner sl){
        signalListeners.remove(sl);
    }
    
    /**
     * will return true if signal with that key exists in the system and 
     * the requesting thread waited untill this signal received
     * @param signal
     * @return
     * @throws InterruptedException 
     */
    public boolean awaitSignal(Object signal) throws InterruptedException {
        final Signal fake = new Signal(signal);
        if (signals.containsKey(fake)) {
            Signal real = signals.get(fake);
            real.await();
            return true;
        } else {
            return false;
        }
    }
    
    public boolean isSignaled(Object signal){
        Signal fake = new Signal(signal);
        return signals.containsKey(fake) && signals.get(fake).isSignaled();
    }

    public void signal(Object signal) {
        Signal s = new Signal(signal);
        dbQueue.add(s);
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

    public Database createDatabase() {
        return new H2Database();
    }

    public void insert(DBRecord record, Round round) throws SQLException {
        if (!knownRecords.contains(record.getClass())) {
            generateTable(record);
            insertStatments.put(record.getClass(), generatePreparedStatement(record));
            knownRecords.add(record.getClass());
        }
        PreparedStatement insertStatement = insertStatments.get(record.getClass());
        insertStatement.setObject(1, round.getName());
        int i = 2;
        for (Field f : record.getFields()) {
            try {
                insertStatement.setObject(i++, f.get(record));
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(DatabaseUnit.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(DatabaseUnit.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        insertStatement.executeUpdate();
    }

    public void startBatch() {
        try {
            connection.startBatch();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseUnit.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void endAndCommitBatch() throws SQLException {
        connection.endAndCommitBatch();
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

    private PreparedStatement generatePreparedStatement(DBRecord record) throws SQLException {
        StringBuilder sb = new StringBuilder("INSERT INTO ").append(record.provideTableName());
        sb.append("(ROUND");
        for (Field f : record.getFields()) {
            sb.append(", ").append(f.getName());
        }
        sb.append(") VALUES (?");
        for (Field f : record.getFields()) {
            sb.append(",?");
        }
        sb.append(");");

        return connection.prepare(sb.toString());
    }

    public void insertLater(DBRecord record, Round round) {
        try {
            dbQueue.put(new SimpleEntry<DBRecord, Round>(record, round));
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    private void generateTable(DBRecord record) throws SQLException {
        StringBuilder exe = new StringBuilder("CREATE TABLE ").append(record.provideTableName()).append(" (");
        exe.append("ID INTEGER NOT NULL AUTO_INCREMENT, ROUND VARCHAR(50) NOT NULL ");
        for (Field f : record.getFields()) {
            exe.append(", ").append(f.getName());
            if (Boolean.class == f.getType() || boolean.class == f.getType()) {
                exe.append(" BOOLEAN");
            } else if (Double.class == f.getType() || double.class == f.getType()) {
                exe.append(" DOUBLE");
            } else if (Float.class == f.getType() || float.class == f.getType()) {
                exe.append(" FLOAT");
            } else if (Integer.class == f.getType() || int.class == f.getType()) {
                exe.append(" INTEGER");
            } else if (Character.class == f.getType() || char.class == f.getType()) {
                exe.append(" CHAR");
            } else if (String.class == f.getType()) {
                exe.append(" VARCHAR(150)");
            } else if (Long.class == f.getType() || long.class == f.getType()) {
                exe.append(" BIGINT");
            }
        }
        exe.append(", PRIMARY KEY (ID));");
        connection.runUpdate(exe.toString());
    }

    public class H2Database implements Database {

        @Override
        public ResultSet query(String query) throws SQLException {
            return connection.runQuery(query);
        }
    }

    private class Signal extends SimpleEntry<DBRecord, Round> {

        Object key;
        Semaphore lock;
        volatile boolean signaled;

        public Signal(Object key) {
            super(null, null);
            this.key = key;
            signaled = false;
            lock = new Semaphore(0);
        }

        public boolean isSignaled() {
            return signaled;
        }

        void signal() {
            signaled = true;
            lock.release(lock.getQueueLength() + 1 /* ADD ONE FOR THE POSIBILITY WHERE THERE ARE NO THREADS BUT 1 THAT JUST ENTERED */);
            for (SignalListner sl : signalListeners) {
                sl.onDataCollectedUpToSignal(UNIT, key);
            }
        }

        void await() throws InterruptedException {
            if (signaled) {
                return;
            }
            lock.acquire();
            lock.release();
        }

        @Override
        public boolean equals(Object o) {
            return o != null
                    && o instanceof Signal
                    && ((Signal) o).key != null
                    && ((Signal) o).key.equals(this.key);
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 67 * hash + (this.key != null ? this.key.hashCode() : 0);
            return hash;
        }
    }

    private class DBCollector implements Runnable {

        @Override
        public void run() {
            System.out.println("Statistics Collector Activated");
            SimpleEntry<DBRecord, Round> stat;
            while (true) {
                try {
                    if (dbQueue.isEmpty() && Thread.interrupted()) {
                        return;
                    }
                    List<SimpleEntry<DBRecord, Round>> multi = new LinkedList<SimpleEntry<DBRecord, Round>>();
                    if (dbQueue.isEmpty()) {
                        stat = dbQueue.take();
                        multi.add(stat);
                    } else {
                        dbQueue.drainTo(multi);
                    }

                    if (multi.size() > 1) {
                        UNIT.startBatch();
                    }

                    for (SimpleEntry<DBRecord, Round> m : multi) {
                        if (m instanceof Signal) {
                            ((Signal) m).signal();
                        } else {
                            try {
                                UNIT.insert(m.getKey(), m.getValue());
                            } catch (SQLException ex) {
                                Logger.getLogger(DatabaseUnit.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                    
                    if (multi.size() > 1) {
                        try {
                            UNIT.endAndCommitBatch();
                        } catch (SQLException ex) {
                            Logger.getLogger(DatabaseUnit.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }

                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
    
    public static interface SignalListner{
        void onDataCollectedUpToSignal(DatabaseUnit source, Object signal);
    }
}
