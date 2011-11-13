package agt0.dev.project;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

public class Agent0Nature implements IProjectNature {

	public static final String ID = "agent0dev.project.Agent0Nature";
	
	private IProject a0proj;
	
	@Override
	public void configure() throws CoreException {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void deconfigure() throws CoreException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IProject getProject() {
		return a0proj;
	}

	@Override
	public void setProject(IProject project) {
		a0proj = project;
		
	}

}
