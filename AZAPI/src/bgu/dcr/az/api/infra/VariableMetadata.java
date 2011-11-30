/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.api.infra;

import bgu.dcr.az.api.ano.Variable;
import bgu.dcr.az.api.exp.InternalErrorException;
import bgu.dcr.az.utils.ReflectionUtil;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bennyl
 */
public class VariableMetadata {

    private String name;
    private String description;
    private Object defaultValue;
    private Class type;

    public VariableMetadata(String name, String description, Object defaultValue, Class type) {
        this.name = name;
        this.description = description;
        this.defaultValue = defaultValue;
        this.type = type;
    }

    public Object getCurrentValue() {
        return defaultValue;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public Class getType() {
        return type;
    }

    public static void assign(Object obj, Map<String, Object> variables) {
        try {
            for (Field field : ReflectionUtil.getRecursivelyFieldsWithAnnotation(obj.getClass(), Variable.class)) {
                final Object val = variables.get(field.getAnnotation(Variable.class).name());
                if (val != null) {
                    field.set(obj, val);
                }
            }
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(VariableMetadata.class.getName()).log(Level.SEVERE, null, ex);
            throw new InternalErrorException();
        } catch (IllegalAccessException ex) {
            Logger.getLogger(VariableMetadata.class.getName()).log(Level.SEVERE, null, ex);
            throw new InternalErrorException();
        }

    }

    public static Map<String, VariableMetadata> map(Object from) {
        VariableMetadata[] s = scan(from);
        HashMap<String, VariableMetadata> ret = new HashMap<String, VariableMetadata>();
        for (VariableMetadata v : s) {
            ret.put(v.getName(), v);
        }

        return ret;
    }

    public static VariableMetadata[] scan(Object from) {
//        System.out.println("Scanning " + from.getClass().getSimpleName());
        List<Field> fields = ReflectionUtil.getRecursivelyFieldsWithAnnotation(from.getClass(), Variable.class);
        VariableMetadata[] ret = new VariableMetadata[fields.size()];
        int i = 0;

        try {
            for (Field field : fields) {
                Object defaultValue = field.get(from);
                Variable v = field.getAnnotation(Variable.class);
                Class type = field.getType();

                VariableMetadata metadata = new VariableMetadata(v.name(), v.description(), defaultValue, type);
                ret[i++] = metadata;
            }

            return ret;
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(VariableMetadata.class.getName()).log(Level.SEVERE, null, ex);
            throw new InternalErrorException("problem reading variables of class: " + from.getClass().getSimpleName(), ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(VariableMetadata.class.getName()).log(Level.SEVERE, null, ex);
            throw new InternalErrorException("problem reading variables of class: " + from.getClass().getSimpleName(), ex);

        }
    }
}
