/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.orm.api;

import bgu.dcr.az.orm.api.Data;
import bgu.dcr.az.orm.api.FieldMetadata;
import bgu.dcr.az.orm.impl.FieldMetadataImpl;
import bgu.dcr.az.orm.impl.ObjectArrayRecord;
import bgu.dcr.az.orm.impl.SimpleData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 *
 * @author User
 */
public class DataUtils {

    public static Data fromMapEntries(Collection<Map.Entry> entries, Class keyType, String keyName, Class valueType, String valueName) {
        ArrayList<ObjectArrayRecord> resultset = new ArrayList<>(entries.size());
        entries.forEach(e -> resultset.add(new ObjectArrayRecord(e.getKey(), e.getValue())));

        return new SimpleData(resultset, new FieldMetadata[]{
            new FieldMetadataImpl(keyName, keyType),
            new FieldMetadataImpl(valueName, valueType)
        });

    }

    public static Data fromMap(Map map, Class keyType, String keyName, Class valueType, String valueName) {
        return fromMapEntries(map.entrySet(), keyType, keyName, valueType, valueName);
    }

    public static Data fromResultSet(ResultSet results) throws SQLException {

        FieldMetadata[] fields = FieldMetadataImpl.fromResultSet(results);
        ArrayList<ObjectArrayRecord> records = new ArrayList<>();

        while (results.next()) {
            Object[] data = new Object[fields.length];
            for (int i = 0; i < fields.length; i++) {
                data[i] = results.getObject(i + 1);
            }

            records.add(new ObjectArrayRecord(data));
        }

        return new SimpleData(records, fields);
    }
    
}
