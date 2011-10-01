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
@DatabaseTable(tableName="ProblemConstraints")
public class ProblemConstraintsLog {
    @DatabaseField(generatedId=true)
    public int id;
    @DatabaseField(canBeNull=false)
    public int var1;
    @DatabaseField(canBeNull=false)
    public int val1;
    @DatabaseField(canBeNull=false)
    public int var2;
    @DatabaseField(canBeNull=false)
    public int val2;
    @DatabaseField(canBeNull=false)
    public int cost;
}
