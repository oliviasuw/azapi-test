package agt0.dev.wizards;

import static agt0.dev.util.SWTUtils.blankComposite;
import static agt0.dev.util.SWTUtils.layout;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import agt0.dev.project.AgentZeroProject;

public class ProjectWizard extends Wizard implements INewWizard {

	private IWorkbench workbench;
	private IStructuredSelection selection;
	private List<String> algorithmArtifacts;
	private String algorithmName;

	public ProjectWizard() {
		algorithmArtifacts = new LinkedList<String>();
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.workbench = workbench;
		this.selection = selection;
		setWindowTitle("Agent0 Project Creation Wizard.");
	}

	@Override
	public void addPages() {
		addPage(new Page1("Agent0 page name"));
	}

	@Override
	public boolean performFinish() {
		
		
		//msgbox("selected items are: " + str + "algorithm name is: " + algorithmName);
		
		//ProjectCreator.INSTANCE.create(algorithmName, algorithmArtifacts);
		AgentZeroProject.createNew(algorithmName, new NullProgressMonitor());
		return true;
	}

	public class Page1 extends WizardPage {

		public Page1(String pageName) {
			super(pageName);
			setTitle("New Agent0 Algorithm Implementation Project");
			setDescription("please enter the implementation details");
		}

		@Override
		public void createControl(Composite parent) {
			Composite composite = blankComposite(parent, 2);

			setControl(composite);

			// ALGORITHM NAME CAPTION
			new Label(composite, SWT.NONE).setText("Algorithm Name");

			// ALGORITHM NAME TEXT
			final Text algorithmNameText = new Text(composite, SWT.BORDER);
			layout(algorithmNameText).expandAndFillHorizon().apply();
			algorithmNameText.addFocusListener(new FocusListener() {
				
				@Override
				public void focusLost(FocusEvent e) {
					algorithmName = algorithmNameText.getText();
				}
				
				@Override
				public void focusGained(FocusEvent e) {	}
			});

//			// ALGORITHM ARTIFACTS CAPTION
//			Label lbl = new Label(composite, SWT.NONE);
//			lbl.setText("Algorithm Artifacts");
//			layout(lbl).expandAndFillHorizon().spanHorizon(2).marginTop(14)
//					.apply();
//
//			// ALGORITHM ARTIFACTS LIST
//			final CheckboxTableViewer tview = jfSelectionList(composite);
//			layout(tview).expandAndFill().spanHorizon(2).apply();
//			setListContentModel(tview, String.class);
//			
//			tview.getControl().setForeground(Colors.LIGHT_BLUE);
//			tview.setInput(AlgorithmArtifactsProvider.INSTANCE.getArtifacts());
//			
//			tview.addCheckStateListener(new ICheckStateListener() {
//				
//				@Override
//				public void checkStateChanged(CheckStateChangedEvent event) {
//					if (event.getChecked()){
//						algorithmArtifacts.add(event.getElement().toString());
//					}else {
//						algorithmArtifacts.remove(event.getElement().toString());
//					}
//				}
//			});
			
		}
	}

}
