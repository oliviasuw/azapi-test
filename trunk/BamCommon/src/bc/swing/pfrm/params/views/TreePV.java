/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.swing.pfrm.params.views;

import bc.swing.models.GenericTreeModel;
import static bc.dsl.XNavDSL.*;
import static bc.dsl.JavaDSL.*;
import static bc.dsl.SwingDSL.*;

import bc.swing.models.GenericTreeModel.Node;
import bc.swing.pfrm.params.ParamModel;
import bc.swing.pfrm.params.ParamView;
import bc.swing.pfrm.params.views.ext.IconProvider;
import bc.swing.pfrm.params.views.ext.SimpleTreeRenderer;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.swing.ImageIcon;
import nu.xom.Element;

/**
 *
 * @author bennyl
 */
public class TreePV extends TreeBase implements ParamView {

    @Override
    public void setModel(ParamModel model) {
        super.setModel(model);
        
        Object root = model.getValue();
        
        if (root instanceof Element){
            setElementModel((Element) model.getValue());
        }else if (root instanceof Map){
            setMapModel((Map) model.getValue(), model);
        }
    }

    private void setElementModel(Element root) {
        setModel(new GenericTreeModel(new ElementNode(root)));
    }

    private void setMapModel(Map map, final ParamModel model) {
        setModel(new GenericTreeModel(new MapNode(model.getName(), map)));
        setCellRenderer(new SimpleTreeRenderer(new IconProvider() {

            public ImageIcon getIcon(Object item) {
                return model.getPage().getModel().provideParamValueIcon(model.getName(), item);
            }
        }));
    }
    
    public static class MapNode extends Node{

        /**
         * the dictionary value
         */
        Object value;
        public MapNode(Object data, Object value) {
            super(data);
            this.value = value;
        }

        @Override
        public List getChildren() {
            if (value instanceof Map){
                final Map m = (Map) value;
                return map(m.keySet(), new Fn1<MapNode, Object>() {

                    @Override
                    public MapNode invoke(Object arg) {
                        return new MapNode(arg, m.get(arg));
                    }
                });
            }else {
                return new LinkedList();
            }
        }
        
    }
    
    public static class ElementNode extends Node<Element>{

        public ElementNode(Element data) {
            super(data);
        }

        @Override
        public List<Node<Element>> getChildren() {
            return map(childs(getData()), new Fn1<Node<Element>, Element>() {

                @Override
                public Node<Element> invoke(Element arg) {
                    return new ElementNode(arg);
                }
            });
        }

        @Override
        public ImageIcon getIcon() {
            return resIcon(getData().getAttributeValue("icon"));
        }

        @Override
        public String toString() {
            return getData().getValue();
        }
        
    }
    
}
