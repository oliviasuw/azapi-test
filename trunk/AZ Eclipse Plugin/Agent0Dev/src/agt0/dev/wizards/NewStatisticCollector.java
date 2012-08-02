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

import agt0.dev.project.AgentZeroProject;
import agt0.dev.util.SWTUtils;
import agt0.dev.util.ds.TemplateInputStream;
import static agt0.dev.util.PlatformUtils.*;

public class NewStatisticCollector extends Wizard implements INewWizard {
	ModulePropertiesPage appage;
	AgentZeroProject azp;
	
	public NewStatisticCollector() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		setWindowTitle("AgentZero Statistic Collector Creation Wizard.");
		azp = AgentZeroProject.activeProject();
	}
	
	@Override
	public void addPages() {
		appage = new ModulePropertiesPage();
		addPage(appage);
	}

	@Override
	public boolean performFinish() {
		azp.createNewStatisticCollector(appage.getModuleName(), true);
		return true;		
	}

}
