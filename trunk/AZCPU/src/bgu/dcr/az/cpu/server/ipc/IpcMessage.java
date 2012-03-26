package bgu.dcr.az.cpu.server.ipc;

public interface IpcMessage {

	public static class TerminationMessage implements IpcMessage{
	}

	public static class HelloMessage implements IpcMessage{
		public String clientId;
	}
}
