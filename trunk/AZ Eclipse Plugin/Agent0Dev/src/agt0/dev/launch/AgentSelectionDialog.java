package agt0.dev.launch;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.ResourceManager;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.List;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;

import agt0.dev.util.ui.MVCWidget;

import static agt0.dev.util.SWTUtils.*;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.layout.GridLayout;

import static agt0.dev.util.JavaUtils.*;

public class AgentSelectionDialog extends TitleAreaDialog implements MVCWidget<AgentSelectionModel>{

	private AgentSelectionModel model;
	private ComboViewer agentList;
	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public AgentSelectionDialog(Shell parentShell) {
		super(parentShell);
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		setTitleImage(ResourceManager.getPluginImage("Agent0Dev", "icons/dialogs/agent-selection.png"));
		setTitle("Agent Selection");
		setMessage("more than one agent found - select who you want to test.");
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayout(new GridLayout(1, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		agentList = new ComboViewer(container, SWT.READ_ONLY);
		Combo combo = agentList.getCombo();
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		setListContentModel(agentList, String.class);
		agentList.setInput(model.getAgents());
		agentList.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				String asel = selection.getFirstElement().toString();
				model.setSelectedAgent(asel);
			}
		});

		agentList.setSelection(new StructuredSelection(model.getAgents().get(0)));
		return area;
	}

	public void setModel(AgentSelectionModel model){
		this.model = model;
	}
	
	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(452, 180);
	}
}
