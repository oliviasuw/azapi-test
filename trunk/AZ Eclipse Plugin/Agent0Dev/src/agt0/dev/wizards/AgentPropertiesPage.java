package agt0.dev.wizards;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;

public class AgentPropertiesPage extends WizardPage {
	private Text agentName;

	/**
	 * Create the wizard.
	 */
	public AgentPropertiesPage() {
		super("wizardPage");
		setTitle("Agent Properties");
		setDescription("fill in the needed agent properties");
	}

	public String getAlgorithmName(){
		return agentName.getText();
	}
	
	@Override
	public boolean canFlipToNextPage() {
		return !agentName.getText().isEmpty();
	}
	
	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		container.setLayout(new GridLayout(2, false));
		
		Label lblAgentName = new Label(container, SWT.NONE);
		lblAgentName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblAgentName.setText("Algorithm Name");
		
		agentName = new Text(container, SWT.BORDER);
		agentName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
	}

}
