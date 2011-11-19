/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.api.infra.stat;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

/**
 *
 * @author bennyl
 */
public interface Database {

    ResultSet query(String query) throws SQLException;

    List<StatisticRecord> structuredQuery(String query) throws SQLException;

    public static class StatisticRecord {

        static int autoId = 0;
        public final int id;
        public final String roundName;
        public final int roundIndex;
        public final String field;
        public final long val;
        public final int parentId;

        /**
         * use this constractor to load a statistic record
         * @param id
         * @param roundName
         * @param roundIndex
         * @param field
         * @param value
         * @param parentId 
         */
        public StatisticRecord(int id, String roundName, int roundIndex, String field, long value, int parentId) {
            this.id = id;
            this.roundName = roundName;
            this.roundIndex = roundIndex;
            this.field = field;
            this.val = value;
            this.parentId = parentId;
        }

        /**
         * this constractor ment for creation of a new record - it auto generate id for it
         * @param roundName
         * @param roundIndex
         * @param field
         * @param value
         * @param parentId 
         */
        public StatisticRecord(String roundName, int roundIndex, String field, long value, int parentId) {
            this(autoId++, roundName, roundIndex, field, value, parentId);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("statistic: ").append("[id:").append(id).append("][round-name:").append(roundName).append("][round-index:").append(roundIndex).append("][field:").append(field).append("][val:").append(val).append("][parent-id:").append(parentId).append("]");
            return sb.toString();
        }

        public static List<StatisticRecord> process(StatisticRoot root) {
            List<StatisticRecord> records = new LinkedList<StatisticRecord>();
            for (Entry<String, Statistic> s : root.getChildren().entrySet()) {
                process(s.getKey(), s.getValue(), root.getRoundName(), root.getIndexInRound(), -1, records);
            }
            
            return records;
        }

        private static void process(String name, Statistic s, String roundName, int indexInRound, int parentId, List<StatisticRecord> records) {
            final StatisticRecord record = new StatisticRecord(roundName, indexInRound, name, s.getValue(), parentId);
            records.add(record);
            for (Entry<String, Statistic> e : s.getChildren().entrySet()){
                process(e.getKey(), e.getValue(), roundName, indexInRound, record.id, records);
            }
        }

    }
}
