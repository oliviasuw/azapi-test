package bgu.dcr.az.cpu.client.scr;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import bgu.dcr.az.cpu.client.wgt.AzButtonWithImg;
import bgu.dcr.az.cpu.shared.AlgorithmData;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
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

public class MachineInfoPanel extends Composite implements HasText {

	@UiField
	SpanElement machineInfo;
	
	// public interface MyNoSelectionCellListResources extends
	// CellList.Resources {
	// @Source({"css/NoSelectionCellList.css"})
	// @Override
	// public Style cellListStyle();
	// }

	private static AlgorithmsPanelUiBinder uiBinder = GWT
			.create(AlgorithmsPanelUiBinder.class);
	
	interface AlgorithmsPanelUiBinder extends UiBinder<Widget, MachineInfoPanel> {
	}

	public MachineInfoPanel() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public MachineInfoPanel(String firstName) {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public String getText() {
		return machineInfo.getInnerText();
	}

	@Override
	public void setText(String text) {
		machineInfo.setInnerText(text);

	}

}
