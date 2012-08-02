/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package agt0.dev.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author blutati
 */
public class TemplateReader {

    List<Part> parts = new LinkedList<Part>();

    public TemplateReader(File file) throws IOException {

        String temp = FileUtils.unPersistText(file);
        while (!temp.isEmpty()) {
            int idx = temp.indexOf("${");
            if (idx < 0) {
                parts.add(new StringPart(temp));
                temp = "";
            } else {
                parts.add(new StringPart(temp.substring(0, idx)));
                temp = temp.substring(idx + 2);
                idx = temp.indexOf("}");
                parts.add(new TemplatePart(temp.substring(0, idx)));
                temp = temp.substring(idx + 1);
            }
        }

    }

    public String get(Object... fields) {
        Map m = new HashMap();
        for (int i=0;i<fields.length;i+=2){
            m.put(fields[i], fields[i+1]);
        }
        
        StringBuilder sb = new StringBuilder();
        for (Part p : parts){
            sb.append(p.toString(m));
        }
        
        return sb.toString();
    }

    private static interface Part {

        String toString(Map vals);
    }

    private static class StringPart implements Part {

        String s;

        public StringPart(String s) {
            this.s = s;
        }

        @Override
        public String toString(Map vals) {
            return s;
        }
    }

    private static class TemplatePart implements Part {

        String name;

        public TemplatePart(String name) {
            this.name = name;
        }

        @Override
        public String toString(Map vals) {
            return vals.get(name).toString();
        }
    }
}
