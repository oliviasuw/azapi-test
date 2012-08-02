package agt0.dev.cmd;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

import agt0.dev.ui.CloudExecutionDialog;
import agt0.dev.util.EclipseUtils;
import agt0.dev.util.EclipseUtils.UIRunnable;

public class ExecuteOnAmazonCloudCommand implements IHandler {

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
		EclipseUtils.doInUiThread(new UIRunnable() {

			@Override
			public void run(IWorkbenchWindow window) {
				CloudExecutionDialog w = new CloudExecutionDialog(window
						.getShell());
				w.open();

				MessageConsole console = EclipseUtils.findConsole("AMAZON");
				EclipseUtils.focusConsole(console);
				MessageConsoleStream out = console.newMessageStream();
				out.println(w.getResult().toString());
				// w.getResult()
			}
		}, false);

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
