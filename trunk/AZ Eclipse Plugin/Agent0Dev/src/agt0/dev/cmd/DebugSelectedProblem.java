package agt0.dev.cmd;

import java.io.File;
import java.util.HashMap;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import agt0.dev.launch.Launcher;
import agt0.dev.launch.LauncherShortcut;

import static agt0.dev.util.JavaUtils.*;

public class DebugSelectedProblem implements IHandler {


	@Override
	public void addHandlerListener(IHandlerListener handlerListener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IStructuredSelection selected = (IStructuredSelection) HandlerUtil.getCurrentSelection(event);
		IResource res = (IResource) selected.getFirstElement();
		File path = res.getLocation().toFile();
		
		//IJavaProject ijp = JavaCore.create(res.getProject());
		//StructuredSelection projsel = new StructuredSelection();
		//projsel.toList().add(ijp);
		new LauncherShortcut().launch(res.getProject(), "debug", assoc(new HashMap<String, String>(), Launcher.PROBLEM_ATTRIBUTE, path.getAbsolutePath()));
		println("debugging " + path.getAbsolutePath());
		return null;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public boolean isHandled() {
		return true;
	}

	@Override
	public void removeHandlerListener(IHandlerListener handlerListener) {
		// TODO Auto-generated method stub

	}

}
