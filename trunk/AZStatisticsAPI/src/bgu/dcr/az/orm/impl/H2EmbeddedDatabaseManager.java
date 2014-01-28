/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.orm.impl;

import bgu.dcr.az.orm.api.Data;
import bgu.dcr.az.orm.api.EmbeddedDatabaseManager;
import bgu.dcr.az.orm.api.QueryDatabase;
import bgu.dcr.az.orm.api.Record;
import bgu.dcr.az.orm.api.TableMetadata;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.Map;

/**
 *
 * @author User
 */
public class H2EmbeddedDatabaseManager implements EmbeddedDatabaseManager {

    Connection connection;
    
    @Override
    public void start(File databasePath, boolean append) {
        if (!append){
            
        }
    }

    @Override
    public void defineTable(String name, Class<? extends Record> recordType) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Data query(String sql, Object[] parameters) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void insert(Object o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Map<String, TableMetadata> tables() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void dumpCSV(File folder) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public QueryDatabase createQueryDatabase() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public QueryDatabase createDefinitionDatabase() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void close() throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    void onWriterThreadTermination() {

    }
    
    void onWriterThreadFailed(Exception ex){
        
    }

    void onWriterThreadCallback() {

    }

    Connection getConnection(){
        return null;
    }
    
}
