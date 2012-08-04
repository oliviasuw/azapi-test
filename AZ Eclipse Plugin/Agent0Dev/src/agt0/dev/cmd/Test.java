package agt0.dev.cmd;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.IWorkbenchWindow;

import agt0.dev.FrameworkUpdateUnit;
import agt0.dev.FrameworkUpdateUnit.UpdateException;
import agt0.dev.project.AgentZeroProject;
import agt0.dev.ui.update.AfterUpdateNotificationWindow;
import agt0.dev.util.EclipseUtils;

public class Test implements IHandler {

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
		try{
			AgentZeroProject az = AgentZeroProject.activeProject();
			az.findCodeUnits();
		}catch(Exception ex){
			System.out.println("there was an exception ... " + ex.getMessage());
			ex.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isHandled() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void removeHandlerListener(IHandlerListener handlerListener) {
		// TODO Auto-generated method stub

	}

}
