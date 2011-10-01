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
@DatabaseTable(tableName=ScenarioPartLog.TABLE_NAME)
public class ScenarioPartLog {
    public static final String TABLE_NAME = "ScenarioParts";
    public static final String CURRENT_CC_COLUMN = "CURRENT_CC";
    public static final String MESSAGE_ID_COLUMN = "MESSAGE_ID";
    
    @DatabaseField(id=true, columnName=MESSAGE_ID_COLUMN)
    public long messageId;
    @DatabaseField(canBeNull=false, columnName=CURRENT_CC_COLUMN)
    public long currentCC; //used to order scenario parts;
    
}
