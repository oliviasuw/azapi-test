/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bc.x.model;

import bc.dsl.JavaDSL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nu.xom.Element;

import static bc.dsl.XNavDSL.*;
import static bc.dsl.JavaDSL.*;

/**
 *
 * @author BLutati
 */
public class XParameter extends XObject{

    public XParameter(Element metadata, XObject parent, XObject root) {
        super(metadata, attr(metadata, "name"), parent, root);
        setValue(attr(metadata, "default"));
    }

    public String getType(){
        return attr(getMetadata(), "type");
    }

    @Override
    public List<String> getValues() {
        return list(getValue());
    }

    public String getRemark(){
        return attr(getMetadata(), "remark");
    }
    
    public String getDefaultVariableName(){
        return attr(getMetadata(), "var");
    }

    @Override
    public void consumeValues(List<String> values) {
        setValue(values.remove(0));
    }


    @Override
    public void setValue(String value) {
        super.setValue(value);
    }

    @Override
    public String getValueOf(String param) {
        if (eq(getName(), param)) return getValue();
        return null;
    }

    @Override
    public String getValue() {
        return super.getValue();
    }

    @Override
    public Map<String, String> asValueMap() {
        return JavaDSL.assoc(new HashMap<String, String>(), getName(), getValue());
    }

}
