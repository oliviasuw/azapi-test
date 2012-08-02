package agt0.dev.ui.update;

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

public class FirstUpdateWindow extends Dialog {

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public FirstUpdateWindow(Shell parentShell) {
		super(parentShell);
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
		label.setImage(ResourceManager.getPluginImage("Agent0Dev", "icons/dialogs/greeting.png"));
		
		Label lblAgentZeroFramework = new Label(container, SWT.NONE);
		lblAgentZeroFramework.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		lblAgentZeroFramework.setFont(SWTResourceManager.getFont("Segoe UI", 14, SWT.BOLD));
		lblAgentZeroFramework.setText("Agent Zero Framework download");
		
		Label lblNewLabel = new Label(container, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		lblNewLabel.setText("\r\nHello,\r\nThe system did not find AgentZero Framework in your workspace,\r\nplease press \"Download\"  to download the latest version,\r\n\r\n\r\nif you choose to cancel you will not be able to create/run agent zero projects\r\nbut you can allways press on the \"update agent zero\" button to download the framework");

		return container;
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		Button button = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FrameworkUpdateUnit.UNIT.startUpdateJob(false);
				close();
			}
		});
		button.setText("Download");
		Button button_1 = createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
		button_1.addSelectionListener(new SelectionAdapter() {
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
		return new Point(549, 312);
	}

}
