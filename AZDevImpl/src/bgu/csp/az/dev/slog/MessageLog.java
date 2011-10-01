/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.dev.slog;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 *
 * @author bennyl
 */
@DatabaseTable(tableName = MessageLog.TABLE_NAME)
public class MessageLog {

    public static final String TABLE_NAME = "Messages";
    public static final String SCENARIO_PART_SENT_IN_ID_FIELD = "SCENARIO_PART_SENT_IN_ID";
    public static final String ID_FIELD = "ID";
    public static final String SENDER_FIELD = "SENDER";
    public static final String RECEIVER_FIELD = "RECEIVER";
    public static final String MESSAGE_TYPE_ID_FIELD = "MESSAGE_TYPE_ID";
    
    @DatabaseField(canBeNull = true, columnName=SCENARIO_PART_SENT_IN_ID_FIELD)
    public long scenerioPartSentInId;
    @DatabaseField(canBeNull = false, columnName=MESSAGE_TYPE_ID_FIELD)
    public byte messageTypeId;
    @DatabaseField(id = true, columnName=ID_FIELD)
    public long id;
    @DatabaseField(canBeNull = false, columnName=SENDER_FIELD)
    public int sender;
    @DatabaseField(canBeNull = false, columnName=RECEIVER_FIELD)
    public int receiver;
}
