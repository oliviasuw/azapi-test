/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author bennyl
 */
public class DataBaseTest {

    /**
     * 
     * @param query
     * @param fetchSize - gives the JDBC driver a hint as to the number of rows 
     * 						 that should be fetched from the database when more rows are needed.
     * @return
     */
    public static int runUpdate(String query, Connection conn) throws SQLException {
        Statement stmt = null;
        int rs = -1984;
        stmt = conn.createStatement();
        rs = stmt.executeUpdate(query);

        return rs;
    }

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        Class.forName("org.h2.Driver");
        Connection conn = DriverManager.getConnection("jdbc:h2:agentzero1", "sa", "");
        String createTable = "CREATE TABLE STATISTICS (ID INTEGER NOT NULL AUTO_INCREMENT, ROUNDNAME VARCHAR(50), ROUNDINDEX INTEGER NOT NULL, FIELD VARCHAR(50) NOT NULL, \"VALUE\" INTEGER NOT NULL, PARENTID INTEGER NOT NULL, PRIMARY KEY (ID));";
        runUpdate(createTable, conn);
        // add application code here
        conn.close();
    }
}
