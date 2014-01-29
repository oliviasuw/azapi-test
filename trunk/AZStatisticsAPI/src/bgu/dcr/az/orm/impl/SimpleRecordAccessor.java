/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.orm.impl;

import bgu.dcr.az.orm.api.FieldMetadata;
import bgu.dcr.az.orm.api.Record;
import bgu.dcr.az.orm.api.RecordAccessor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author User
 */
public class SimpleRecordAccessor implements RecordAccessor {

    private final FieldMetadata[] fields;
    private final Object[] record;
    private Map<String, Integer> nameToIndex = null;
    private Class lastAsClass = null;
    private FieldWithUppercaseName[] lasAsClassFields = null;

    public SimpleRecordAccessor(FieldMetadata[] fields, Object[] record) {
        this.fields = fields;
        this.record = record;
    }

    /**
     * SimpleRecordAccessor can cache some information about the usage and types
     * of the record it uses, in this case if you wish that those caches will
     * propagate to a new SimpleRecordAccessor (can help with execution speed)
     * you can use this method in order to construct a new object with the same
     * information cache as this one
     *
     * @param record
     * @return
     */
    public SimpleRecordAccessor newWithSamePrototype(Object[] record) {
        SimpleRecordAccessor result = new SimpleRecordAccessor(fields, record);
        result.nameToIndex = this.nameToIndex;
        result.lasAsClassFields = this.lasAsClassFields;
        result.lastAsClass = this.lastAsClass;

        return result;
    }

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
        if (record[columnIndex] instanceof Number) {
            return ((Number) record[columnIndex]).doubleValue();
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
        try {
            //need to cache those? - see profiling output
            if (recordType != lastAsClass) {
                lastAsClass = recordType;
                final Field[] recordFields = lastAsClass.getFields();
                lasAsClassFields = new FieldWithUppercaseName[recordFields.length];
                for (int i = 0; i < recordFields.length; i++) {
                    lasAsClassFields[i] = new FieldWithUppercaseName(recordFields[i]);
                }
            }

            T result = recordType.newInstance();
            for (FieldWithUppercaseName f : lasAsClassFields) {
                Integer index = findIndexOf(f.upperCaseName);
                if (index != null) {
                    f.f.set(result, record[index]);
                }
            }

            return result;
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new RuntimeException("cannot constract record ", ex);
        }
    }

    @Override
    public int numColumns() {
        return fields.length;
    }

    private Integer findIndexOf(String columnName) {
        if (nameToIndex == null) {
            nameToIndex = new HashMap<>();

            for (int i = 0; i < fields.length; i++) {
                nameToIndex.put(fields[i].name().toUpperCase(), i);
            }
        }

        return nameToIndex.get(columnName);
    }

    private static class FieldWithUppercaseName {

        Field f;
        String upperCaseName;

        public FieldWithUppercaseName(Field f) {
            this.f = f;
            this.f.setAccessible(true);
            this.upperCaseName = f.getName().toUpperCase();
        }

    }

}
