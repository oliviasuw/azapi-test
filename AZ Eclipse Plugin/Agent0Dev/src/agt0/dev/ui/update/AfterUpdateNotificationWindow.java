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
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.jface.text.TextViewer;

public class AfterUpdateNotificationWindow extends Dialog {
	
	String version = "i4.1";
	String changeLog = "no changes was made...";
	
	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public AfterUpdateNotificationWindow(Shell parentShell, String version, String changeLog) {
		super(parentShell);
		this.version = version;
		this.changeLog = changeLog;
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
		label.setImage(ResourceManager.getPluginImage("Agent0Dev", "icons/dialogs/update-sucess.png"));
		
		Label lblAgentZeroFramework = new Label(container, SWT.NONE);
		lblAgentZeroFramework.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		lblAgentZeroFramework.setFont(SWTResourceManager.getFont("Segoe UI", 16, SWT.BOLD));
		lblAgentZeroFramework.setText("Agent Zero Framework updated!!!");
		
		Label lblVersion = new Label(container, SWT.NONE);
		lblVersion.setText("Version: ");
		
		Label versionLabel = new Label(container, SWT.NONE);
		versionLabel.setForeground(SWTResourceManager.getColor(SWT.COLOR_LIST_SELECTION));
		versionLabel.setText(version);
		
		Label lblChangelog = new Label(container, SWT.NONE);
		lblChangelog.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		lblChangelog.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		lblChangelog.setText("Change Log:");
		
		TextViewer textViewer = new TextViewer(container, SWT.BORDER);
		StyledText changeLogText = textViewer.getTextWidget();
		changeLogText.setText(changeLog);
		changeLogText.setEditable(false);
		changeLogText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

		return container;
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(536, 389);
	}

}
