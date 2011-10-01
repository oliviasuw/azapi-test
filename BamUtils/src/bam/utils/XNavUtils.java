/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bam.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import static bam.utils.JavaUtils.*;
import java.util.List;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

/**
 *
 * @author bennyl
 */
public class XNavUtils {

    public static List<Element> all(String what, Element parent) {
        List<Element> ret = new LinkedList<Element>();
        if (eq(parent.getLocalName(), what)) {
            ret.add(parent);
        } else {
            Elements elements = parent.getChildElements();
            for (int i = 0; i < elements.size(); i++) {
                ret.addAll(all(what, elements.get(i)));
            }
        }

        return ret;
    }

    public static List<Element> childs(String what, Element parent) {
        List<Element> ret = new LinkedList<Element>();
        Elements elements = parent.getChildElements();
        for (int i = 0; i < elements.size(); i++) {
            final Element e = elements.get(i);
            if (eq(e.getLocalName(), what)) {
                ret.add(e);
            }
        }

        return ret;
    }

    public static List<Element> childs(Element parent) {
        List<Element> ret = new LinkedList<Element>();

        if (parent != null) {
            Elements elements = parent.getChildElements();
            for (int i = 0; i < elements.size(); i++) {
                ret.add(elements.get(i));
            }
        }

        return ret;
    }

    public static boolean isa(Element e, String type) {
        return eq(e.getLocalName(), type);
    }

    public static Document xload(File xml) {
        try {
            FileInputStream fis = new FileInputStream(xml);
            Builder b = new Builder();
            Document doc = b.build(fis);
            return doc;
        } catch (Exception ex) {
            return null;
        }
    }

    public static boolean named(Element e, String name) {
        return eq(name, e.getAttributeValue("name"));
    }

    public static boolean typed(Element e, String name) {
        return eq(name, e.getAttributeValue("type"));
    }

    public static String attr(Element e, String attr) {
        final String val = e.getAttributeValue(attr);
        return val == null ? "" : val;
    }

    public static String attr(Object e, String attr) {
        return attr((Element) e, attr);
    }

    public static List<Element> filterByAttr(List<Element> elements, final String attr, final String value) {
        return filter(elements, new Fn<Boolean>() {

            @Override
            public Boolean invoke(Object... args) {
                return eq(attr(args[0], attr), value);
            }
        });
    }
}
