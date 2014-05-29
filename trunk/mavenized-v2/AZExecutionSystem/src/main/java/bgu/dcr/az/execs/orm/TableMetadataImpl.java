/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.execs.orm;

import bgu.dcr.az.execs.orm.api.FieldMetadata;
import bgu.dcr.az.execs.orm.api.TableMetadata;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 *
 * @author User
 */
public class TableMetadataImpl implements TableMetadata {

    private final String name;
    private FieldMetadata[] fields;

    private TableMetadataImpl(String name, FieldMetadata[] fields) {
        this.name = name;
        this.fields = fields;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public FieldMetadata[] fields() {
        return fields;
    }

    public static Map<String, TableMetadata> from(DatabaseMetaData md) throws SQLException {
        Map<String, TableMetadata> result = new HashMap<>();

        try (ResultSet tables = md.getTables(null, "PUBLIC", null, null)) {
            while (tables.next()) {
                String name = tables.getString("TABLE_NAME");
                
                LinkedList<FieldMetadata> fieldMetadataList = new LinkedList<>();
                TableMetadataImpl table = new TableMetadataImpl(name, null);
                try (ResultSet fields = md.getColumns(null, "PUBLIC", name, null)) {
                    while (fields.next()) {
                        int type = fields.getInt("DATA_TYPE");
                        String columnName = fields.getString("COLUMN_NAME");

                        fieldMetadataList.add(FieldMetadataImpl.fromSQLData(columnName, type));
                    }
                }

                table.fields = fieldMetadataList.toArray(new FieldMetadata[fieldMetadataList.size()]);
                result.put(name, table);
            }
        }

        return result;
    }

    @Override
    public String toString() {
        return name;
    }

}
