package agt0.dev.wizards;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;

public class ModulePropertiesPage extends WizardPage {
	private Text moduleNameText;

	/**
	 * Create the wizard.
	 */
	public ModulePropertiesPage() {
		super("wizardPage");
		setTitle("New Module Parameters");
		setDescription("please enter the parameters for your new module");
	}

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		container.setLayout(new GridLayout(2, false));
		
		Label lblModuleName = new Label(container, SWT.NONE);
		lblModuleName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblModuleName.setText("Module Name");
		
		moduleNameText = new Text(container, SWT.BORDER);
		moduleNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
	}
	
	public String getModuleName(){
		return moduleNameText.getText();
	}

}
