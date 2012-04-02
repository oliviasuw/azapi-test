package bgu.dcr.az.cpu.client.wgt;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.ButtonElement;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;

public class AzButtonWithImg extends Composite implements HasText, HasClickHandlers {

	@UiField
	SpanElement text;
	@UiField
	ImageElement image;
	@UiField
	ButtonElement button;

	
	private static AzButtonUiBinder uiBinder = GWT
			.create(AzButtonUiBinder.class);

	interface AzButtonUiBinder extends UiBinder<Widget, AzButtonWithImg> {
	}

	public AzButtonWithImg() {		
		initWidget(uiBinder.createAndBindUi(this));
		
	}
	
	@Override
	public String getText() {
		return text.getInnerText();
	}

	@Override
	public void setText(String t) {
		text.setInnerText(t);
	}

	public void setSrc(String src) {
		image.setSrc(src);
	}

	public String getSrc() {
		return image.getSrc();
	}


	@Override
	public HandlerRegistration addClickHandler(ClickHandler handler) {
		return addDomHandler(handler, ClickEvent.getType());
	}
	
}
