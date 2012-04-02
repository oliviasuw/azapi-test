package bgu.dcr.az.cpu.client.wgt;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;

public class ProgressBar extends Composite {

	private Object jqlookup;
	int value = 0;

	private static ProgressBarUiBinder uiBinder = GWT
			.create(ProgressBarUiBinder.class);

	interface ProgressBarUiBinder extends UiBinder<Widget, ProgressBar> {
	}

	public ProgressBar() {
		initWidget(uiBinder.createAndBindUi(this));
		getElement().setId(DOM.createUniqueId());

	}

	@Override
	protected void onLoad() {
		super.onLoad();
		jqlookup = getJQueryLookup(getElement().getId());
		transformToProgressbar(jqlookup);
		getElement().removeClassName("ui-corner-all");
		getElement().getFirstChildElement().removeClassName("ui-corner-left");
		setValue(value);
	}

	public void setValue(int value) {
		if (jqlookup != null)
			setValue(jqlookup, value);
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	/**
	 * @param jql
	 *            - jquery lookup
	 */
	private static native void transformToProgressbar(Object jql)/*-{
		jql.progressbar();
	}-*/;

	private static native void call(Object jql, String option)/*-{
		jql.progressbar(option);
	}-*/;

	private static native void setValue(Object jql, int value)/*-{
		jql.progressbar("value", value);
	}-*/;

	public static native Object getJQueryLookup(String id)/*-{
		var $ = $wnd.jQuery;
		return $("#" + id);
	}-*/;
}
