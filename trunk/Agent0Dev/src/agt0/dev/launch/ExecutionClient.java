package agt0.dev.launch;

import static agt0.dev.util.JavaUtils.log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.debug.core.ILaunch;

//import bgu.csp.sim.api.infra.Event;

import agt0.dev.util.conc.StreamGobbler;
import agt0.dev.util.json.Json;
import agt0.dev.util.json.Json.Type;
import agt0.dev.util.json.JsonTokenizer;
import agt0.dev.util.json.JsonTokenizer.ITokenListener;

import static agt0.dev.util.JavaUtils.*;

public class ExecutionClient implements Runnable {

	private static final byte[] TAKE_MESSAGE = "take\n".getBytes();
	private static final int UPDATE_PERIOD_MILIS = 1000;
	private int port;
	private ILaunch launch;
	Socket cs = null;

	public ExecutionClient(int port, ILaunch launch) {
		this.port = port;
		this.launch = launch;
	}

	@Override
	public void run() {
		try {

			System.out.println("trying to connect");
			while (true) {
				try {
					cs = new Socket("localhost", port);
					System.out.println("connected!");
					break;
				} catch (Exception ex) {

					if (launch.isTerminated()) {
						System.out.println("launch is terminated - exiting! ");
						return;
					}

					System.out.println("event server not responding retrying");
					Thread.sleep(250);
				}

			}

			InputStream in = cs.getInputStream();
			OutputStream out = cs.getOutputStream();

			// StreamGobbler gobbler = new StreamGobbler(in, System.out);
			// gobbler.start();
			JsonTokenizer tok = new JsonTokenizer(in, new ITokenListener() {

				@Override
				public void onToken(Json token) {
					if (token.getType() == Json.Type.ARRAY) {
						List<Json> events = token.getArray();
						for (Json event : events) {
							if (event.getType() == Type.MAP) {
								println("received event: "
										+ event.toString());
//								Launcher.EventManager.INSTANCE
//										.fire(parseEvent(event));
							} else {
								println("received event with stracture different then a map - throwing..");
							}
						}
					} else if (token.getType() == Type.VALUE
							&& token.getValue().equals("BYE")) {
						try {
							cs.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else {
						println("received event list with stracture different then a array - throwing..");
					}
				}
			});
			tok.start();

			while (true) {
				Thread.sleep(UPDATE_PERIOD_MILIS);
				out.write(TAKE_MESSAGE);
			}

		} catch (Exception e) {
			// log(e);
			System.out.println("Execution Client Done!");

		} finally {
			/*
			 * if (cs != null) try { cs.close(); } catch (IOException e) { //
			 * TODO Auto-generated catch block e.printStackTrace(); }
			 */
		}

	}

//	private Event parseEvent(Json event) {
//		Map<String, Json> map = event.getMap();
//		HashMap<String, String> pmap = new HashMap<String, String>();
//		for (Entry<String, Json> p : map.entrySet()){
//			if (!p.getKey().equals("event")){
//				pmap.put(p.getKey(), p.getValue().getValue());
//			}
//		}
//		return new Event(map.get("event").getValue(), pmap);
//	}

}
