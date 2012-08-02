package agt0.dev.ui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.wb.swt.ResourceManager;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;


public class CloudExecutionDialog extends Dialog {
	private static final int STAM_ID = IDialogConstants.CLIENT_ID + 1;
	
	private Text accessKeyText;
	private Text secretKeyText;
	private Spinner numWorkersSpin;
	private Combo machineTypeCombo;
	private Result result = null;

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public CloudExecutionDialog(Shell parentShell) {
		super(parentShell);
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		GridLayout gridLayout = (GridLayout) container.getLayout();
		gridLayout.verticalSpacing = 10;
		gridLayout.numColumns = 2;
		
		Label label = new Label(container, SWT.NONE);
		label.setImage(ResourceManager.getPluginImage("Agent0Dev", "icons/dialogs/cloud execution.png"));
		
		Label label_1 = new Label(container, SWT.NONE);
		label_1.setText("Run Experiments On The Cloud (experimental) ");
		label_1.setFont(SWTResourceManager.getFont("Segoe UI", 13, SWT.BOLD));
		
		Group group = new Group(container, SWT.NONE);
		group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		group.setText("Amazon Account Information");
		group.setLayout(new GridLayout(2, false));
		
		Label label_2 = new Label(group, SWT.NONE);
		label_2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_2.setText("Access Key");
		
		accessKeyText = new Text(group, SWT.BORDER);
		accessKeyText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label label_3 = new Label(group, SWT.NONE);
		label_3.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_3.setText("Secret Key");
		
		secretKeyText = new Text(group, SWT.BORDER | SWT.PASSWORD);
		secretKeyText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Group group_1 = new Group(container, SWT.NONE);
		group_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		group_1.setText("Environment Information");
		group_1.setLayout(new GridLayout(2, false));
		
		Label label_4 = new Label(group_1, SWT.NONE);
		label_4.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_4.setText("Machines Type");
		
		machineTypeCombo = new Combo(group_1, SWT.READ_ONLY);
		machineTypeCombo.setItems(new String[] {"Micro (t1.micro)", "Small (m1.small)", "High-CPU Medium (c1.medium)", "Medium (m1.medium)"});
		machineTypeCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		machineTypeCombo.select(0);
		
		Label label_5 = new Label(group_1, SWT.NONE);
		label_5.setText("Number Of Workers");
		
		numWorkersSpin = new Spinner(group_1, SWT.BORDER);
		numWorkersSpin.setMaximum(1000);
		numWorkersSpin.setMinimum(1);

		return container;
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		Button button_1 = createButton(parent, STAM_ID, IDialogConstants.OK_LABEL,
				true);
		button_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onOk();
			}
		});
		Button button = createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//close();
			}
		});
	}

	protected void onOk() {
		result = new Result(
				accessKeyText.getText(),
				secretKeyText.getText(), 
				machineTypeCombo.getText(),
				numWorkersSpin.getSelection());
		
		System.out.println("the result is: " + result);
		close();
	}
	

	public Result getResult() {
		return result;
	}
	

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(454, 327);
	}

	
	public static class Result {
		String accessKey;
		String secretKey;
		String machineType;
		int numOfWorkers;
		
		public Result(String accessKey, String secretKey, String machineType,
				int numOfWorkers) {
			super();
			this.accessKey = accessKey;
			this.secretKey = secretKey;
			this.machineType = machineType;
			this.numOfWorkers = numOfWorkers;
		}

		@Override
		public String toString() {
			return "Result [accessKey=" + accessKey + ", secretKey="
					+ secretKey + ", machineType=" + machineType
					+ ", numOfWorkers=" + numOfWorkers + "]";
		}
		
	}
}
