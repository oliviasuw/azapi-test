package agt0.dev.util;

import static agt0.dev.util.JavaUtils.compareByFn;
import static agt0.dev.util.JavaUtils.drop;
import static agt0.dev.util.JavaUtils.select;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.internal.core.hierarchy.TypeHierarchy;

public class SourceUtils {
	
	private static boolean isAlphaNummeric(char c){
		return (c >= 'a' && c <= 'z') ||
				(c >='A' && c <= 'Z') ||
				(c >= '0' && c <='9');
	}
	
	public static IMethod method(IType type, String name){
		try {
			return (IMethod) select(type.getMethods(), compareByFn("elementName", name));
		} catch (JavaModelException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static String camelCase(String what){
		char[] chars = what.toCharArray();
		StringBuilder sb = new StringBuilder();
		boolean camel = true;
		char c;
		
		for (int i=0; i<chars.length; i++){
			c = chars[i];
			if (isAlphaNummeric(c)){
				if (camel){
					camel = false;
					c = Character.toUpperCase(c);
				}
				
				sb.append(c);
			}else {
				camel = true;
			}
		}
		
		return sb.toString();
	}
	
	public static CompilationUnit ast(ICompilationUnit unit, boolean resolveBinding) {
		ASTParser parser = ASTParser.newParser(AST.JLS3); 
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(unit); // set source
		parser.setResolveBindings(resolveBinding); // we need bindings later on
		return (CompilationUnit) parser.createAST(null /* IProgressMonitor */); // parse
	}
	
	
	public static IType typeOf(ICompilationUnit unit){
		return unit.getType(drop(unit.getElementName(), 4));
	}
	
	public static class TypeVisitor extends ASTVisitor {
		private LinkedList<String> classHierarchy;
		
		@Override
		public boolean visit(TypeDeclaration node) {
			classHierarchy = new LinkedList<String>();
			
			ITypeBinding type = node.resolveBinding().getTypeDeclaration();
			while (type != null && !type.getName().equals("Object")){
				System.out.println("type infered: " + type.getQualifiedName());
				classHierarchy.addLast(type.getQualifiedName());

				type =  type.getSuperclass();
			}
			
			return true;
		
		}
		
		public LinkedList<String> getClassHierarchy() {
			return classHierarchy;
		}
	}
}
