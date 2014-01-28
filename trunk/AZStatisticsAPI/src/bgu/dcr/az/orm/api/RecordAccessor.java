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

    int getInt(int columnIndex);

    long getLong(int columnIndex);

    double getDouble(int columnIndex);

    float getFloat(int columnIndex);

    char getCharacter(int columnIndex);

    String getString(int columnIndex);

    byte getByte(int columnIndex);

    boolean getBoolean(int columnIndex);

    short getShort(int columnIndex);

    int getInt(String columnName);

    long getLong(String columnName);

    double getDouble(String columnName);

    float getFloat(String columnName);

    char getCharacter(String columnName);

    String getString(String columnName);

    byte getByte(String columnName);

    boolean getBoolean(String columnName);

    short getShort(String columnName);

    <T extends Record> T as(Class<T> recordType);
}
