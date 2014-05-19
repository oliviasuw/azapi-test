/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.orm.api;

import bgu.dcr.az.execs.api.experiments.ExecutionService;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

/**
 *
 * @author User
 */
public interface EmbeddedDatabaseManager extends Closeable, ExecutionService {

    /**
     * start the manager - initiate a connection to the database start any
     * writer threads etc.
     *
     * @param databasePath the path to the database
     * @param append if this is set to false then any existing data that may be
     * already contained in the database will be deleted
     */
    void start(File databasePath, boolean append) throws SQLException;

    /**
     * define a new table with the given name, if this table already exists in
     * the database then nothing bad happen :)
     *
     * @param name the table name
     * @param recordType the record type that will be stored in the table
     */
    void defineTable(String name, Class<? extends DBRecord> recordType);

    /**
     * query for data in this database
     *
     * @param sql
     * @param parameters
     * @see QueryDatabase#query(java.lang.String, java.lang.Object...)
     * @return
     */
    Data query(String sql, Object[] parameters) throws SQLException;

    
    /**
     * executes an update query in this database
     * 
     * @param sql
     * @param parameters
     * @throws SQLException 
     */
    void executeUpdate(String sql, Object ... parameters);
    
    /**
     * insert a new record into the database
     *
     * @param o
     */
    void insert(Object o);

    /**
     * get tables metadata which is a map {@code tableName -> tableMetadata}
     *
     * @return
     * @throws java.sql.SQLException
     */
    Map<String, TableMetadata> tables() throws SQLException;

    /**
     * dump the database content into a set of csv files and store them in the
     * given folder
     *
     * @param folder
     * @throws java.sql.SQLException
     * @throws java.io.IOException
     */
    void dumpCSV(File folder) throws SQLException, IOException;

    /**
     * create and return a new object that can be used in order to query the
     * database that is managed by this manager
     *
     * @return
     */
    QueryDatabase createQueryDatabase();

    /**
     * create and return a new object that can be used in order to define new
     * tables in the database that is managed by this manager
     *
     * @return
     */
    DefinitionDatabase createDefinitionDatabase();

}
