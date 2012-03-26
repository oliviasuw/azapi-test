package bgu.dcr.az.cpu.server.ipc;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import bgu.dcr.az.cpu.server.exp.UncheckedIOException;

import com.google.gwt.thirdparty.guava.common.collect.ImmutableList;

public class IpcServer implements Runnable {

	private boolean running = false;
	private Map<String, ClientData> clients = new ConcurrentHashMap<>();
	private int port = 7000;
	private List<ServerListener> listeners = new LinkedList<>();

	private static IpcServer server = null;

	/**
	 * @return the default server - notice that you must call {@link start}
	 *         first
	 */
	public static IpcServer get() {
		return server;
	}

	/**
	 * start the default server and attach it with the given listener.
	 * 
	 * @param listener
	 */
	public static void start(ServerListener listener) {
		if (server != null)
			return;
		server = new IpcServer();
		server.addServerListener(listener);
		new Thread(server).start();
	}

	private ClientData retreiveClient(String clientId) {
		ClientData c = clients.get(clientId);
		if (c == null)
			throw new ClientNotConnectedException("client " + clientId
					+ " is not connected");
		return c;
	}

	/**
	 * send the given message to the given client - not waiting for it to
	 * response (the response will received by the server listener)
	 * 
	 * @param msg
	 * @param clientId
	 * @throws ClientNotConnectedException
	 */
	public void send(IpcMessage msg, String clientId)
			throws ClientNotConnectedException {
		ClientData c = retreiveClient(clientId);
		try {
			c.out.writeObject(msg);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	/**
	 * @return true if the server is running
	 */
	public boolean isServerRunning() {
		return running;
	}

	/**
	 * @return the port that this server is listening upon
	 */
	public int getListeningPort() {
		return port;
	}

	/**
	 * @return immutable view of the connected clients
	 */
	public ImmutableList<String> getClientList() {
		return ImmutableList.copyOf(clients.keySet());
	}

	/**
	 * send termination message to the given client
	 * 
	 * @param clientId
	 * @throws ClientNotConnectedException
	 */
	public void terminateClient(String clientId)
			throws ClientNotConnectedException {
		send(new IpcMessage.TerminationMessage(), clientId);
	}

	/**
	 * @param clientId
	 * @return true if there is a client connected with the given name
	 */
	public boolean isClientConnected(String clientId) {
		return clients.containsKey(clientId);
	}

	/**
	 * add a server listener
	 * 
	 * @param listener
	 */
	public void addServerListener(ServerListener listener) {
		listeners.add(listener);
	}

	/**
	 * remove the given listener
	 * 
	 * @param listener
	 */
	public void removeServerListener(ServerListener listener) {
		listeners.add(listener);
	}

	@Override
	public void run() {
		try {
			running = true;
			for (ServerListener s : listeners)
				s.onServerStart(this);

			ServerSocket ss = new ServerSocket(port);
			while (!Thread.currentThread().isInterrupted()) {
				Socket s = ss.accept();
				handleNewClient(s);
			}

		} catch (IOException e) {
			for (ServerListener s : listeners)
				s.onServerDie(this);
		} finally {
			running = false;
		}
	}

	private void handleNewClient(final Socket s) {
		new Thread() {
			@Override
			public void run() {
				ClientData cdata = null;
				try {
					// first wait for client hello
					cdata = new ClientData(s, "???");
					IpcMessage.HelloMessage message = (IpcMessage.HelloMessage) cdata.in
							.readObject();
					cdata.clientId = message.clientId;
					clients.put(cdata.clientId, cdata);

					// notify client entrance
					for (ServerListener l : listeners)
						l.onNewClient(IpcServer.this, cdata.clientId);

					while (!Thread.currentThread().isInterrupted()) {
						IpcMessage msg = (IpcMessage) cdata.in.readObject();
						for (ServerListener l : listeners) {
							IpcMessage reply = l.onClientResponse(
									IpcServer.this, cdata.clientId, msg);
							if (reply != null) {
								cdata.out.writeObject(reply);
							}
						}
					}

				} catch (Exception e) {
					System.out.println("killing client "
							+ (cdata != null ? cdata.clientId : "???")
							+ " because of an error : " + e.getMessage() + " ("
							+ e.getClass().getSimpleName() + ")");

					// notify client disconnected
					if (cdata != null)
						for (ServerListener l : listeners)
							l.onClientDisconnected(IpcServer.this,
									cdata.clientId);

					// killing the actual connection
					try {
						s.close();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		}.start();

	}

	public static interface ServerListener {
		void onNewClient(IpcServer server, String clientId);

		void onClientDisconnected(IpcServer server, String clientId);

		IpcMessage onClientResponse(IpcServer server, String clientId,
				IpcMessage response);

		void onServerDie(IpcServer server);

		void onServerStart(IpcServer server);
	}

	public static abstract class ServerHandler implements ServerListener {
		@Override
		public void onClientDisconnected(IpcServer server, String clientId) {
			// TODO Auto-generated method stub

		}

		@Override
		public IpcMessage onClientResponse(IpcServer server, String clientId,
				IpcMessage response) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void onNewClient(IpcServer server, String clientId) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onServerDie(IpcServer server) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onServerStart(IpcServer server) {
			// TODO Auto-generated method stub

		}
	}

	public static class ClientNotConnectedException extends RuntimeException {

		public ClientNotConnectedException() {
			super();
			// TODO Auto-generated constructor stub
		}

		public ClientNotConnectedException(String arg0, Throwable arg1,
				boolean arg2, boolean arg3) {
			super(arg0, arg1, arg2, arg3);
			// TODO Auto-generated constructor stub
		}

		public ClientNotConnectedException(String arg0, Throwable arg1) {
			super(arg0, arg1);
			// TODO Auto-generated constructor stub
		}

		public ClientNotConnectedException(String arg0) {
			super(arg0);
			// TODO Auto-generated constructor stub
		}

		public ClientNotConnectedException(Throwable arg0) {
			super(arg0);
			// TODO Auto-generated constructor stub
		}
	}

	private static class ClientData {
		Socket sock;
		ObjectInputStream in;
		ObjectOutputStream out;
		String clientId;

		public ClientData(Socket sock, String id) throws IOException {
			this.sock = sock;
			this.out = new ObjectOutputStream(sock.getOutputStream());
			this.in = new ObjectInputStream(sock.getInputStream());
			this.clientId = id;
		}
	}

}
