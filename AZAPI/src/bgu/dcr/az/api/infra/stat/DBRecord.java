/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.api.infra.stat;

import bgu.dcr.az.utils.ReflectionUtil;
import java.lang.reflect.Field;

/**
 *
 * @author bennyl
 */
public abstract class DBRecord {
    public abstract String provideTableName();

    private Field[] fields;
    public DBRecord() {
        fields = getClass().getDeclaredFields();
        for (Field f : fields){
            f.setAccessible(true);
        }
    }

    public Field[] getFields() {
        return fields;
    }
    
}
