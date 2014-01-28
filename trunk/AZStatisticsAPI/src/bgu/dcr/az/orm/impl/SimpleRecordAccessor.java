/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.orm.impl;

import bgu.dcr.az.orm.api.FieldMetadata;
import bgu.dcr.az.orm.api.Record;
import bgu.dcr.az.orm.api.RecordAccessor;
import java.util.Map;

/**
 *
 * @author User
 */
public class SimpleRecordAccessor implements RecordAccessor {

    FieldMetadata[] fields;
    Object[] record;
    Map<String, Integer> nameToIndex;

    @Override
    public Integer getInt(int columnIndex) {
        if (record[columnIndex] instanceof Integer) {
            return (Integer) record[columnIndex];
        }
        return Integer.valueOf("" + record[columnIndex]);
    }

    @Override
    public Long getLong(int columnIndex) {
        if (record[columnIndex] instanceof Long) {
            return (Long) record[columnIndex];
        }
        return Long.valueOf("" + record[columnIndex]);
    }

    @Override
    public Double getDouble(int columnIndex) {
        if (record[columnIndex] instanceof Double) {
            return (Double) record[columnIndex];
        }
        return Double.valueOf("" + record[columnIndex]);
    }

    @Override
    public Float getFloat(int columnIndex) {
        if (record[columnIndex] instanceof Float) {
            return (Float) record[columnIndex];
        }
        return Float.valueOf("" + record[columnIndex]);
    }

    @Override
    public String getString(int columnIndex) {
        if (record[columnIndex] instanceof String) {
            return (String) record[columnIndex];
        }
        return "" + record[columnIndex];
    }

    @Override
    public Byte getByte(int columnIndex) {
        if (record[columnIndex] instanceof Byte) {
            return (Byte) record[columnIndex];
        }
        return Byte.valueOf("" + record[columnIndex]);
    }

    @Override
    public Boolean getBoolean(int columnIndex) {
        if (record[columnIndex] instanceof Boolean) {
            return (Boolean) record[columnIndex];
        }
        return Boolean.valueOf("" + record[columnIndex]);
    }

    @Override
    public Integer getInt(String columnName) {
        return getInt(findIndexOf(columnName));
    }

    @Override
    public Long getLong(String columnName) {
        return getLong(findIndexOf(columnName));
    }

    @Override
    public Double getDouble(String columnName) {
        return getDouble(findIndexOf(columnName));
    }

    @Override
    public Float getFloat(String columnName) {
        return getFloat(findIndexOf(columnName));
    }

    @Override
    public String getString(String columnName) {
        return getString(findIndexOf(columnName));
    }

    @Override
    public Byte getByte(String columnName) {
        return getByte(findIndexOf(columnName));
    }

    @Override
    public Boolean getBoolean(String columnName) {
        return getBoolean(findIndexOf(columnName));
    }

    @Override
    public <T extends Record> T as(Class<T> recordType) {
        throw new UnsupportedOperationException("Not implemented yet.");
//        
//        try {
//            //need to cache those...
//            T result = recordType.newInstance();
//            for (Field f : recordType.getFields()){
//                f.setAccessible(true);
//                Integer index = findIndexOf(f.getName().toUpperCase());
//            }
//        } catch (InstantiationException | IllegalAccessException ex) {
//            throw new RuntimeException("cannot constract record ", ex);
//        }
    }

    @Override
    public int numColumns() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private int findIndexOf(String columnName) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
