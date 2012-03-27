package bgu.dcr.az.cpu.client.wgt;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;

public class AzButton extends Composite implements HasText {

	@UiField
	SpanElement text;
	@UiField
	ImageElement image;
	@UiField
	Button button;

	private static AzButtonUiBinder uiBinder = GWT
			.create(AzButtonUiBinder.class);

	interface AzButtonUiBinder extends UiBinder<Widget, AzButton> {
	}

	public AzButton() {
		
		initWidget(uiBinder.createAndBindUi(this));
	}

	public HandlerRegistration addClickHandler(ClickHandler handler) {
		System.out.println("BLU BLU");
		return addHandler(handler, ClickEvent.getType());
	}

	public void onClick(ClickEvent event) {
		System.out.println("BLA BLA");
		this.fireEvent(event);
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

}
