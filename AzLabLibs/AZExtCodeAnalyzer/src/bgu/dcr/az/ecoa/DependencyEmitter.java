/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.ecoa;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.bcel.Repository;
import org.apache.bcel.classfile.ConstantClass;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.DescendingVisitor;
import org.apache.bcel.classfile.EmptyVisitor;
import org.apache.bcel.classfile.JavaClass;

public class DependencyEmitter extends EmptyVisitor {

    private JavaClass javaClass;
    private Set<String> dependencies;
    private Set<String> classesToCheck;

    public DependencyEmitter(JavaClass javaClass, Set<String> classesToCheck) {
        this.javaClass = javaClass;
        this.classesToCheck = classesToCheck;
        this.dependencies = new HashSet<>();
    }

    @Override
    public void visitConstantClass(ConstantClass obj) {
        ConstantPool cp = javaClass.getConstantPool();
        String bytes = obj.getBytes(cp);
        String clsName = bytes.replaceAll("/", ".");
        if (classesToCheck.contains(clsName)) {
            dependencies.add(clsName);
        }
    }

    public static Map<String, Set<String>> findDependencies(Set<String> classesToCheck) {
        HashMap<String, Set<String>> ret = new HashMap<>();
        for (String c : classesToCheck) {
            try {
                JavaClass javaClass = Repository.lookupClass(c);
                DependencyEmitter visitor = new DependencyEmitter(javaClass, classesToCheck);
                DescendingVisitor classWalker = new DescendingVisitor(javaClass, visitor);
                classWalker.visit();
                ret.put(c, visitor.dependencies);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(DependencyEmitter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return ret;
    }
}