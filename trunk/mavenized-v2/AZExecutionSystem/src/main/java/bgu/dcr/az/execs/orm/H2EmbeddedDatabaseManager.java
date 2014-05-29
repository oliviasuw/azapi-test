/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.execs.orm;

import bgu.dcr.az.execs.orm.api.DataUtils;
import bgu.dcr.az.execs.orm.api.Data;
import bgu.dcr.az.execs.orm.api.DefinitionDatabase;
import bgu.dcr.az.execs.orm.api.EmbeddedDatabaseManager;
import bgu.dcr.az.execs.orm.api.QueryDatabase;
import bgu.dcr.az.execs.orm.api.DBRecord;
import bgu.dcr.az.execs.orm.api.TableMetadata;
import bgu.dcr.az.execs.util.PreparedStatementLRUCache;
import bgu.dcr.az.common.exceptions.UncheckedSQLException;
import bgu.dcr.az.conf.modules.ModuleContainer;
import bgu.dcr.az.execs.exceptions.InitializationException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.h2.tools.Csv;

/**
 *
 * @author User
 */
public class H2EmbeddedDatabaseManager implements EmbeddedDatabaseManager {

    public static String DATA_BASE_NAME = "agentzero";
    private static final int MAXIMUM_CACHE_SIZE = 5;

    private Connection connection;
    private H2EmbeddedDatabaseWriter writer = null;
    private PreparedStatementLRUCache cachedQueries;

    @Override
    public void initialize(ModuleContainer mc) {
        if (connection != null) {
            throw new InitializationException("attempting to initialize already initialized database");
        }

        try {
            start(new File(DATA_BASE_NAME), false);
        } catch (SQLException ex1) {
            throw new InitializationException("cannot initialize database, see cause", ex1);
        }
    }

    @Override
    public void start(File databasePath, boolean append) throws SQLException {
        final String databaseAbsolutePath = databasePath.getAbsolutePath();
        if (!append) {
            for (File f : databasePath.getAbsoluteFile().getParentFile().listFiles()) {
                if (f.getName().startsWith(databasePath.getName() + ".")) {
                    f.delete();
                }
            }
        }

        try {
            String options = "LOG=0;CACHE_SIZE=65536;LOCK_MODE=0;UNDO_LOG=0";
            Class.forName("org.h2.Driver");
            connection = DriverManager.
                    getConnection("jdbc:h2:" + databaseAbsolutePath + ";" + options, "sa", "");
            connection.setAutoCommit(false);
        } catch (ClassNotFoundException | SQLException ex) {
            throw new SQLException("cannot connect to statistics database", ex);
        }

        if (writer != null) {
            try {
                terminateWriter();
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                Logger.getLogger(H2EmbeddedDatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        writer = new H2EmbeddedDatabaseWriter(this);

        Thread t = new Thread(writer);
        t.setName("Statistics Database Writer");
        t.start();

        cachedQueries = new PreparedStatementLRUCache(MAXIMUM_CACHE_SIZE, connection);
    }

    @Override
    public void defineTable(String name, Class<? extends DBRecord> recordType) {
        writer.appendDefineTableCommand(name, recordType);
    }

    @Override
    public Data query(String sql, Object[] parameters) throws SQLException {
        try {
            writer.synchronize();
            if (parameters == null) {
                try (Statement sts = connection.createStatement();
                        ResultSet results = sts.executeQuery(sql)) {
                    return DataUtils.fromResultSet(results);
                }
            } else {
                PreparedStatement statement = prepere(sql, parameters);
                try (ResultSet results = statement.executeQuery()) {
                    return DataUtils.fromResultSet(results);
                }
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new SQLException("SQL execution interrupted", ex);
        }
    }

    @Override
    public void execute(String sql, Object... parameters) {
        writer.appendExecuteUpdateCommand(sql, parameters);
    }

    @Override
    public void insert(Object o) {
        writer.appendInsertionCommand(o);
    }

    @Override
    public Map<String, TableMetadata> tables() throws SQLException {
        DatabaseMetaData md = connection.getMetaData();
        return TableMetadataImpl.from(md);
    }

    @Override
    public void dumpCSV(File folder) throws SQLException, IOException {
        if (!folder.exists()) {
            folder.mkdirs();
        }

        for (TableMetadata t : tables().values()) {
            System.out.println("writing table " + t.name());
            try (Statement sts = connection.createStatement();
                    ResultSet rs = sts.executeQuery("select * from " + t.name())) {
                File ff = new File(folder.getAbsolutePath() + "/" + t.name() + ".csv");
                ff.createNewFile();
                try (FileWriter fw = new FileWriter(ff)) {
                    new Csv().write(fw, rs);
                }
            }
        }

    }

    @Override
    public QueryDatabase createQueryDatabase() {
        return new QDB();
    }

    @Override
    public DefinitionDatabase createDefinitionDatabase() {
        return new DDB();
    }

    @Override
    public void close() throws IOException {
        try {
            try {
                terminateWriter();
            } catch (InterruptedException ex) {
                Logger.getLogger(H2EmbeddedDatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
                Thread.currentThread().interrupt();
            }
            closeCachedStoredProcedures();
            connection.close();
        } catch (SQLException ex) {
            throw new IOException("cannot close database", ex);
        }
    }

    Connection getConnection() {
        return connection;
    }

    void onWriterThreadFailed(Exception ex) {
        Logger.getLogger(H2EmbeddedDatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
    }

    private void terminateWriter() throws InterruptedException {
        writer.close();
    }

    private PreparedStatement prepere(String sql, Object[] parameters) throws SQLException {
        PreparedStatement ps = cachedQueries.retreive(sql);

        for (int i = 0; i < parameters.length; i++) {
            ps.setObject(i + 1, parameters[i]);
        }

        return ps;
    }

    private void closeCachedStoredProcedures() {
        cachedQueries.clear();
    }

    @Override
    public void defineTable(String name, RecordDescriptor recordDes) {
        writer.appendDefineTableCommand(name, recordDes);
    }

    @Override
    public void insert(Object o, Object recordIdentifier) {
        writer.appendInsertionCommand(o, recordIdentifier);
    }

    private class DDB implements DefinitionDatabase {

        @Override
        public void defineTable(String tableName, Class recordType) {
            H2EmbeddedDatabaseManager.this.defineTable(tableName, recordType);
        }

    }

    private class QDB implements QueryDatabase {

        @Override
        public Data query(String sql, Object... parameters) {
            try {
                return H2EmbeddedDatabaseManager.this.query(sql, parameters);
            } catch (SQLException ex) {
                throw new UncheckedSQLException("see cause", ex);
            }
        }

    }

}
