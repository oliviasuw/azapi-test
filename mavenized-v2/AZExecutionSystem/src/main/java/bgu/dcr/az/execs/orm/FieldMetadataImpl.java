/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.execs.orm;

import bgu.dcr.az.execs.orm.api.FieldMetadata;
import bgu.dcr.az.execs.orm.api.TableMetadata;
import bgu.dcr.az.execs.util.SQLUtils;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 *
 * @author User
 */
public class FieldMetadataImpl implements FieldMetadata {

    private String name;
    private Class type;

    public FieldMetadataImpl(String name, Class type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String toString() {
        return name + ": " + type.getSimpleName();
    }
    
    @Override
    public Class type() {
        return type;
    }

    public static FieldMetadataImpl fromSQLData(String name, int dataType) {
        return new FieldMetadataImpl(name, SQLUtils.sqlTypeToClass(dataType));
    }

    public static FieldMetadataImpl[] fromResultSet(ResultSet rs) throws SQLException {
        ResultSetMetaData metadata = rs.getMetaData();

        FieldMetadataImpl[] results = new FieldMetadataImpl[metadata.getColumnCount()];
        for (int i = 0; i < results.length; i++) {
            results[i] = fromSQLData(metadata.getColumnLabel(i + 1), metadata.getColumnType(i + 1));
        }

        return results;
    }

}
