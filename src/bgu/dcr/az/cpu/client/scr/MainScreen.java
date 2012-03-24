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
import com.google.gwt.user.client.ui.SimplePanel;

public class MainScreen extends Composite {

	private static MainScreenUiBinder uiBinder = GWT
			.create(MainScreenUiBinder.class);
	@UiField SimplePanel content;

	interface MainScreenUiBinder extends UiBinder<Widget, MainScreen> {
	}

	public MainScreen() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void setContent(Widget content){
		this.content.clear();
		this.content.add(content);
	}
	
}
