/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.orm.api;

/**
 * represents a database accessor that is able to define new tables
 *
 * @author User
 */
public interface DefinitionDatabase {

    /**
     * define a new table in the database, use the given record type as its
     * fields
     *
     * @param tableName
     * @param recordType
     */
    void defineTable(String tableName, Class<? extends DBRecord> recordType);
}
