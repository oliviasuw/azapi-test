/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.ecoa;

import java.util.LinkedList;
import java.util.List;
import org.reflections.scanners.AbstractScanner;

/**
 *
 * @author Administrator
 */
public class ExternalClassesScanner extends AbstractScanner {

    LinkedList<String> classNames = new LinkedList<String>();
    public ExternalClassesScanner() {
    }

    public LinkedList<String> getClassNames() {
        return classNames;
    }
    
    @SuppressWarnings({"unchecked"})
    public void scan(final Object cls) {
        String className = getMetadataAdapter().getClassName(cls);
        if (className.startsWith("ext.sim") && !className.contains("$")){
            classNames.add(className);
        }
    }
}
