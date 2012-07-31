/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.srccon.cscn;

import bgu.dcr.az.db.ent.Code;
import bgu.dcr.az.db.ent.CodeType;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.QualifiedType;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.UnionType;
import org.eclipse.jdt.core.dom.WildcardType;

/**
 *
 * @author Administrator
 */
public class CodeScanner {
    public static final String AGENT_TYPE = "AGENT";
    public static final String CORRECTNESS_TESTER_TYPE = "CORRECTNESS_TESTER";
    public static final String ERROR_TYPE = "ERROR";
    public static final String LIMITER_TYPE = "LIMITER";
    public static final String MESSAGE_DELAYER_TYPE = "MESSAGE_DELAYER";
    public static final String PROBLEM_GENERATOR_TYPE = "PROBLEM_GENERATOR";
    public static final String STATISTIC_COLLECTOR_TYPE = "STATISTIC_COLLECTOR";
    public static final String UNKNOWN_TYPE = "UNKNOWN";
    
    public static List<Code> scan(File sourcePath, File extLibPath, File azLibPath){
        //1. create CodeFileData for all the files in source path
        //2. merge all the code file data class names into one set of strings 
        //3. for each cu search simple names that shows in the class names and add them to this class dependencies
        //4. from the given data create list of code to return
        return null;
    }
    
    private static class CodeFileData{
        CompilationUnit codeAST;
        Set<ClassData> declaredClasses;
        File file;
        Set<String> dependencies;
    }
    
    private static class ClassData{
        CodeType codeType;
        String className;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {

        ASTParser parser = ASTParser.newParser(AST.JLS4);

        final File agentFile = new File("Agent.java");
        FileReader fr = new FileReader(agentFile);
        char[] whole = new char[(int) agentFile.length()];
        fr.read(whole);

        parser.setSource(whole);
//        parser.setSource(new C);
        //parser.setSource("/*abc*/".toCharArray());
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        //ASTNode node = parser.createAST(null);



        //ICompilationUnit cu = new SourceFile(new File   ("/bla", null), null);
        final CompilationUnit cu = (CompilationUnit) parser.createAST(null);
//        System.out.println("Class name: " + Arrays.toString(cu.types().toArray()));
        for (Iterator it = cu.types().iterator(); it.hasNext();) {
            TypeDeclaration t = (TypeDeclaration) it.next();

            System.out.println("found: " + t.getName());

        }

        final Set<String> types = new HashSet<>();
        cu.accept(new ASTVisitor() {
//            @Override
//            public boolean visit(FieldDeclaration node) {
//                scanTypes(node.getType());
//                return true;
//            }
//
//            @Override
//            public boolean visit(VariableDeclarationStatement node) {
//                scanTypes(node.getType());
//                return true;
//            }
//
//            @Override
//            public boolean visit(SingleVariableDeclaration node) {
//                scanTypes(node.getType());
//                return true;
//            }
//
//            @Override
//            public boolean visit(SimpleType node) {
//                System.out.println("found type: " + node);
//                return true;
//            }

            @Override
            public boolean visit(SimpleName node) {
                System.out.println("found name: " + node);
                return true;
            }
            
            
            
            private void scanTypes(Type type) {
                Queue typesToScan = new LinkedList();
                typesToScan.add(type);
                while (!typesToScan.isEmpty()) {
                    Object t = typesToScan.poll();

                    if (t instanceof ParameterizedType) {
                        ParameterizedType pt = (ParameterizedType) t;
                        typesToScan.add(pt.getType());
                        typesToScan.addAll(pt.typeArguments());
                    } else if (t instanceof SimpleType) {
                        SimpleType st = (SimpleType) t;
                        types.add(st.getName().getFullyQualifiedName());
                    } else if (t instanceof ArrayType) {
                        ArrayType at = (ArrayType) t;
                        typesToScan.add(at.getElementType());
                    } else if (t instanceof QualifiedType) {
                        QualifiedType qt = (QualifiedType) t;
                        types.add(qt.getName().getFullyQualifiedName());
                        typesToScan.add(qt.getQualifier());
                    } else if (t instanceof UnionType) {
                        UnionType ut = (UnionType) t;
                        typesToScan.addAll(ut.types());
                    } else if (t instanceof WildcardType) {
                        WildcardType wct = (WildcardType) t;
                        typesToScan.add(wct.getBound());
                    }
                }
            }
        });
        
        System.out.println("Depends on: " + Arrays.toString(types.toArray()));
    }
}
