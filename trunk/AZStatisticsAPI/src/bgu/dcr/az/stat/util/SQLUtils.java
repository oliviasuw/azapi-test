/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.stat.util;

import java.sql.Types;

/**
 *
 * @author User
 */
public class SQLUtils {

    public static Class sqlTypeToClass(int sqlType) {
        Class result = Object.class;

        switch (sqlType) {
            case Types.CHAR:
            case Types.VARCHAR:
            case Types.LONGVARCHAR:
                result = String.class;
                break;

            case Types.NUMERIC:
            case Types.DECIMAL:
                result = java.math.BigDecimal.class;
                break;

            case Types.BIT:
                result = java.lang.Boolean.class;
                break;

            case Types.TINYINT:
                result = java.lang.Byte.class;
                break;

            case Types.SMALLINT:
                result = java.lang.Short.class;
                break;

            case Types.INTEGER:
                result = java.lang.Integer.class;
                break;

            case Types.BIGINT:
                result = java.lang.Long.class;
                break;

            case Types.REAL:
                result = java.lang.Float.class;
                break;

            case Types.FLOAT:
            case Types.DOUBLE:
                result = java.lang.Double.class;
                break;

            case Types.BINARY:
            case Types.VARBINARY:
            case Types.LONGVARBINARY:
                result = java.lang.Byte[].class;
                break;

            case Types.DATE:
                result = java.sql.Date.class;
                break;

            case Types.TIME:
                result = java.sql.Time.class;
                break;

            case Types.TIMESTAMP:
                result = java.sql.Timestamp.class;
                break;
        }

        return result;
    }
}
