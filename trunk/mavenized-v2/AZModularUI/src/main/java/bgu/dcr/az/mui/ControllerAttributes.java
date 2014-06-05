/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.mui;

import bgu.dcr.az.conf.api.JavaDocInfo;
import java.util.Iterator;

/**
 *
 * @author bennyl
 */
public class ControllerAttributes implements Iterable<String> {

    private String registrationName;
    private JavaDocInfo doc;

    public ControllerAttributes(String registrationName, JavaDocInfo doc) {
        this.registrationName = registrationName;
        this.doc = doc;
    }

    public String registrationName() {
        return registrationName;
    }

    public String iconPath() {
        return doc.first("icon");
    }

    public String description() {
        return doc.description();
    }

    public String title() {
        return doc.first("title");
    }

    public String get(String name) {
        return doc.first(name);
    }

    public Iterable<String> multiValueAttr(String name) {
        return doc.tag(name);
    }

    @Override
    public Iterator<String> iterator() {
        return doc.tags().iterator();
    }

}
