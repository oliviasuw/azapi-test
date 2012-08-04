package agt0.dev.ui;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import agt0.dev.project.AgentZeroProject;

public class UploadChooser extends ViewPart {

	public static final String ID = "agt0.dev.ui.UploadChooser"; //$NON-NLS-1$
	private final FormToolkit toolkit = new FormToolkit(Display.getCurrent());
	private Table table;
	

	public UploadChooser() {
	}

	/**
	 * Create contents of the view part.
	 * 
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		Composite container = toolkit.createComposite(parent, SWT.NONE);
		toolkit.paintBordersFor(container);

		table = new Table(container, SWT.CHECK | SWT.BORDER
				| SWT.FULL_SELECTION);
		table.setBounds(10, 10, 574, 407);
		toolkit.adapt(table);
		toolkit.paintBordersFor(table);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);


		TableColumn tblclmnChoosen = new TableColumn(table, SWT.CHECK);
		tblclmnChoosen.setMoveable(true);
		tblclmnChoosen.setWidth(100);
		tblclmnChoosen.setText("Choosen");

		TableColumn tblclmnType = new TableColumn(table, SWT.NONE);
		tblclmnType.setWidth(100);
		tblclmnType.setText("Type");

		TableColumn tblclmnName = new TableColumn(table, SWT.NONE);
		tblclmnName.setWidth(100);
		tblclmnName.setText("Name");

		createActions();
		initializeToolBar();
		initializeMenu();
		table.setRedraw(false);
		TableItem item = new TableItem(table, SWT.NONE);
		item.setChecked(true);

		item.setText(1, "type");
		item.setText(2, "name");
		table.setRedraw(true);
		
		Button btnUpload = new Button(container, SWT.NONE);
		btnUpload.setBounds(10, 423, 90, 30);
		toolkit.adapt(btnUpload, true, true);
		btnUpload.setText("Upload");

	}

	public void dispose() {
		toolkit.dispose();
		super.dispose();
	}

	/**
	 * Create the actions.
	 */
	private void createActions() {
		// Create the actions
	}

	/**
	 * Initialize the toolbar.
	 */
	private void initializeToolBar() {
		IToolBarManager tbm = getViewSite().getActionBars().getToolBarManager();
	}

	/**
	 * Initialize the menu.
	 */
	private void initializeMenu() {
		IMenuManager manager = getViewSite().getActionBars().getMenuManager();
	}

	@Override
	public void setFocus() {
		// Set the focus
	}

	public void addToTable(Object value) {

	}

	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());
		final Table table = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION
				| SWT.CHECK);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		TableColumn columnCheck = new TableColumn(table, SWT.NONE);
		columnCheck.setWidth(100);
		columnCheck.setText("Select");
		
		
		TableColumn columnType = new TableColumn (table, SWT.NONE);
		columnType.setWidth(100);
		columnType.setText("Type");
	
		TableColumn columnName = new TableColumn (table, SWT.NONE);
		columnName.setWidth(100);
		columnName.setText("Name");
	

		for (int i = 0; i < 3; i++) {
			TableItem item = new TableItem(table, SWT.NONE);
			item.setText(new String[] { null, "" + i, "" + i });
		}
//		final TableEditor editor = new TableEditor(table);
//		editor.horizontalAlignment = SWT.LEFT;
//		editor.grabHorizontal = true;
		shell.pack();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}
}
