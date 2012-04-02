package bgu.dcr.az.cpu.client.scr;

import bgu.dcr.az.cpu.client.wgt.AzButtonWithImg;

import com.gargoylesoftware.htmlunit.javascript.host.Window;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class XMLViewWindow extends Composite {

	PopupPanel popup;
	@UiField
	AzButtonWithImg cancelButton;
	@UiField
	AzButtonWithImg executeButton;
	@UiField
	SpanElement title;
	@UiField
	SpanElement body;

	private static XMLViewWindowUiBinder uiBinder = GWT
			.create(XMLViewWindowUiBinder.class);

	interface XMLViewWindowUiBinder extends UiBinder<Widget, XMLViewWindow> {
	}

	public XMLViewWindow() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public XMLViewWindow(PopupPanel xmlPopup) {
		initWidget(uiBinder.createAndBindUi(this));
		popup = xmlPopup;
		cancelButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				popup.hide();

			}
		});
		
		executeButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				com.google.gwt.user.client.Window.alert("Executing");
			}
		});

	}

	public String getTitle() {
		// TODO Auto-generated method stub
		return title.getInnerText();
	}

	public void setTitle(String t) {
		title.setInnerText(t);

	}

	public String getBody() {
		// TODO Auto-generated method stub
		return body.getInnerText();
	}

	public void setBody(String b) {
		body.setInnerText(b);

	}

}
