package agt0.dev.wizards;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import static agt0.dev.util.SWTUtils.*;

import agt0.dev.Activator;
import agt0.dev.model.AlgorithmArtifactsProvider;
import agt0.dev.project.ProjectCreator;
import agt0.dev.util.SWTUtils;

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
		String str = "";
		for (String s : algorithmArtifacts){
			str += s + ", ";
		}
		
		//msgbox("selected items are: " + str + "algorithm name is: " + algorithmName);
		
		ProjectCreator.INSTANCE.create(algorithmName, algorithmArtifacts);
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
