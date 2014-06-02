/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.execs.orm;

import bgu.dcr.az.common.reflections.ReflectionUtils;
import bgu.dcr.az.execs.orm.api.DBRecord;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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
    Map<Object, RecordManipulator> recordManipulators = new IdentityHashMap<>(); // accessed from this thread only
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
                    } else if (item instanceof ExecuteCommand) {
                        execute((ExecuteCommand) item);
                    } else {
                        addToPreparedStatementBatch(item);
                    }
                }

                commitBatch();
            }
        } catch (InterruptedException | SQLException ex) {
            ex.printStackTrace();
            try {
                terminate();
            } catch (SQLException ex1) {
                Logger.getLogger(H2EmbeddedDatabaseWriter.class.getName()).log(Level.SEVERE, null, ex1);
            } finally {
                manager.onWriterThreadFailed(ex);
            }
        }
    }

    private void execute(ExecuteCommand cmd) throws SQLException {
        if (cmd.parameters == null || cmd.parameters.length == 0) {
            try (Statement st = manager.getConnection().createStatement()) {
//                System.out.println("executing: \n" + cmd.sql);
                st.execute(cmd.sql);
            }
        } else {
            try (PreparedStatement st = manager.getConnection().prepareStatement(cmd.sql)) {
                for (int i = 0; i < cmd.parameters.length; i++) {
                    st.setObject(i + 1, cmd.parameters[i]);
                }
//                System.out.println("executing: \n" + st.toString());
                st.execute();
            }
        }
    }

    private void defineTable(DefineTableCommand cmd) throws SQLException {
        if (!recordManipulators.containsKey(cmd.recordDes.identifier())) {
            RecordManipulator manipulator = new RecordManipulator(cmd.tableName, cmd.recordDes, manager.getConnection());
            recordManipulators.put(cmd.recordDes.identifier(), manipulator);

            manipulator.defineTable(manager.getConnection());
        }
    }

    private void addToPreparedStatementBatch(Object item) throws SQLException {
        RecordManipulator manipulator;

        if (item instanceof ObjectWithIdentifier) {
            ObjectWithIdentifier owi = (ObjectWithIdentifier) item;
            manipulator = recordManipulators.get(owi.ident);
            item = owi.o;
        } else {
            manipulator = recordManipulators.get(item.getClass());
        }

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

    void appendDefineTableCommand(String name, RecordDescriptor recordDes) {
        q.add(new DefineTableCommand(name, recordDes));
    }

    void appendDefineTableCommand(String name, Class recordDes) {
        q.add(new DefineTableCommand(name, new ObjectRecordDescriptor(recordDes)));
    }

    void appendExecuteUpdateCommand(String sql, Object... parameters) {
        q.add(new ExecuteCommand(sql, parameters));
    }

    void synchronize() throws InterruptedException {
        System.out.println("Sync Against last version of Database");
        q.add(CALLBACK_SIGNAL);
        synchronizationLock.acquire();
    }

    void appendInsertionCommand(Object o) {
        q.add(o);
    }

    private static class ObjectWithIdentifier {

        Object o;
        Object ident;

        public ObjectWithIdentifier(Object o, Object ident) {
            this.o = o;
            this.ident = ident;
        }

    }

    void appendInsertionCommand(Object o, Object identifier) {
        q.add(new ObjectWithIdentifier(o, identifier));
    }

    private static class ExecuteCommand {

        String sql;
        Object[] parameters;

        public ExecuteCommand(String sql, Object[] parameters) {
            this.sql = sql;
            this.parameters = parameters;
        }
    }

    private static class DefineTableCommand {

        String tableName;
        RecordDescriptor recordDes;

        public DefineTableCommand(String tableName, RecordDescriptor recordDes) {
            this.tableName = tableName;
            this.recordDes = recordDes;
        }

    }

    private static class ObjectRecordDescriptor implements RecordDescriptor {

        Class c;
        String[] fieldsNames;
        Field[] fields;

        public ObjectRecordDescriptor(Class c) {
            this.c = c;
            List<Field> allFields = ReflectionUtils.allFields(c);
            fields = allFields.stream().map(f -> {
                f.setAccessible(true);
                return f;
            }).toArray(Field[]::new);
            fieldsNames = allFields.stream().map(f -> f.getName()).toArray(String[]::new);
        }

        @Override
        public String[] fields() {
            return fieldsNames;
        }

        @Override
        public Object get(int idx, Object from) {
            try {
                return fields[idx].get(from);
            } catch (IllegalArgumentException | IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }
        }

        @Override
        public Class type(int idx) {
            return fields[idx].getType();
        }

        @Override
        public Object identifier() {
            return c;
        }

    }

    private static class RecordManipulator {

        //List<Field> fields;
        PreparedStatement insertStatement;
        String tableName;
        //Class recordClass;
        private Connection connection;
        private final RecordDescriptor record;

        public RecordManipulator(String tableName, RecordDescriptor record, Connection connection) throws SQLException {
            //fields = ReflectionUtils.allFields(recordClass);
            this.record = record;
            this.tableName = tableName;
            //this.recordClass = recordClass;
            this.connection = connection;

        }

        private void createInsertStatement() throws SQLException, SecurityException {
            StringBuilder sb = new StringBuilder("INSERT INTO ").append(tableName).append(" (");
            String[] fields = record.fields();
            for (String f : fields) {
                //f.setAccessible(true);
                sb.append(f).append(",");
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
            String[] fields = record.fields();
            for (int i = 0; i < fields.length; i++) {

                exe.append(", ").append(fields[i]);
                Class type = record.type(i);
                if (Boolean.class == type || boolean.class == type) {
                    exe.append(" BOOLEAN");
                } else if (Double.class == type || double.class == type) {
                    exe.append(" DECIMAL(20, 3)");
                } else if (Float.class == type || float.class == type) {
                    exe.append(" DECIMAL(14, 3)");
                } else if (Integer.class == type || int.class == type) {
                    exe.append(" INTEGER");
                } else if (Character.class == type || char.class == type) {
                    exe.append(" CHAR");
                } else if (String.class == type) {
                    exe.append(" VARCHAR(150)");
                } else if (Long.class == type || long.class == type) {
                    exe.append(" BIGINT");
                } else {
                    throw new SQLException("Cannot generate table " + tableName + ", field " + fields[i] + " is not primitive.");
                }
            }
            exe.append(", PRIMARY KEY (ID));");

            try (Statement st = connection.createStatement()) {
//                System.out.println("creating table: \n" + exe.toString());
                st.executeUpdate(exe.toString());
            }
        }

        private void insert(Object item) throws SQLException {
            if (insertStatement == null) {
                createInsertStatement();
            }

            String[] fields = record.fields();
            for (int i = 0; i < fields.length; i++) {
                insertStatement.setObject(i + 1, record.get(i, item));
            }

            insertStatement.addBatch();
        }

        void commit() throws SQLException {
            if (insertStatement != null) {
                insertStatement.executeBatch();
            }
        }
    }
}
