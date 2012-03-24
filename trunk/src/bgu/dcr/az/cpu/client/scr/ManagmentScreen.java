package bgu.dcr.az.cpu.client.scr;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;

public class ManagmentScreen extends Composite {

	private static Home2ScreenUiBinder uiBinder = GWT
			.create(Home2ScreenUiBinder.class);

	interface Home2ScreenUiBinder extends UiBinder<Widget, ManagmentScreen> {
	}

	public ManagmentScreen() {
		initWidget(uiBinder.createAndBindUi(this));
	}

}
