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
import static bc.dsl.JavaDSL.*;

/**
 *
 * @author BLutati
 */
public class XCommand extends XObject {

    private XCommand(Element metadata) {
        super(metadata, attr(metadata, "name"), null, null);
    }

    public static XCommand create(Element metadata) {
        XCommand cmd = new XCommand(metadata);
        for (XObject c : generateFragments(metadata, cmd, cmd)) {
            cmd.addChild(c);
        }

        return cmd;
    }

    public static List<XObject> generateFragments(Element metadata, XObject parent, XObject root) {
        List<XObject> xob = new LinkedList<XObject>();

        for (Element c : childs(metadata)) {
            if (isa(c, "if")) {
                xob.add(XIf.create(c, parent, root));
            } else if (isa(c, "loop")) {
                xob.add(new XLoop(c, parent, root));
            } else if (isa(c, "param")) {
                xob.add(new XParameter(c, parent, root));
            } else if (isa(c, "frame")) {
                xob.add(new XFrame(c, parent, root));
            } else {
                log("cannot parse element - " + c.toXML() + ", throwing...");
            }
        }

        return xob;
    }

    @Override
    public List<String> getValues() {
        List<String> values = new LinkedList<String>();
        for (XObject frag : getChilds()) {
            values.addAll(frag.getValues());
        }

        return values;
    }

    @Override
    public void consumeValues(List<String> values) {
        for (XObject frag : getChilds()) {
            frag.consumeValues(values);
        }
    }

    @Override
    public Map<String, String> asValueMap() {
        Map<String, String> ret = new HashMap<String, String>();
        for (XObject frag : getChilds()) {
            ret.putAll(frag.asValueMap());
        }
        return ret;
    }
}
