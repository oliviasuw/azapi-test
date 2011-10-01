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

@DatabaseTable(tableName=AgentLogLog.TABLE_NAME)
public class AgentLogLog {
    public static final String TABLE_NAME = "Logs";
    public static final String ID_COLUMN = "ID";
    public static final String DATA_COLUMN = "DATA";
    public static final String SCENARIO_PART_ID_COLUMN = "SCENARIO_PART_ID";
    
    @DatabaseField(id=true, columnName=ID_COLUMN)
    long id;
    @DatabaseField(canBeNull=false, columnName=DATA_COLUMN)
    public String data;
    @DatabaseField(canBeNull=false, columnName=SCENARIO_PART_ID_COLUMN)
    public long scenarioPartId;
}
