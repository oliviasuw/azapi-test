package agt0.dev.wizards;

import static agt0.dev.util.JavaUtils.assoc;
import static agt0.dev.util.PlatformUtils.resource;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.internal.core.PackageFragmentRoot;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import agt0.dev.util.SWTUtils;
import agt0.dev.util.ds.TemplateInputStream;
import static agt0.dev.util.PlatformUtils.*;

public class NewAgent extends Wizard implements INewWizard {
	AgentPropertiesPage appage;
	IJavaProject project;
	
	public NewAgent() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		setWindowTitle("AgentZero Agent Creation Wizard.");
		
		IJavaElement element = (IJavaElement) selection.getFirstElement();
		project = element.getJavaProject();
	}
	
	@Override
	public void addPages() {
		appage = new AgentPropertiesPage();
		addPage(appage);
	}

	@Override
	public boolean performFinish() {
		IFolder srcf = project.getProject().getFolder("src");
		IFolder agtf = srcf.getFolder("ext/sim/agents");
		IFile afile = agtf.getFile(appage.getAlgorithmName() + "Agent.java");
		
		try {
			Map<String, String> tkeys = assoc(new HashMap<String, String>(),
					"ALGORITHM_NAME", appage.getAlgorithmName());
			afile.create(new TemplateInputStream(tkeys,
					resource("templates/NEW_AGENT_TEMPLATE.java")), true, null);
			openEditorFor(afile);
			
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
		
	}

}
