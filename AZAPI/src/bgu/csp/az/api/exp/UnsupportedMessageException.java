package bgu.csp.az.api.exp;

public class UnsupportedMessageException extends RuntimeException{

	public UnsupportedMessageException() {
		super();
	}

	public UnsupportedMessageException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnsupportedMessageException(String message) {
		super(message);
	}

	public UnsupportedMessageException(Throwable cause) {
		super(cause);
	}
	
}
