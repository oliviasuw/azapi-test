package bgu.dcr.az.cpu.server.ipc;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.google.gwt.thirdparty.guava.common.collect.ImmutableList;

public class IpcServer implements Runnable {

	private boolean running = false;
	private Map<String, > clients = new LinkedList<>();
	private ReentrantReadWriteLock clientsLock = new ReentrantReadWriteLock();

	private void validate(String clientId) {
		try {
			clientsLock.readLock().lock();
			if (!clients.contains(clientId))
				throw new ClientNotConnectedException();
		} finally {
			clientsLock.readLock().unlock();
		}
	}

	/**
	 * send the given message to the given client - not waiting for it to
	 * response (the response will received by the server listener)
	 * 
	 * @param msg
	 * @param clientId
	 * @throws ClientNotConnectedException
	 */
	public void send(IpcMessage msg, String clientId){
		validate(clientId);

	}

	/**
	 * @return true if the server is running
	 */
	public boolean isServerRunning() {
		return false;
	}

	/**
	 * @return the port that this server is listening upon
	 */
	public int getListeningPort() {
		return -1;
	}

	/**
	 * @return immutable view of the connected clients
	 */
	public ImmutableList<String> getClientList() {
		return null;
	}

	/**
	 * same as {@link send} but also waiting for the client to response, this
	 * method will block until the response will be received - notice that if
	 * there is a server listener activated he will also be notified about the
	 * response
	 * 
	 * @param msg
	 * @param clientId
	 * @throws ClientNotConnectedException
	 * @return
	 */
	public IpcMessage sendAndWait(IpcMessage msg, String clientId)
			throws ClientNotConnectedException {
		return null;
	}

	/**
	 * block until the given client will send a message
	 * 
	 * @param clientId
	 * @return
	 * @throws ClientNotConnectedException
	 */
	public IpcMessage nextMessageFrom(String clientId)
			throws ClientNotConnectedException {
		return null;
	}

	/**
	 * send termination message to the given client
	 * 
	 * @param clientId
	 * @throws ClientNotConnectedException
	 */
	public void terminateClient(String clientId)
			throws ClientNotConnectedException {

	}

	/**
	 * @param clientId
	 * @return true if there is a client connected with the given name
	 */
	public boolean isClientConnected(String clientId) {
		return false;
	}

	/**
	 * add a server listener
	 * 
	 * @param listener
	 */
	public void addServerListener(ServerListener listener) {

	}

	/**
	 * remove the given listener
	 * 
	 * @param listener
	 */
	public void removeServerListener(ServerListener listener) {

	}

	public static interface ServerListener {
		void onNewClient(IpcServer server, String clientId);

		void onClientDisconnected(IpcServer server, String clientId);

		void onClientResponse(IpcServer server, String clientId,
				IpcMessage response);

		void onServerDie(IpcServer server);

		void onServerStart(IpcServer server);
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
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
	
	private static class ClientData{
		Socket sock;
		ObjectInputStream in;
		ObjectOutputStream out;
		
		public ClientData(Socket sock) {
			this.sock = sock;
			this.out = new ObjectOutputStream(sock.getOutputStream());
			this.in = new ObjectInputStream(sock.getInputStream());
		}
		
	}
}
