package agt0.dev.launch;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.CompilationUnit;

import agt0.dev.Global;
import agt0.dev.project.Agent0Nature;
import agt0.dev.project.AgentZeroProject;

import static agt0.dev.util.SourceUtils.*;
import static agt0.dev.util.JavaUtils.*;

public class AgentSelectionModel {
	private List<String> agents;
	private HashMap<String, String> agent2class;
	private String selectedAgent;
		
	public List<String> getAgents() {
		return agents;
	}
	
	public String agent2class(String agent) {
		return agent2class.get(agent);
	}
	
	public void loadAgents(IJavaProject project){
		agents = new LinkedList<String>();
		agent2class = new HashMap<String, String>();
	
		try {
			if (project.getProject().hasNature(AgentZeroProject.NATURE_ID)){
				for (IPackageFragment pack : project.getPackageFragments()){
					for (ICompilationUnit unit : pack.getCompilationUnits()){
						CompilationUnit astUnit = ast(unit, true);
						TypeVisitor tv = new TypeVisitor();
						astUnit.accept(tv);
						
						
						boolean isAgent = false;
						for (String cname : tv.getClassHierarchy()){
							if (cname.startsWith(Global.AGENT_CLASS_NAME.<String>data())){
								isAgent = true;
								break;
							}
						}
						
						if (isAgent){
							agents.add(unit.getTypes()[0].getElementName());
							agent2class.put(unit.getTypes()[0].getElementName(), unit.getTypes()[0].getFullyQualifiedName());
						}
					}
				}
			}
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getSelectedAgentClassName() {
		return agent2class(selectedAgent);
	}
	
	public void setSelectedAgent(String selectedAgent) {
		this.selectedAgent = selectedAgent;
	}
	
}
