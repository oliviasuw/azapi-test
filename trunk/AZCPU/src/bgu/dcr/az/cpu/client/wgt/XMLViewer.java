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

public class XMLViewer extends Composite {

	private static XMLViewerUiBinder uiBinder = GWT
			.create(XMLViewerUiBinder.class);

	interface XMLViewerUiBinder extends UiBinder<Widget, XMLViewer> {
	}

	Object editor = null;
	String value = "";

	public XMLViewer() {
		initWidget(uiBinder.createAndBindUi(this));
		getElement().setId(DOM.createUniqueId());
	}

	@Override
	protected void onLoad() {
		if (editor == null) editor = buildEditor(getElement().getId());
		setXML(value);
		super.onLoad();
	}
	
	public void setXML(String xml) {
		if (editor != null){
			setXML(editor, xml);
		}
		
		value = xml;
	}

	@Override
	public void setHeight(String height) {
		//System.out.println("syncing heights to " + editor);
		super.setHeight(height);
		syncHeight(getElement().getId(), height);
		//syncHeight(editor, "" + this.getOffsetHeight() + "px");
	}
	
	
	public String getXML() {
		return value;
	}

	private static native Object buildEditor(String id) /*-{
		var myCodeMirror = $wnd.CodeMirror($doc.getElementById(id), {
			mode : "xml", 
			theme : "xq-dark"
		});
		
		return myCodeMirror
	}-*/;
	
	private static native void setXML(Object editor, String text)/*-{
		editor.setValue(text);
	}-*/;
	
	private static native void syncHeight(String id, String height) /*-{
		var $ = $wnd.jQuery;
		$("#" + id).children(".CodeMirror").children(".CodeMirror-scroll").css("height", height);
	}-*/;

}
