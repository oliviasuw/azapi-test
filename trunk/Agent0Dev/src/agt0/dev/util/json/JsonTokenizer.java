package agt0.dev.util.json;

import static agt0.dev.util.JavaUtils.println;

import java.io.IOException;
import java.io.InputStream;

import agt0.dev.util.json.Json.JsonParseException;

public class JsonTokenizer extends Thread {
	InputStream in;
	StringBuilder token;
	ITokenListener listener;

	public JsonTokenizer(InputStream in, ITokenListener listener) {
		this.in = in;
		this.listener = listener;

		resetToken();
	}

	private void resetToken() {
		token = new StringBuilder();
	}

	public static interface ITokenListener {
		void onToken(Json token);
	}

	@Override
	public void run() {
		try {
			while (!this.isInterrupted()) {
				readToken();
				Json jtok;
				try {
					jtok = new Json(token.toString());
					listener.onToken(jtok);
				} catch (JsonParseException e) {
					println("token " + token
							+ " is not a valid json - throwing it (" + e.getMessage() + ")");
					
				}
				resetToken();
			}
		} catch (IOException e1) {
			println("json tokenizer closing.");
		}
	}

	private void readToken() throws IOException {
		int r = in.read();
		if (r < 0)
			throw new IOException("Connection Closed");
		switch (r) {
		case ' ':
			break;
		case '{':
			token.append((char) r);
			readBalanced('{', '}', -1);
			break;
		case '[':
			token.append((char) r);
			readBalanced('[', ']', -1);
			break;
		case '"':
			token.append((char) r);
			readBalanced('"', '"', '\\');
			break;
		default:
			println("tokenizer throws '" + (char) r
					+ "' as it not a part of a json element");
			break;
		}
	}

	private void readBalanced(final int open, final int close, int escape)
			throws IOException {
		boolean bescape = false;
		int balance = 1;
		while (!isInterrupted() && balance > 0) {
			int r = in.read();
			if (r < 0)
				throw new IOException("Connection Closed");
			token.append((char) r);

			if (r == close) {
				if (!bescape)
					balance--;
				bescape = false;
			} else if (r == open) {
				balance++;
				bescape = false;
			} else if (r == escape) {
				bescape = !bescape;
			} else {
				bescape = false;
			}
		}

	}
}
