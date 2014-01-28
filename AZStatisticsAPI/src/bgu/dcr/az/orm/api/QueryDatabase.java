package bgu.dcr.az.orm.api;

/**
 * represents a database accessor which allows to query for data from the
 * database
 *
 * @author Benny Lutati
 */
public interface QueryDatabase {

    /**
     * query the database using simple sql format, you can also embedd
     * parameters using the "prepered statement" style which mean that instead
     * of writing:{@code
     *  query("SELECT * FROM TABLE_NAME WHERE ID = 1234 and NAME = \"NAME\" ");
     * }
     * you can write:{@code
     *  query("SELECT * FROM TABLE_NAME WHERE ID = ? and NAME = ?", 1234, "NAME");
     * }
     * the later will result in faster execution and also automatic escaping of
     * problematic characters,
     *
     * @param sql
     * @param parameters
     * @return
     */
    Data query(String sql, Object... parameters);
}
