/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.orm.api;

/**
 *
 * @author User
 */
public interface RecordAccessor {

    Integer getInt(int columnIndex);

    Long getLong(int columnIndex);

    Double getDouble(int columnIndex);

    Float getFloat(int columnIndex);

    String getString(int columnIndex);

    Byte getByte(int columnIndex);

    Boolean getBoolean(int columnIndex);

    Integer getInt(String columnName);

    Long getLong(String columnName);

    Double getDouble(String columnName);

    Float getFloat(String columnName);

    String getString(String columnName);

    Byte getByte(String columnName);

    Boolean getBoolean(String columnName);

    <T extends Record> T as(Class<T> recordType);

    int numColumns();

    public Object get(int id);

    public Object get(String columnName);
}
