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
@DatabaseTable(tableName=MessageArgumentLog.TABLE_NAME)
public class MessageArgumentLog {
    
    public static final String TABLE_NAME = "MessageArguments";
    public static final String ID_COLUMN = "ID";
    public static final String MESSAGE_ID_COLUMN = "MESSAGE_ID";
    public static final String INDEX_COLUMN = "INDEX";
    public static final String ARGUMENT_STRING_COLUMN = "ARGUMENT_STRING";
    public static final String TYPE_COLUMN = "TYPE";
    
    
    @DatabaseField(generatedId=true, columnName=ID_COLUMN)
    public int id;
    @DatabaseField(canBeNull=false, columnName=MESSAGE_ID_COLUMN)
    public long messageId;
    @DatabaseField(canBeNull=false, columnName=INDEX_COLUMN)
    public byte index;
    @DatabaseField(canBeNull=false, columnName=ARGUMENT_STRING_COLUMN)
    public String argumentString;
    @DatabaseField(canBeNull=false, columnName=TYPE_COLUMN)
    public String type;

    @Override
    public String toString() {
        return "#" + index + " " + type + ": " + argumentString;
    }
    
    
}
