/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.x.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import nu.xom.Element;

import static bc.dsl.XNavDSL.*;

/**
 *
 * @author BLutati
 */
public class XFrame extends XObject {

    public XFrame(Element metadata, XObject parent, XObject root) {
        super(metadata, attr(metadata, "name"), parent, root);
        for (XObject c : XCommand.generateFragments(metadata, this, root)) {
            addChild(c);
        }
    }

    @Override
    public List<String> getValues() {
        List<String> ret = new LinkedList<String>();
        for (XObject c : getChilds()) {
            ret.addAll(c.getValues());
        }

        return ret;
    }

    @Override
    public void consumeValues(List<String> values) {
        for (XObject c : getChilds()) {
            c.consumeValues(values);
        }
    }

    @Override
    public Map<String, String> asValueMap() {
        Map<String, String> ret = new HashMap<String, String>();
        for (XObject c : getChilds()) {
            ret.putAll(c.asValueMap());
        }
        return ret;
    }
}
