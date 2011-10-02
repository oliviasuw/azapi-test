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
public class XLoop extends XObject{

    private List<LoopChangedListener> listeners = new LinkedList<LoopChangedListener>();

    public XLoop(Element metadata, XObject parent, XObject root) {
        super(metadata, "LOOP", parent, root);

        if (hasAttr(metadata, "min")){
            int min = cint(attr(metadata, "min"));
            for (int i=0; i<min; i++) addLoopFragment();
        }
    }

    public boolean hasMin(){
        return hasAttr(getMetadata(), "min");
    }

    public int getMin(){
        return cint(attr(getMetadata(), "min"));
    }

    public void addLoopChangedListener(LoopChangedListener lis){
        listeners.add(lis);
    }

    @Override
    public List<String> getValues() {
        LinkedList<String> values = new LinkedList<String>();
        for (XObject c : getChilds()) values.addAll(c.getValues());
        values.add(attr(getMetadata(), "exit"));
        return values;
    }

    @Override
    public String getValueOf(String param) {
        //loop not supports inner analyzing yet..
        return null;
    }

    public XObject addLoopFragment(){
        List<XObject> ret = XCommand.generateFragments(getMetadata(), this, getRoot());
        XObject xob = new XObject(getMetadata(), "loop-frag", this, getRoot()) {

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
                HashMap<String, String> ret = new HashMap<String, String>();
                
                for (XObject c : getChilds()) {
                    ret.putAll(c.asValueMap());
                }
                
                return ret;
            }
        };

        for (XObject c : ret) xob.addChild(c);
        addChild(xob);
        for (LoopChangedListener l : listeners) {
            l.onLoopAdded(xob);
        }
        return xob;
    }

    public XObject removeLoop(){
        final List<XObject> childs = getChilds();
        XObject child = childs.get(childs.size()-1);
        childs.remove(child);
        for (LoopChangedListener l : listeners) l.onLoopRemoved(child);
        return child;
    }

    @Override
    public void consumeValues(List<String> values) {
        this.getChilds().clear();
        String exitValue = attr(getMetadata(), "exit");
        while (ne(values.get(0), exitValue)){
            XObject lf = addLoopFragment();
            lf.consumeValues(values);
        }

        values.remove(0); // remove the exit value
    }

    @Override
    public Map<String, String> asValueMap() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public static interface LoopChangedListener{
        void onLoopAdded(XObject loop);
        void onLoopRemoved(XObject loop);
    }

}
