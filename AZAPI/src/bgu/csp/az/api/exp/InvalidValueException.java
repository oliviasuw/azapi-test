package bgu.csp.az.api.exp;

public class InvalidValueException extends RuntimeException {

	public InvalidValueException() {
		super();
	}

	public InvalidValueException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public InvalidValueException(String arg0) {
		super(arg0);
	}

	public InvalidValueException(Throwable arg0) {
		super(arg0);
	}

}
