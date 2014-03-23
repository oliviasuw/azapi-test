/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.anop.utils;

import java.util.HashMap;
import java.util.Map;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.SimpleElementVisitor7;

/**
 *
 * @author User
 */
public class MirrorUtils {

    /**
     * extract the methods (ExecutableElements) from the given type element
     * (which represents a class)
     *
     * @param type
     * @return map of method name -> element
     */
    public static Map<String, ExecutableElement> extractMethods(TypeElement type) {
        final Map<String, ExecutableElement> methods = new HashMap<>();
        for (Element e : type.getEnclosedElements()) {
            if (e.getKind() == ElementKind.METHOD) {
                SimpleElementVisitor7<Void, Void> visitor = new SimpleElementVisitor7<Void, Void>() {

                    @Override
                    public Void visitExecutable(ExecutableElement e, Void p) {
                        methods.put(e.getSimpleName().toString(), e);
                        return null;
                    }
                };

                e.accept(visitor, null);
            }
        }

        return methods;
    }
}
