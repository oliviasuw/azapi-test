/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.orm.impl;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
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
                        manager.onWriterThreadTermination();
                        return;
                    } else if (item == CALLBACK_SIGNAL) {
                        manager.onWriterThreadCallback();
                    } else if (item instanceof DefineTableCommand) {
                        defineTable((DefineTableCommand) item);
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

    private void defineTable(DefineTableCommand cmd) throws SQLException {
        RecordManipulator manipulator = new RecordManipulator(cmd.tableName, cmd.recordType, manager.getConnection());
        recordManipulators.put(cmd.recordType, manipulator);

        manipulator.defineTable(manager.getConnection());
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

    private static class DefineTableCommand {

        String tableName;
        Class recordType;

        public DefineTableCommand(String tableName, Class recordType) {
            this.tableName = tableName;
            this.recordType = recordType;
        }

    }

    private static class RecordManipulator {

        Field[] fields;
        PreparedStatement insertStatement;
        String tableName;
        Class recordClass;

        public RecordManipulator(String tableName, Class recordClass, Connection connection) throws SQLException {
            fields = recordClass.getFields();
            this.tableName = tableName;
            this.recordClass = recordClass;

            StringBuilder sb = new StringBuilder("INSERT INTO ").append(tableName).append(" (");
            for (Field f : fields) {
                f.setAccessible(true);
                sb.append(f.getName()).append(",");
            }

            sb.deleteCharAt(sb.length() - 1).append(") VALUES (");
            for (int i = 0; i < fields.length; i++) {
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
                st.executeUpdate(exe.toString());
            }
        }

        private void insert(Object item) throws SQLException {
            try {
                for (int i = 0; i < fields.length; i++) {
                    insertStatement.setObject(i, fields[i].get(item));
                }

                insertStatement.addBatch();
            } catch (IllegalArgumentException | IllegalAccessException ex) {
                throw new SQLException("cannot insert object " + item);
            }
        }

        void commit() throws SQLException {
            insertStatement.executeBatch();
        }
    }
}
