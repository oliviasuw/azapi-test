package agt0.dev.ui;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.wb.swt.ResourceManager;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import agt0.dev.FrameworkUpdateUnit;
import agt0.dev.Global;

public class ErrorDialog extends Dialog {

	String desc = "?";
	
	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public ErrorDialog(Shell parentShell) {
		super(parentShell);
	}


	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public ErrorDialog(Shell parentShell, String desc) {
		super(parentShell);
		this.desc = desc;
	}

	
	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout(2, false));
		
		Label label = new Label(container, SWT.NONE);
		label.setImage(ResourceManager.getPluginImage("Agent0Dev", "icons/dialogs/found-bug.png"));
		
		Label lblAgentZeroFramework = new Label(container, SWT.NONE);
		lblAgentZeroFramework.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		lblAgentZeroFramework.setFont(SWTResourceManager.getFont("Segoe UI", 14, SWT.BOLD));
		lblAgentZeroFramework.setText("Error");
		
		Label desc = new Label(container, SWT.NONE);
		desc.setText(this.desc);
		desc.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));

		return container;
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		Button btnOk = createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
		btnOk.setText("Close");
		btnOk.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				close();
			}
		});
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(561, 225);
	}

}
