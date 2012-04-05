package bgu.dcr.az.cpu.client;

import gwtupload.client.SingleUploader;
import gwtupload.client.IFileInput.FileInputType;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

import bgu.dcr.az.cpu.client.scr.MainScreen;
import bgu.dcr.az.cpu.client.scr.ManagementScreen;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class CPUClient implements EntryPoint {

	private static CPUClient instance;
	public static final CPUServiceAsync service = GWT.create(CPUService.class);
	
	private SimpleEventBus eventBus;
	
	public static CPUClient get() {
		return instance;
	}
	
		
	@Override
	public void onModuleLoad() {
		instance = this;
		eventBus = new SimpleEventBus();
		
		MainScreen screen = new MainScreen();
		makeFullPage(screen);
//		RootPanel.get().add(screen);
//		
		SingleUploader su = new SingleUploader(FileInputType.LABEL);
		su.setTitle("upload");
		RootPanel.get().add(su);
		
	}
	
	private void makeFullPage(final Widget w) {
		w.setWidth("100%");
		w.setHeight(Window.getClientHeight() + "px");
		Window.addResizeHandler(new ResizeHandler() {
			
			@Override
			public void onResize(ResizeEvent event) {
				int height = event.getHeight();
			    w.setHeight(height + "px");
			}
		});
	}

}
