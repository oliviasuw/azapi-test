package bgu.dcr.az.metagen.model;

import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;

public class ModifiersMetadata {
	
	
	private Element element;

	public ModifiersMetadata(Element element) {
		this.element = element;
	}
	
	private boolean contains(Modifier m){
		for (Modifier mod : element.getModifiers()){
			if (m == mod) return true;
		}
		
		return false;
	}
	
	public boolean isPublic(){
		return contains(Modifier.PUBLIC);
	}
	
	public boolean isStatic(){
		return contains(Modifier.STATIC);
	}
	
	public boolean isFinal(){
		return contains(Modifier.FINAL);
	}
	
	public boolean isAbstract(){
		return contains(Modifier.ABSTRACT);
	}
	
	public boolean isPrivate(){
		return contains(Modifier.PRIVATE);
	}
	
	public boolean isProtected(){
		return contains(Modifier.PROTECTED);
	}
	
	public boolean isPackageProtected(){
		return !isPublic() && !isPrivate() && !isProtected();
	}
	
	public boolean isNative(){
		return contains(Modifier.NATIVE);
	}
	
	public boolean isTransient(){
		return contains(Modifier.TRANSIENT);
	}
	
	public boolean isVolatile(){
		return contains(Modifier.VOLATILE);
	}
	
	public boolean isSynchronized(){
		return contains(Modifier.SYNCHRONIZED);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("");
		for (Modifier e : element.getModifiers()) {
			sb.append(e.toString()).append(" ");
		}

		if (sb.length() == 0)
			return "";
		return sb.deleteCharAt(sb.length() - 1).toString();
	}
	
}
