package agt0.dev.util;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

public class EclipseUtils {
	/**
	 * @return the active workspace directory see tutorial at
	 *         http://www.coderanch
	 *         .com/t/453807/vc/find-Workspace-through-Java-code
	 */
	public static File getWorkspaceDirectory() {
		// get object which represents the workspace
		IWorkspace workspace = ResourcesPlugin.getWorkspace();

		// get location of workspace (java.io.File)
		return workspace.getRoot().getLocation().toFile();
	}

	/**
	 * execute somthing in the ui thread
	 * 
	 * @param runnable
	 * @param async
	 */
	public static void doInUiThread(final UIRunnable runnable, boolean async) {
		if (async) {
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					runnable.run(PlatformUI.getWorkbench()
							.getActiveWorkbenchWindow());
				}
			});
		} else {
			PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
				public void run() {
					runnable.run(PlatformUI.getWorkbench()
							.getActiveWorkbenchWindow());
				}
			});
		}
	}

	public static interface UIRunnable {
		void run(IWorkbenchWindow window);
	}

	/**
	 * @param f
	 * @return the ipath represented by the given file
	 */
	public static IPath file2ipath(File f) {
		return Path.fromOSString(f.getAbsolutePath());
	}

	/**
	 * @param f
	 * @return the ifile represented by the given file
	 */
	public static IFile file2ifile(File f) {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		// IPath location=
		// Path.fromOSString(f.getAbsolutePath().substring(getWorkspaceDirectory().getAbsolutePath().length()));
		Path location = new Path(f.getAbsolutePath());
		return workspace.getRoot().getFileForLocation(location);//(location);// getFile(location);
	}

	public static void openEditorFor(File file){
		openEditorFor(file2ifile(file));
	}
	
	/**
	 * will open the given file in a new editor
	 * 
	 * @param file
	 */
	public static void openEditorFor(IFile file) {
		IEditorDescriptor desc = PlatformUI.getWorkbench().getEditorRegistry()
				.getDefaultEditor(file.getName());
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					.getActivePage()
					.openEditor(new FileEditorInput(file), desc.getId());
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
