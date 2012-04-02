package bgu.dcr.az.cpu.client.scr;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class ExecutionInfoPanel extends Composite {

	private static ExecutionInfoUiBinder uiBinder = GWT
			.create(ExecutionInfoUiBinder.class);

	interface ExecutionInfoUiBinder extends UiBinder<Widget, ExecutionInfoPanel> {
	}

	public ExecutionInfoPanel() {
		initWidget(uiBinder.createAndBindUi(this));
	}

}
