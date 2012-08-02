package agt0.dev.util;

import static agt0.dev.util.SWTUtils.inUIThread;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.part.FileEditorInput;

import agt0.dev.Activator;

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
		return workspace.getRoot().getFileForLocation(location);// (location);//
																// getFile(location);
	}

	public static void openEditorFor(File file) {
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

	public static MessageConsole findConsole(String name) {
		ConsolePlugin plugin = ConsolePlugin.getDefault();
		IConsoleManager conMan = plugin.getConsoleManager();
		IConsole[] existing = conMan.getConsoles();
		for (int i = 0; i < existing.length; i++)
			if (name.equals(existing[i].getName()))
				return (MessageConsole) existing[i];
		// no console found, so create a new one
		MessageConsole myConsole = new MessageConsole(name, null);
		conMan.addConsoles(new IConsole[] { myConsole });
		return myConsole;
	}

	public static boolean focusConsole(IConsole toFocus) {
		try {
			IConsole myConsole = toFocus;// your console instance
			IWorkbenchPage page = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getActivePage();// obtain the
																// active page
			String id = IConsoleConstants.ID_CONSOLE_VIEW;
			IConsoleView view;
			view = (IConsoleView) page.showView(id);
			view.display(myConsole);
			return true;
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	public static IEditorPart getActiveEditor() {
		final IEditorPart[] box = new IEditorPart[1];
		inUIThread(new Runnable() {

			@Override
			public void run() {
				box[0] = Activator.getDefault().getWorkbench()
						.getActiveWorkbenchWindow().getActivePage()
						.getActiveEditor();
			}
		});

		return box[0];
	}
	
	public static ISelection getSelection(){
		final ISelection[] ret = new ISelection[1];
		doInUiThread(new UIRunnable() {
			
			@Override
			public void run(IWorkbenchWindow window) {
				ret[0] = window.getSelectionService().getSelection();
			}
		}, true);
		return ret[0];
	}

	public static IResource extractResourceFromSelection(ISelection sel) {
		if (!(sel instanceof IStructuredSelection))
			return null;
		IStructuredSelection ss = (IStructuredSelection) sel;
		Object element = ss.getFirstElement();
		if (element instanceof IResource)
			return (IResource) element;
		if (!(element instanceof IAdaptable))
			return null;
		IAdaptable adaptable = (IAdaptable) element;
		Object adapter = adaptable.getAdapter(IResource.class);
		return (IResource) adapter;
	}

	public static IProject getActiveProject() {
		IResource res = extractResourceFromSelection(getSelection());
		if (res == null){
			IEditorPart editor = getActiveEditor();
			IFileEditorInput input = (IFileEditorInput)editor.getEditorInput() ;
		    IFile file = input.getFile();
		    return file.getProject();
		}
		return res.getProject();
	}
	
	public static IJavaProject getActiveJavaProject(){
		IProject baseProject = getActiveProject();
		if (baseProject != null){
			try {
				if (baseProject.hasNature(JavaCore.NATURE_ID)) {
					return JavaCore.create(baseProject);
				}
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return null;
	}

}
