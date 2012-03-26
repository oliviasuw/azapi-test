package bgu.dcr.az.cpu.client;

import java.util.List;

import bgu.dcr.az.cpu.shared.AlgorithmData;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

public interface CPUServiceAsync {

	void listAlgorithms(AsyncCallback<List<AlgorithmData>> callback);
}
