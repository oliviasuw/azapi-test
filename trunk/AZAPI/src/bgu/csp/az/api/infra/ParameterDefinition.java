/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.api.infra;

/**
 *
 * @author bennyl
 */
public class ParameterDefinition {
    private Class type;
    private Object defaultValue;

    public Object getDefaultValue() {
        return defaultValue;
    }

    public Class getType() {
        return type;
    }

    public ParameterDefinition(Class type, Object defaultValue) {
        this.type = type;
        this.defaultValue = defaultValue;
    }
    
    
}
