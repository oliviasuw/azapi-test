/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.orm.impl;

import bgu.dcr.az.common.reflections.ReflectionUtils;
import bgu.dcr.az.orm.api.DBRecord;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author User
 */
public class H2EmbeddedDatabaseWriter implements Runnable {

    static final Object TERMINATION_SIGNAL = new Object();
    static final Object CALLBACK_SIGNAL = new Object();

    BlockingQueue q = new LinkedBlockingQueue(); //accessed from producer threads
    Map<Class, RecordManipulator> recordManipulators = new IdentityHashMap<>(); // accessed from this thread only
    H2EmbeddedDatabaseManager manager;
    Semaphore synchronizationLock = new Semaphore(0);

    public H2EmbeddedDatabaseWriter(H2EmbeddedDatabaseManager manager) {
        this.manager = manager;
    }

    @Override
    public void run() {
        try {
            LinkedList batch = new LinkedList();
            while (true) {
                batch.add(q.take());
                q.drainTo(batch);

                while (!batch.isEmpty()) {
                    Object item = batch.removeFirst();
                    if (item == TERMINATION_SIGNAL) {
                        terminate();
                        return;
                    } else if (item == CALLBACK_SIGNAL) {
                        synchronizationLock.release();
                    } else if (item instanceof DefineTableCommand) {
                        defineTable((DefineTableCommand) item);
                    } else if (item instanceof ExecuteUpdateCommand) {
                        executeUpdate((ExecuteUpdateCommand) item);
                    } else {
                        addToPreparedStatementBatch(item);
                    }
                }

                commitBatch();
            }
        } catch (InterruptedException | SQLException ex) {
            try {
                terminate();
            } catch (SQLException ex1) {
                Logger.getLogger(H2EmbeddedDatabaseWriter.class.getName()).log(Level.SEVERE, null, ex1);
            } finally {
                manager.onWriterThreadFailed(ex);
            }
        }
    }

    private void executeUpdate(ExecuteUpdateCommand cmd) throws SQLException {
        if (cmd.parameters == null || cmd.parameters.length == 0) {
            try (Statement st = manager.getConnection().createStatement()) {
                System.out.println("creating table: \n" + cmd.sql);
                st.executeUpdate(cmd.sql);
            }
        } else {
            try (PreparedStatement st = manager.getConnection().prepareStatement(cmd.sql)) {
                for (int i = 0; i < cmd.parameters.length; i++) {
                    st.setObject(i + 1, cmd.parameters[i]);
                }
                System.out.println("creating table: \n" + st.toString());
                st.executeUpdate(cmd.sql);
            }
        }
    }

    private void defineTable(DefineTableCommand cmd) throws SQLException {
        if (!recordManipulators.containsKey(cmd.recordType)) {
            RecordManipulator manipulator = new RecordManipulator(cmd.tableName, cmd.recordType, manager.getConnection());
            recordManipulators.put(cmd.recordType, manipulator);

            manipulator.defineTable(manager.getConnection());
        }
    }

    private void addToPreparedStatementBatch(Object item) throws SQLException {
        RecordManipulator manipulator = recordManipulators.get(item.getClass());
        if (manipulator == null) {
            throw new SQLException("Unknown record type: " + item.getClass().getCanonicalName() + " (did you forget to call defineTable?)");
        }

        manipulator.insert(item);
    }

    private void commitBatch() throws SQLException {
        for (RecordManipulator m : recordManipulators.values()) {
            m.commit();
        }

        manager.getConnection().commit();
    }

    private void terminate() throws SQLException {
        for (RecordManipulator m : recordManipulators.values()) {
            m.insertStatement.close();
        }
    }

    void close() throws InterruptedException {
        q.add(TERMINATION_SIGNAL);
        synchronize();
    }

    void appendDefineTableCommand(String name, Class<? extends DBRecord> recordType) {
        q.add(new DefineTableCommand(name, recordType));
    }

    void appendExecuteUpdateCommand(String sql, Object... parameters) {
        q.add(new ExecuteUpdateCommand(sql, parameters));
    }

    void synchronize() throws InterruptedException {
        System.out.println("Sync Against last version of Database");
        q.add(CALLBACK_SIGNAL);
        synchronizationLock.acquire();
    }

    void appendInsertionCommand(Object o) {
        q.add(o);
    }

    private static class ExecuteUpdateCommand {

        String sql;
        Object[] parameters;

        public ExecuteUpdateCommand(String sql, Object[] parameters) {
            this.sql = sql;
            this.parameters = parameters;
        }
    }

    private static class DefineTableCommand {

        String tableName;
        Class recordType;

        public DefineTableCommand(String tableName, Class recordType) {
            this.tableName = tableName;
            this.recordType = recordType;
        }

    }

    private static class RecordManipulator {

        List<Field> fields;
        PreparedStatement insertStatement;
        String tableName;
        Class recordClass;
        private Connection connection;

        public RecordManipulator(String tableName, Class recordClass, Connection connection) throws SQLException {
            fields = ReflectionUtils.allFields(recordClass);
            this.tableName = tableName;
            this.recordClass = recordClass;
            this.connection = connection;

        }

        private void createInsertStatement() throws SQLException, SecurityException {
            StringBuilder sb = new StringBuilder("INSERT INTO ").append(tableName).append(" (");
            for (Field f : fields) {
                f.setAccessible(true);
                sb.append(f.getName()).append(",");
            }

            sb.deleteCharAt(sb.length() - 1).append(") VALUES (");
            for (int i = 0; i < fields.size(); i++) {
                sb.append("?,");
            }
            sb.deleteCharAt(sb.length() - 1).append(");");

            insertStatement = connection.prepareStatement(sb.toString());
        }

        void defineTable(Connection connection) throws SQLException {
            StringBuilder exe = new StringBuilder("CREATE TABLE ").append(tableName).append(" (");
            exe.append("ID INTEGER NOT NULL AUTO_INCREMENT ");
            for (Field f : fields) {
                exe.append(", ").append(f.getName());
                if (Boolean.class == f.getType() || boolean.class == f.getType()) {
                    exe.append(" BOOLEAN");
                } else if (Double.class == f.getType() || double.class == f.getType()) {
                    exe.append(" DECIMAL(20, 3)");
                } else if (Float.class == f.getType() || float.class == f.getType()) {
                    exe.append(" DECIMAL(14, 3)");
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

            try (Statement st = connection.createStatement()) {
                System.out.println("creating table: \n" + exe.toString());
                st.executeUpdate(exe.toString());
            }
        }

        private void insert(Object item) throws SQLException {
            if (insertStatement == null) {
                createInsertStatement();
            }

            try {
                for (int i = 0; i < fields.size(); i++) {
                    insertStatement.setObject(i + 1, fields.get(i).get(item));
                }

                insertStatement.addBatch();
            } catch (IllegalArgumentException | IllegalAccessException ex) {
                throw new SQLException("cannot insert object " + item);
            }
        }

        void commit() throws SQLException {
            if (insertStatement != null) {
                insertStatement.executeBatch();
            }
        }
    }
}
