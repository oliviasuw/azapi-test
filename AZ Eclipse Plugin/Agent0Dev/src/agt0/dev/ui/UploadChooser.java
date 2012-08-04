package agt0.dev.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jdt.core.JavaModelException;
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

import bgu.dcr.az.ecoa.rmodel.ScannedCodeUnit;

import agt0.dev.project.AgentZeroProject;
import agt0.dev.util.EclipseUtils;
import agt0.dev.util.FileUtils;
import agt0.dev.util.UploadUtils;

import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Label;

public class UploadChooser extends ViewPart {

	public static final String ID = "agt0.dev.ui.UploadChooser"; //$NON-NLS-1$
	private final FormToolkit toolkit = new FormToolkit(Display.getCurrent());
	private Table table;
	private List<ScannedCodeUnit> scannedCode;
	private Text userName;
	private Text password;
	private Label labelUploading;

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

		createActions();
		initializeToolBar();
		initializeMenu();
		container.setLayout(new FormLayout());

		Button btnUpload = new Button(container, SWT.NONE);
		btnUpload.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if("".equals(userName.getText().trim()) || "".equals(password.getText().trim())){
					EclipseUtils.showError("you mast set a user name and passwored in order to upload.");
					return;
				}
				AgentZeroProject az = AgentZeroProject.activeProject();
				if (az != null) {
					labelUploading.setText("uploading");
					
					List<ScannedCodeUnit> selected = new ArrayList<ScannedCodeUnit>();
					int i = 0;
					for (TableItem item : table.getItems()) {
						if (item.getChecked()) {
							selected.add(scannedCode.get(i));
						}
						i++;
					}
					if (selected.size() > 0) {
						for (ScannedCodeUnit unit : selected) {
							try {
								List<File> jarList = UploadUtils.coolectJars(az
										.getJavaProject());
								List<File> srcList = unit.dependencies;
								File zip= UploadUtils.createZip(srcList, jarList);
								UploadUtils.uploadUsingPost(zip,userName.getText().trim(),password.getText().trim());
								zip.delete();
							} catch (JavaModelException e1) {
								e1.printStackTrace();
								return;
							} catch (Exception e1) {
								e1.printStackTrace();
								return;
							}

						}
					}
					labelUploading.setText("");
					
					
				}
			}
		});
		FormData fd_btnUpload = new FormData();
		fd_btnUpload.top = new FormAttachment(0, 3);
		btnUpload.setLayoutData(fd_btnUpload);
		toolkit.adapt(btnUpload, true, true);
		btnUpload.setText("Upload");

		Button btnRefrash = new Button(container, SWT.NONE);
		FormData fd_btnRefrash = new FormData();
		fd_btnRefrash.top = new FormAttachment(btnUpload, 0, SWT.TOP);
		fd_btnRefrash.left = new FormAttachment(btnUpload, 6);
		btnRefrash.setLayoutData(fd_btnRefrash);
		btnRefrash.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				init();
			}
		});
		toolkit.adapt(btnRefrash, true, true);
		btnRefrash.setText("Refresh");

		ScrolledComposite scrolledComposite = new ScrolledComposite(container,
				SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		fd_btnUpload.left = new FormAttachment(scrolledComposite, 0, SWT.LEFT);
		FormData fd_scrolledComposite = new FormData();
		fd_scrolledComposite.left = new FormAttachment(0, 13);
		fd_scrolledComposite.right = new FormAttachment(100, -10);
		fd_scrolledComposite.bottom = new FormAttachment(100, -10);
		fd_scrolledComposite.top = new FormAttachment(0, 36);
		scrolledComposite.setLayoutData(fd_scrolledComposite);
		toolkit.adapt(scrolledComposite);
		toolkit.paintBordersFor(scrolledComposite);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);

		table = new Table(scrolledComposite, SWT.CHECK | SWT.BORDER
				| SWT.FULL_SELECTION);
		toolkit.adapt(table);
		toolkit.paintBordersFor(table);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		TableColumn tblclmnChoosen = new TableColumn(table, SWT.CHECK);
		tblclmnChoosen.setMoveable(true);
		tblclmnChoosen.setWidth(100);
		tblclmnChoosen.setText("Choosen");

		TableColumn tblclmnType = new TableColumn(table, SWT.NONE);
		tblclmnType.setWidth(206);
		tblclmnType.setText("Type");

		TableColumn tblclmnName = new TableColumn(table, SWT.NONE);
		tblclmnName.setWidth(216);
		tblclmnName.setText("Name");
		scrolledComposite.setContent(table);
		scrolledComposite.setMinSize(table
				.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		
		Label labelUserName = new Label(container, SWT.NONE);
		FormData fd_labelUserName = new FormData();
		fd_labelUserName.top = new FormAttachment(btnUpload, 5, SWT.TOP);
		fd_labelUserName.left = new FormAttachment(btnRefrash, 6);
		labelUserName.setLayoutData(fd_labelUserName);
		toolkit.adapt(labelUserName, true, true);
		labelUserName.setText("User Name:");
		
		Label labelPassword = new Label(container, SWT.NONE);
		FormData fd_labelPassword = new FormData();
		fd_labelPassword.top = new FormAttachment(btnUpload, 5, SWT.TOP);
		labelPassword.setLayoutData(fd_labelPassword);
		toolkit.adapt(labelPassword, true, true);
		labelPassword.setText("Password:");
		
		userName = new Text(container, SWT.BORDER);
		fd_labelPassword.left = new FormAttachment(userName, 6);
		FormData fd_userName = new FormData();
		fd_userName.right = new FormAttachment(labelUserName, 104, SWT.RIGHT);
		fd_userName.bottom = new FormAttachment(scrolledComposite, -3);
		fd_userName.left = new FormAttachment(labelUserName, 6);
		userName.setLayoutData(fd_userName);
		toolkit.adapt(userName, true, true);
		
		password = new Text(container, SWT.BORDER);
		FormData fd_password = new FormData();
		fd_password.right = new FormAttachment(labelPassword, 105, SWT.RIGHT);
		fd_password.bottom = new FormAttachment(scrolledComposite, -3);
		fd_password.left = new FormAttachment(labelPassword, 7);
		password.setLayoutData(fd_password);
		toolkit.adapt(password, true, true);
		
		labelUploading = new Label(container, SWT.NONE);
		
		FormData fd_labelUploading = new FormData();
		fd_labelUploading.top = new FormAttachment(labelUserName, 0, SWT.TOP);
		fd_labelUploading.right = new FormAttachment(scrolledComposite, 0, SWT.RIGHT);
		labelUploading.setLayoutData(fd_labelUploading);
		toolkit.adapt(labelUploading, true, true);
		labelUploading.setText("");
		init();

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

	public void init() {
		AgentZeroProject az = AgentZeroProject.activeProject();
		if (az != null) {
			scannedCode = az.findCodeUnits();
			table.removeAll();
			table.redraw();

			for (ScannedCodeUnit unit : scannedCode) {
				table.setRedraw(false);
				TableItem item = new TableItem(table, SWT.NONE);
				item.setText(1, unit.type);
				item.setText(2, unit.registeredName);
				table.setRedraw(true);

			}
		}
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

		TableColumn columnType = new TableColumn(table, SWT.NONE);
		columnType.setWidth(100);
		columnType.setText("Type");

		TableColumn columnName = new TableColumn(table, SWT.NONE);
		columnName.setWidth(100);
		columnName.setText("Name");

		for (int i = 0; i < 3; i++) {
			TableItem item = new TableItem(table, SWT.NONE);
			item.setText(new String[] { null, "" + i, "" + i });
		}
		// final TableEditor editor = new TableEditor(table);
		// editor.horizontalAlignment = SWT.LEFT;
		// editor.grabHorizontal = true;
		shell.pack();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}
}
