/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.api.infra.stat;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author bennyl
 */
public interface Database {

    ResultSet query(String query) throws SQLException;
    List<StatisticRecord> structuredQuery(String query) throws SQLException;
    
    public static class StatisticRecord {

        public final int id;
        public final String roundName;
        public final int roundIndex;
        public final String field;
        public final long val;
        public final int parentId;

        public StatisticRecord(int id, String roundName, int roundIndex, String field, long value, int parentId) {
            this.id = id;
            this.roundName = roundName;
            this.roundIndex = roundIndex;
            this.field = field;
            this.val = value;
            this.parentId = parentId;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("statistic: ").append("[id:").append(id).append("][round-name:").append(roundName).append("][round-index:").append(roundIndex).append("][field:").append(field).append("][val:").append(val).append("][parent-id:").append(parentId).append("]");
            return sb.toString();
        }
        
    }
}
