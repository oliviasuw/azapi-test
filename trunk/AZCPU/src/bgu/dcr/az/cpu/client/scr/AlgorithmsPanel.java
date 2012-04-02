package bgu.dcr.az.cpu.client.scr;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import bgu.dcr.az.cpu.client.wgt.AzButtonWithImg;
import bgu.dcr.az.cpu.shared.AlgorithmData;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.CellList.Resources;
import com.google.gwt.user.cellview.client.CellList.Style;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;

public class AlgorithmsPanel extends Composite implements HasText {

	@UiField
	AzButtonWithImg addButton;
	@UiField
	AzButtonWithImg clearAllButton;

	// public interface MyNoSelectionCellListResources extends
	// CellList.Resources {
	// @Source({"css/NoSelectionCellList.css"})
	// @Override
	// public Style cellListStyle();
	// }

	private static AlgorithmsPanelUiBinder uiBinder = GWT
			.create(AlgorithmsPanelUiBinder.class);
	@UiField(provided = true)
	CellList<AlgorithmData> cellList = new CellList<AlgorithmData>(
			new AbstractCell<AlgorithmData>() {
				@Override
				public void render(Context context, AlgorithmData value,
						SafeHtmlBuilder sb) {
					// TODO
					sb.appendEscaped(value.getName() + " @ " + value.getPath());
				}
			}/*
			 * , GWT.<MyNoSelectionCellListResources>
			 * create(MyNoSelectionCellListResources.class)
			 */);

	interface AlgorithmsPanelUiBinder extends UiBinder<Widget, AlgorithmsPanel> {
	}

	public AlgorithmsPanel() {
		initWidget(uiBinder.createAndBindUi(this));
		initCellList();
		addHandlers();
	}

	private void addHandlers() {
		addButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				addRowToList(new AlgorithmData("new Name", "newpath"));
			}
		});
		clearAllButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				clearList();
			}
		});
	}

	public AlgorithmsPanel(String firstName) {
		initWidget(uiBinder.createAndBindUi(this));
		initCellList();
	}

	public void initCellList() {

		List<AlgorithmData> temp = new LinkedList<AlgorithmData>();
		for (int i = 0; i < 100; i++) {
			temp.add(new AlgorithmData("SBB" + i, "this is the path"));
		}
		cellList.setRowData(temp);
	}

	private void addRowToList(AlgorithmData toAdd) {
		List<AlgorithmData> temp = new LinkedList<AlgorithmData>();
		int beforeCount = cellList.getRowCount();
		int afterCount = beforeCount + 1;
		temp.add(toAdd);
		cellList.setVisibleRange(0, afterCount);
		cellList.setRowData(beforeCount, temp);
		cellList.setRowCount(afterCount);
	}

	private void clearList() {
		cellList.setRowData(new LinkedList<AlgorithmData>() {
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
