package agt0.dev.project.explorer;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.internal.core.PackageFragment;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

import static agt0.dev.util.SWTUtils.*;

import agt0.dev.project.Agent0Nature;
import agt0.dev.project.AgentZeroProject;

public class LabelProvider implements ILabelProvider {

	@Override
	public void addListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public Image getImage(Object element) {
		try {

			if (element instanceof IPackageFragment) {
				IPackageFragment pf = (IPackageFragment) element;
				IProject project = pf.getJavaProject().getProject();
				if (project.getNature(AgentZeroProject.NATURE_ID) != null) {
					String path = pf.getPath().toString();
					if (path.endsWith("ext/sim/agents"))
						return image("icons/agents-folder.png");
					else if (path.endsWith("ext/sim/tools"))
						return image("icons/tools-folder.png");
					else if (path.endsWith("ext/sim/stracturedProblems"))
						return image("icons/sproblems-folder.png");
					else if (path.endsWith("ext/sim/modules"))
						return image("icons/moudules-folder.png");
				}
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public String getText(Object element) {
		try {

			if (element instanceof IPackageFragment) {
				IPackageFragment pf = (IPackageFragment) element;
				IProject project = pf.getJavaProject().getProject();
				if (project.getNature(AgentZeroProject.NATURE_ID) != null) {
					String path = pf.getPath().toString();
					if (path.endsWith("ext/sim/agents"))
						return "Agents";
					else if (path.endsWith("ext/sim/tools"))
						return "Tools";
					else if (path.endsWith("ext/sim/stracturedProblems"))
						return "Stractured Problems";
					else if (path.endsWith("ext/sim/modules"))
						return "Modules";
				}
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}

		return null;

	}

}
