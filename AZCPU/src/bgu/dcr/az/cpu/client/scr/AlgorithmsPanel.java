package bgu.dcr.az.cpu.client.scr;

import java.util.LinkedList;
import java.util.List;

import bgu.dcr.az.cpu.shared.AlgorithmData;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
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

	
//	public interface MyNoSelectionCellListResources extends CellList.Resources {
//		@Source({"css/NoSelectionCellList.css"})
//		@Override
//		public Style cellListStyle();
//	}

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
			}/*, GWT.<MyNoSelectionCellListResources> create(MyNoSelectionCellListResources.class)*/);

	interface AlgorithmsPanelUiBinder extends UiBinder<Widget, AlgorithmsPanel> {
	}

	public AlgorithmsPanel() {
		initWidget(uiBinder.createAndBindUi(this));
		initCellList();
	}

	public AlgorithmsPanel(String firstName) {
		initWidget(uiBinder.createAndBindUi(this));
		initCellList();
	}

	public void initCellList() {

		// this.cellList.setStyleName("css/cellList.css");
		List<AlgorithmData> temp = new LinkedList<AlgorithmData>();
		for (int i = 0; i < 100; i++) {
			temp.add(new AlgorithmData("SBB" + i, "this is the path"));
		}

		// cellList.addCellPreviewHandler(DefaultSelectionEventManager
		// .createCustomManager(new EventTranslator<AlgorithmData>() {
		//
		// @Override
		// public boolean clearCurrentSelection(
		// CellPreviewEvent<AlgorithmData> event) {
		// // TODO Auto-generated method stub
		// return false;
		// }
		//
		// @Override
		// public SelectAction translateSelectionEvent(
		// CellPreviewEvent<AlgorithmData> event) {
		// // TODO Auto-generated method stub
		// return SelectAction.IGNORE;
		// }
		// }));
		// cellList.setStyleName("list");
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
