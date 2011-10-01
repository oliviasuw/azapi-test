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
@DatabaseTable(tableName="Result")
public class ResultLog {
    @DatabaseField(dataType= DataType.LONG_STRING)
    String thrownException;
    @DatabaseField()
    String assignmentProduced;
    @DatabaseField()
    int assignmentProducedCost;
    @DatabaseField()
    String goodAssignment;
    @DatabaseField()
    int goodAssignmentCost;
}
