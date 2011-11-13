package agt0.dev.util;

import static agt0.dev.util.JavaUtils.cint;
import static agt0.dev.util.JavaUtils.isNummeric;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ContentViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import agt0.dev.Activator;
import agt0.dev.util.ui.MVCWidget;

public class SWTUtils {

	public static Display display = Display.getDefault();
	public static Device device = Display.getDefault();
	private static HashMap<String, Image> images = new HashMap<String, Image>();

	public static class Colors {
		public static final Color BLUE = device.getSystemColor(SWT.COLOR_BLUE);
		public static final Color LIGHT_BLUE = new Color(device, 80, 100, 255);
		public static final Color WHITE = device
				.getSystemColor(SWT.COLOR_WHITE);
		public static final Color GRAY = device.getSystemColor(SWT.COLOR_GRAY);
		public static final Color BLACK = device
				.getSystemColor(SWT.COLOR_BLACK);
		public static final Color GHOSTY_WHITE = new Color(device, 245, 245,
				245);
		public static final Color GHOSTY_BLUE = new Color(device, 245, 255, 255);
		public static final Color DARK_GREEN = device
				.getSystemColor(SWT.COLOR_DARK_GREEN);
	}

	public static void gridLayout(Composite cp, int numCol) {
		GridLayout gl = new GridLayout(numCol, false);
		cp.setLayout(gl);
		gl.marginWidth = 0;
		gl.marginHeight = 0;
	}

	public static void inUIThread(Runnable r) {
		if (isUiThread()) {
			r.run();
		} else {
			display().syncExec(r);
		}
	}

	private static Display display() {
		return Display.getDefault();
	}

	public static Composite blankComposite(Composite parent, int numColumns) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = numColumns;
		composite.setLayout(layout);
		return composite;
	}

	public static Composite blankStack(Composite parent) {
		Composite composite = new Composite(parent, SWT.None);
		RowLayout rl = new RowLayout();
		rl.wrap = true;
		rl.pack = false;
		rl.type = SWT.VERTICAL;
		composite.setLayout(rl);
		return composite;
	}

	public static <T> void setListContentModel(ContentViewer tview,
			Class<T> listType) {
		tview.setContentProvider(new ArrayContentProvider() {
			@SuppressWarnings("unchecked")
			@Override
			public Object[] getElements(Object inputElement) {
				return ((List<T>) inputElement).toArray();
			}
		});
	}

	public static Image image(String filePath) {

		Image ret = images.get(filePath);
		if (ret == null) {
			ret = Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
					filePath).createImage();
			images.put(filePath, ret);
		}

		return ret;
	}

	public static void msgbox(final String message) {
		inUIThread(new Runnable() {

			@Override
			public void run() {
				MessageDialog.openInformation(PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getShell(), "Information!",
						message);
			}
		});
	}

	public static void errbox(final String message) {
		inUIThread(new Runnable() {

			@Override
			public void run() {
				MessageDialog.openError(PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getShell(), "Error!",
						message);
			}
		});

	}

	/**
	 * defines jface table viewer as a selection list
	 * 
	 * @param parent
	 * @return
	 */
	public static CheckboxTableViewer jfSelectionList(Composite parent) {
		final Table t = new Table(parent, SWT.CHECK | SWT.BORDER);
		final CheckboxTableViewer tview = new CheckboxTableViewer(t);

		final TableViewerColumn colview = new TableViewerColumn(tview, SWT.NONE);
		colview.setLabelProvider(new ToListColumnLabelProvider(false));

		t.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				super.controlResized(e);
				colview.getColumn().setWidth(
						t.getSize().x - t.getBorderWidth() * 2
								- t.getVerticalBar().getSize().x);
			}
		});

		return tview;
	}

	public static TableViewer jfLogList(Composite parent) {
		final Table t = new Table(parent, SWT.VIRTUAL | SWT.HIDE_SELECTION
				| SWT.FULL_SELECTION);
		final TableViewer tview = new TableViewer(t);

		final TableViewerColumn colview = new TableViewerColumn(tview, SWT.NONE);
		colview.setLabelProvider(new ToListColumnLabelProvider(true));

		t.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				super.controlResized(e);
				colview.getColumn().setWidth(
						t.getSize().x - t.getBorderWidth() * 2
								- t.getVerticalBar().getSize().x);
			}
		});

		// TABLE COSTUMIZATIONS
		t.setLinesVisible(true);

		t.addListener(SWT.MeasureItem, new Listener() {
			public void handleEvent(Event event) {
				// height cannot be per row so simply set
				event.height = 26;
			}
		});

		return tview;
	}

	public static GridLayouter layout(ContentViewer viewer) {
		return layout(viewer.getControl());
	}

	public static GridLayouter layout(Control element) {
		return new GridLayouter(element);
	}

	public static Button button(Composite parent, String text,
			SelectionListener listener) {
		Button bt1 = new Button(parent, SWT.PUSH);
		bt1.setText(text);
		bt1.addSelectionListener(listener);
		return bt1;
	}

	public static IViewPart findView(String id) {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getActivePage().findView(id);
	}

	public static class GridLayouter {
		Control element;
		GridData gdata;

		public GridLayouter(Control element) {
			this.element = element;
			gdata = new GridData();
		}

		public GridLayouter fillHorizon() {
			gdata.horizontalAlignment = GridData.FILL;
			return this;
		}

		public GridLayouter expandHorizon() {
			gdata.grabExcessHorizontalSpace = true;
			return this;
		}

		public GridLayouter spanHorizon(int columns) {
			gdata.horizontalSpan = columns;
			return this;
		}

		public GridLayouter expandAndFillHorizon() {
			return fillHorizon().expandHorizon();
		}

		public GridLayouter marginTop(int pixels) {
			gdata.verticalIndent = pixels;
			return this;
		}

		public GridLayouter expandVertically() {
			gdata.grabExcessVerticalSpace = true;
			return this;
		}

		public GridLayouter fillVertical() {
			gdata.verticalAlignment = SWT.FILL;
			return this;
		}

		public GridLayouter expandAndFillVertical() {
			return expandVertically().fillVertical();
		}

		public GridLayouter expandAndFill() {
			return expandAndFillHorizon().expandAndFillVertical();
		}

		public GridLayouter noMargin() {
			gdata.horizontalIndent = 0;
			gdata.verticalIndent = 0;
			return this;
		}

		public void apply() {
			element.setLayoutData(gdata);
		}

	}

	public static class ToListColumnLabelProvider extends ColumnLabelProvider {
		boolean even = true;
		OptimizedIndexSearcher searcher = null;

		public ToListColumnLabelProvider(boolean alternateRowColor) {
			if (alternateRowColor)
				searcher = new OptimizedIndexSearcher();
		}

		@Override
		public String getText(Object element) {
			return element.toString();
		}

		public Color getBackground(Object element) {
			if (even) {
				return null;
			} else {
				return Colors.GHOSTY_BLUE;
			}
		}

		public void update(ViewerCell cell) {
			if (searcher != null)
				even = searcher.isEven((TableItem) cell.getItem());
			super.update(cell);
		}
	}

	private static class OptimizedIndexSearcher {
		private int lastIndex = 0;

		public boolean isEven(TableItem item) {
			TableItem[] items = item.getParent().getItems();

			// 1. Search the next ten items
			for (int i = lastIndex; i < items.length && lastIndex + 10 > i; i++) {
				if (items[i] == item) {
					lastIndex = i;
					return lastIndex % 2 == 0;
				}
			}

			// 2. Search the previous ten items
			for (int i = lastIndex; i < items.length && lastIndex - 10 > i; i--) {
				if (items[i] == item) {
					lastIndex = i;
					return lastIndex % 2 == 0;
				}
			}

			// 3. Start from the beginning
			for (int i = 0; i < items.length; i++) {
				if (items[i] == item) {
					lastIndex = i;
					return lastIndex % 2 == 0;
				}
			}

			return false;
		}
	}

	// AGENT 0 SPECIFIC!

//	public static Image agentImage(String id) {
//		return image(agentImagePath(id));
//	}

//	public static String agentImagePath(String id) {
//		int code = (isNummeric(id) ? cint(id) : id.hashCode());
//		return "icons/agts/"
//				+ ((code % AlgorithmExaminer.NUMBER_OF_AGENT_ICONS) + 1)
//				+ ".png";
//
//	}

	public static String inputBox(String title, String message) {
		InputDialog inp = new InputDialog(display.getActiveShell(), title,
				message, "", null);
		inp.setBlockOnOpen(true);
		if (inp.open() == InputDialog.OK) {
			return inp.getValue();
		} else {
			return null;
		}
	}

	public static Shell activeShell() {
		return Display.getDefault().getActiveShell();
	}

	public static boolean isUiThread() {
		return Thread.currentThread().getId() == Display.getDefault()
				.getThread().getId();
	}

	public static void showView(final String viewID){
		inUIThread(new Runnable() {
			
			@Override
			public void run() {
				IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
				window.getWorkbench().getViewRegistry().find(viewID);
				try {
					window.getActivePage().showView(viewID);
				} catch (PartInitException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
	
	
	public static <T> boolean openDialog(
			final Class<? extends MVCWidget<T>> dialog, final T model) {
		final boolean[] box = new boolean[1];
		inUIThread(new Runnable() {

			@Override
			public void run() {
				try {
					MVCWidget<T> instance = dialog.getConstructor(Shell.class)
							.newInstance(activeShell());

					instance.setModel(model);
					if (Dialog.OK == ((Dialog) instance).open()) {
						box[0] = true;
						return;
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

				box[0] = false;
			}
		});

		return box[0];
	}
}
