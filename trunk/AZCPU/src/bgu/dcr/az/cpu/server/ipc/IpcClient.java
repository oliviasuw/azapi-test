package bgu.dcr.az.cpu.server.ipc;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedList;

import bgu.dcr.az.cpu.server.exp.UncheckedIOException;

public class IpcClient implements Runnable{
	
	private int port = 7000;
	private boolean connected = false;
	private boolean running = false;
	private ObjectInputStream serverIn;
	private ObjectOutputStream serverOut;
	private Socket sock;
	private LinkedList<ClientListener> listeners = new LinkedList<ClientListener>();
	
	private static IpcClient client = null;
	
	/**
	 * @return the default client instance - notice that you must call {@link start} first or you will get a null pointer.
	 */
	public static IpcClient get(){
		return client;
	}
	
	/**
	 * starts new client thread and attach to it the given listener
	 * @param listener
	 */
	public static void start(ClientListener listener){
		if (client != null) return;
		client = new IpcClient();
		client.addClientListener(listener);
		new Thread(client).start();
	}
	
	/**
	 * 
	 * @return the port that this client connected to
	 */
	public int getConnectedToPort(){
		return port;
	}
	
	/**
	 * @return true if this client is connected to the server
	 */
	public boolean isConnected(){
		return connected;
	}
	
	/**
	 * @return true if this client thread is running
	 */
	public boolean isRunning(){
		return running;
	}
	
	/**
	 * send the given message to the server 
	 * @param message
	 * @throws NotConnectedToServerException
	 * @throws {@link UncheckedIOException}
	 */
	public void send(IpcMessage message) throws NotConnectedToServerException{
		if (!isConnected()) throw new NotConnectedToServerException("you are not connected to the ipc-server");
		try {
			serverOut.writeObject(message);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
	/**
	 * add a client listener
	 * @param listener
	 */
	public void addClientListener(ClientListener listener){
		listeners.add(listener);
	}
	
	/**
	 * remove a client listener
	 * @param listener
	 */
	public void removeClientListener(ClientListener listener){
		listeners.remove(listener);
	}
	
	@Override
	public void run() {
		try{
			
			running = true;
			sock = new Socket("localhost", port);
			for (ClientListener l : listeners) l.onConnectedToServer(this);
			
			serverIn = new ObjectInputStream(sock.getInputStream());
			serverOut = new ObjectOutputStream(sock.getOutputStream());
			
			while (!Thread.currentThread().isInterrupted()){
				IpcMessage msg = (IpcMessage) serverIn.readObject();
				for (ClientListener l : listeners){
					IpcMessage res = l.onServerRequest(this, msg);
					if (res != null){
						serverOut.writeObject(res);
					}
				}
			}
			
		} catch (Exception e) {
			if (sock != null){
				try {
					sock.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
			for (ClientListener l : listeners) l.onServerDisconnect(this); 
		} finally{
			connected = false;
			running = false;
		}
	}
	
	public static interface ClientListener{
		public IpcMessage onServerRequest(IpcClient client, IpcMessage request);
		public void onServerDisconnect(IpcClient client);
		public void onConnectedToServer(IpcClient client);
	}

	public static class NotConnectedToServerException extends RuntimeException{

		public NotConnectedToServerException() {
			super();
		}

		public NotConnectedToServerException(String message, Throwable cause,
				boolean enableSuppression, boolean writableStackTrace) {
			super(message, cause, enableSuppression, writableStackTrace);
		}

		public NotConnectedToServerException(String message, Throwable cause) {
			super(message, cause);
		}

		public NotConnectedToServerException(String message) {
			super(message);
		}

		public NotConnectedToServerException(Throwable cause) {
			super(cause);
		}
		
	}
	
}
