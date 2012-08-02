package agt0.dev.ui;

import java.io.File;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.ResourceManager;
import org.eclipse.wb.swt.SWTResourceManager;

import swing2swt.layout.FlowLayout;
import agt0.dev.SharedDataUnit;

public class WhereIsJFXDialog extends Dialog {
	private Text jfxlocText;
	private String jfxLocation;
	
	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public WhereIsJFXDialog(Shell parentShell) {
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
		
		Composite composite = new Composite(container, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		composite.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		Label label = new Label(composite, SWT.NONE);
		label.setImage(ResourceManager.getPluginImage("Agent0Dev", "icons/dialogs/where.png"));
		
		Label lblAgentZeroFramework = new Label(composite, SWT.NONE);
		lblAgentZeroFramework.setFont(SWTResourceManager.getFont("Segoe UI", 14, SWT.BOLD));
		lblAgentZeroFramework.setText("JavaFx Runtime Nedded.");
		
		Label lblNewLabel = new Label(container, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		lblNewLabel.setText("\r\nAgentZero Visualization Viewer requires JavaFx runtime.\r\nAgentZero could not locate the required runtime in your system.\r\nyou can download JavaFx runtime by following the link bellow.");
		
		Link link = new Link(container, SWT.NONE);
		link.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		link.setText("<a href=\"http://www.oracle.com/technetwork/java/javafx/downloads/index.html\">JavaFx Runtime</a>");
		
		Label lblIfYouAlready = new Label(container, SWT.NONE);
		lblIfYouAlready.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		lblIfYouAlready.setText("\r\nIf you already install JavaFx runtime but still seeing this message\r\nplease supply the location to the runtime installation \r\n(default installation location on windows machine is: C:\\Program Files\\Oracle\\JavaFX <VERSION> Runtime)");
		
		Label lblJavafxRuntimeLocation = new Label(container, SWT.NONE);
		lblJavafxRuntimeLocation.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblJavafxRuntimeLocation.setText("JavaFx runtime location");
		
		jfxlocText = new Text(container, SWT.BORDER);
		jfxlocText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		return container;
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		Button btnUpdate = createButton(parent, IDialogConstants.CLIENT_ID + 1, "Execute",
				true);
		btnUpdate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) { 
				if (!jfxlocText.getText().isEmpty()){
					SharedDataUnit.UNIT.storeJavaFxRuntimeLocation(new File(jfxlocText.getText()));
				}
				
				close();
			}
		});
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
		return new Point(648, 329);
	}

	
	
}
