package bgu.dcr.az.cpu.client.scr;

import bgu.dcr.az.cpu.client.wgt.AzButton;
import bgu.dcr.az.cpu.shared.ExperimentData;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
	
	ManagementScreen mngScreen;
	ExecutionsScreen excScreen;
	
	@UiField 
	SimplePanel content;
	@UiField
	AzButton leftButton;
	@UiField
	AzButton rightButton;
	

	interface MainScreenUiBinder extends UiBinder<Widget, MainScreen> {
	}

	public MainScreen() {
		initWidget(uiBinder.createAndBindUi(this));
		mngScreen = new ManagementScreen();
		excScreen = new ExecutionsScreen();
		setContent(mngScreen);
		leftButton.setPushed(true);
		addHandlers();
	}

	public void setContent(Widget content){
		this.content.clear();
		this.content.add(content);
	}
	
	
	private void addHandlers() {
		leftButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (leftButton.isPushButton()){
					leftButton.setPushed(true);
				}
				if(leftButton.isPushed()){
					rightButton.setPushed(false);
					setContent(mngScreen);
				}
			}
		});
		rightButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (rightButton.isPushButton()){
					rightButton.setPushed(true);
				}
				if(rightButton.isPushed()){
					rightButton.setPushed(true);
					leftButton.setPushed(false);
					setContent(excScreen);
				}
			}
		});
	}
	
	
	
}
