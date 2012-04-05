package bgu.dcr.az.cpu.client.scr;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import bgu.dcr.az.cpu.client.CPUClient;
import bgu.dcr.az.cpu.client.wgt.AzButtonWithImg;
import bgu.dcr.az.cpu.shared.AlgorithmData;
import bgu.dcr.az.cpu.shared.ExperimentData;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

public class ExperimentsPanel extends Composite implements HasText {

	private Command deleteCmd;
	private Command executeCmd;
	private PopupPanel contextMenu;
	private ExperimentData currentSelection;
	private AdvClickListener listener;
	private XMLViewWindow xmlWindow;
	final private PopupPanel popupPanel = new PopupPanel(true);
	final private PopupPanel xmlPopup = new PopupPanel(true);
	
	@UiField
	AzButtonWithImg addButton;
	@UiField
	AzButtonWithImg clearAllButton;
	
	
	// public interface MyCellListResources extends CellList.Resources {
	// @Source({ "css/CellList.css" })
	// @Override
	// public Style cellListStyle();
	// }

	private static ExperimentsPanelUiBinder uiBinder = GWT
			.create(ExperimentsPanelUiBinder.class);
	@UiField(provided = true)
	CellList<ExperimentData> cellList = new CellList<ExperimentData>(
			new AbstractCell<ExperimentData>() {

				@Override
				public void render(Context context, ExperimentData value,
						SafeHtmlBuilder sb) {
					// TODO
					sb.appendEscaped(value.getName());
					//sb.appendEscaped("TEST " + value.hashCode());
				}

				public Set<String> getConsumedEvents() {
					return Collections.singleton("contextmenu");
				}

				public void onBrowserEvent(
						com.google.gwt.cell.client.Cell.Context context,
						Element parent,
						ExperimentData value,
						com.google.gwt.dom.client.NativeEvent event,
						com.google.gwt.cell.client.ValueUpdater<ExperimentData> valueUpdater) {
					event.preventDefault();
					event.stopPropagation();
					Element element = (Element) event.getEventTarget().cast();
					int index = Integer.valueOf(element.getAttribute("__idx"));
					ExperimentData selected = cellList.getVisibleItems().get(
							index);
					cellList.getSelectionModel().setSelected(selected, true);
					if (selected != null) {
						currentSelection = selected;
					}
					openMenu((Event) event);

				};
			}/* , GWT.<MyCellListResources> create(MyCellListResources.class) */);

	interface ExperimentsPanelUiBinder extends
			UiBinder<Widget, ExperimentsPanel> {
	}
	
	
	private void initializeXMLViewWindow(){
		xmlWindow = new XMLViewWindow(xmlPopup);
		xmlPopup.add(xmlWindow);
		xmlPopup.setModal(true);
		xmlPopup.setGlassEnabled(true);
		xmlPopup.setAutoHideEnabled(false);
		
	}
	
	private void openXMLViewWindow(ExperimentData data){
		int height = Window.getClientHeight()-150;
		xmlPopup.setHeight(height + "px");
		int width = Window.getClientWidth()-150;
		xmlPopup.setWidth(width + "px");
//		xmlPopup.getg
		xmlWindow.setTitle("Viewing experiment " + data.getName());
		xmlWindow.setBody(data.getContent());
		xmlPopup.center();
	}

	public ExperimentsPanel() {
		initWidget(uiBinder.createAndBindUi(this));
		initSelectionModel();
		this.contextMenu = new PopupPanel(true);
		this.contextMenu.add(new HTML("My Context menu!"));
		this.contextMenu.hide();
		initCellList();
		initializeXMLViewWindow();
		initializeRightClickMenu();
		addHandlers();
	}

	public ExperimentsPanel(String firstName) {
		initWidget(uiBinder.createAndBindUi(this));
		initSelectionModel();
		initCellList();
		initializeXMLViewWindow();
		initializeRightClickMenu();
		addHandlers();
	}

	private void addHandlers() {
		addButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				addRowToList(new ExperimentData());
			}
		});
		clearAllButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				clearList();
			}
		});
	}
	
	private void initSelectionModel() {
		final SingleSelectionModel<ExperimentData> selectionModel = new SingleSelectionModel<ExperimentData>();
		cellList.setSelectionModel(selectionModel);
		selectionModel
				.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
					public void onSelectionChange(SelectionChangeEvent event) {
						ExperimentData selected = selectionModel
								.getSelectedObject();
						if (selected != null) {
							currentSelection = selected;
						}
					}
				});
	}

	private void initializeRightClickMenu() {
		sinkEvents(Event.ONMOUSEUP | Event.ONDBLCLICK | Event.ONCONTEXTMENU);
		this.listener = new AdvClickListener() {

			@Override
			public void onClick(ClickEvent event) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onRightClick(Widget sender, Event event) {
				openMenu(event);
			}

			@Override
			public void onClick(Widget sender, Event event) {
				// TODO Auto-generated method stub

			}
		};

		executeCmd = new Command() {

			@Override
			public void execute() {
				openXMLViewWindow(currentSelection);
				popupPanel.hide();

			}
		};

		deleteCmd = new Command() {

			@Override
			public void execute() {
				LinkedList<ExperimentData> tmp = new LinkedList<ExperimentData>(cellList.getVisibleItems());
				tmp.remove(currentSelection);
				cellList.setRowData(tmp);
				popupPanel.hide();

			}
		};

		MenuBar popupMenuBar = new MenuBar(true);
		MenuItem menuItem1 = new MenuItem("Execute", true, executeCmd);
		MenuItem menuItem2 = new MenuItem("Delete", true, deleteCmd);

		popupPanel.setStyleName("popup");
		menuItem1.addStyleName("popup-item");
		menuItem2.addStyleName("popup-item");
		

		popupMenuBar.addItem(menuItem1);
		popupMenuBar.addItem(menuItem2);

		popupMenuBar.setVisible(true);
		popupPanel.add(popupMenuBar);
	}

	private void openMenu(Event event) {
		@SuppressWarnings("unchecked")
		SingleSelectionModel<ExperimentData> selectionModel = ((SingleSelectionModel<ExperimentData>) cellList
				.getSelectionModel());
		ExperimentData selected = selectionModel.getSelectedObject();
		int index = cellList.getVisibleItems().indexOf(selected);

		if (selected != null) {
			Element element = cellList.getRowElement(index);

			int top = element.getAbsoluteTop();
			int bottom = element.getAbsoluteBottom();
			int left = element.getAbsoluteLeft() - cellList.getAbsoluteLeft();
			int right = element.getAbsoluteRight();

			int x = DOM.eventGetClientX(event);
			int y = DOM.eventGetClientY(event);


			if (x < right && x > left && y < bottom && y > top) {
				popupPanel.setPopupPosition(x, y);
				popupPanel.show();
			}
		}
	}

	public interface AdvClickListener extends ClickHandler {
		void onClick(Widget sender, Event event);

		void onRightClick(Widget sender, Event event);
	}

	@Override
	public void onBrowserEvent(Event event) {
		GWT.log("onBrowserEvent", null);
		event.stopPropagation();
		event.preventDefault();
		switch (DOM.eventGetType(event)) {
		case Event.ONMOUSEUP:
			if (DOM.eventGetButton(event) == Event.BUTTON_LEFT) {
				GWT.log("Event.BUTTON_LEFT", null);
				listener.onClick(this, event);
			}

			if (DOM.eventGetButton(event) == Event.BUTTON_RIGHT) {
				GWT.log("Event.BUTTON_RIGHT", null);
				listener.onRightClick(this, event);
			}
			break;
		case Event.ONDBLCLICK:
			GWT.log("Event.ONCONTEXTMENU", null);
			break;

		case Event.ONCONTEXTMENU:
			GWT.log("Event.ONCONTEXTMENU", null);
			break;

		default:
			break; // Do nothing
		}// end switch
	}

	public void initCellList() {

		CPUClient.service.listExperiments(new AsyncCallback<List<ExperimentData>>() {
			
			@Override
			public void onSuccess(List<ExperimentData> result) {
				cellList.setRowData(result);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("there was an error loading the experiment list - " + caught.getMessage());
				
			}
		});
	}

	private void addRowToList(ExperimentData toAdd) {
		List<ExperimentData> temp = new LinkedList<ExperimentData>();
		int beforeCount = cellList.getRowCount();
		int afterCount = beforeCount + 1;
		temp.add(toAdd);
		cellList.setVisibleRange(0, afterCount);
		cellList.setRowData(beforeCount, temp);
		cellList.setRowCount(afterCount);
	}

	private void clearList() {
		cellList.setRowData(new LinkedList<ExperimentData>() {
		});
	}

	@Override
	public String getText() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setText(String text) {
		// TODO Auto-generated method stub

	}

}
