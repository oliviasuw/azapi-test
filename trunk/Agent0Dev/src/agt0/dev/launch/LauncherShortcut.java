package agt0.dev.launch;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;

import agt0.dev.project.Agent0Nature;

public class LauncherShortcut implements ILaunchShortcut {
	
	
	public void launch(IEditorPart editor, String mode) {
		IEditorInput input = editor.getEditorInput();
		IJavaElement javaElement = (IJavaElement) input
				.getAdapter(IJavaElement.class);
		if (javaElement != null) {
			searchAndLaunch(new Object[] { javaElement }, mode);
		}
	}

	public void launch(ISelection selection, String mode) {
		if (selection instanceof IStructuredSelection) {
			searchAndLaunch(((IStructuredSelection) selection).toArray(), mode);
		}
	}
	
	public void searchAndLaunch(Object[] search, String mode) {
		if (search.length == 0)
			System.out.println("cannot launch.. ");
		else {
			if (search[0] instanceof IJavaElement) {
				IJavaElement je = (IJavaElement) search[0];
				try {
					IProject prj = je.getJavaProject().getProject();
					if (prj.getNature(Agent0Nature.ID) != null){
						System.out.println("Called For Project: " + prj.getName()); 
						launch(prj, mode, new HashMap<String, String>());
					}
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}else if (search[0] instanceof IProject){
				launch((IProject) search[0], mode, new HashMap<String, String>());
			}
		}
	}
	
	public void launch(IProject proj, String mode, Map<String, String> additionalAttributes) {
		
		try {
			ILaunchManager lman = DebugPlugin.getDefault().getLaunchManager();
			ILaunchConfigurationType type = lman.getLaunchConfigurationType(Launcher.LAUNCH_CONFIGURATION_TYPE_ID);
			
			ILaunchConfigurationWorkingCopy copy = type.newInstance(proj, "a0lc_" + proj.getName() + "_" + mode);//= lc.copy("test");
			
			CommonTab tab = new CommonTab();
			tab.setDefaults(copy);	
			tab.dispose(); 			
			copy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, proj.getName());
			for (Entry<String, String> e : additionalAttributes.entrySet()){
				copy.setAttribute(e.getKey(), e.getValue());
			}
			
			DebugUITools.launch(copy.doSave(), mode);
			
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
