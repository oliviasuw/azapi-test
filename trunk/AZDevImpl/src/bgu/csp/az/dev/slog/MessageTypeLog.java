/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.dev.slog;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 *
 * @author bennyl
 */
@DatabaseTable(tableName=MessageTypeLog.TABLE_NAME)
public class MessageTypeLog {
    public static final String TABLE_NAME = "MessageTypes";
    public static final String NAME_COLUMN = "NAME";
    public static final String ID_COLUMN = "ID";
    @DatabaseField(id=true, columnName=ID_COLUMN)
    public byte id;
    @DatabaseField(canBeNull=false, columnName=NAME_COLUMN)
    public String name;
}
