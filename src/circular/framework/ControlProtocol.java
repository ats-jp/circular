package circular.framework;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * {@link ControlServer}と任意のクライアント間のプロトコルを定義したクラスです。
 * 
 * @author 千葉 哲嗣
 * @version $Name:  $
 */
public class ControlProtocol {

	/**
	 * 「開始要求」の値 '0'
	 */
	public static final char START_VALUE = '0';

	/**
	 * 「停止要求」の値 '1'
	 */
	public static final char STOP_VALUE = '1';

	/**
	 * 「状態要求」の値 '2'
	 */
	public static final char INQUIRE_VALUE = '2';

	/**
	 * 「実行中」の値 '0'
	 */
	public static final char ON_VALUE = '0';

	/**
	 * 「停止中」の値 '1'
	 */
	public static final char OFF_VALUE = '1';

	/**
	 * 「要求不明エラー」の値 '2'
	 */
	public static final char UNKNOWN_METHOD_ERROR_VALUE = '2';

	/**
	 * 「不明なサーバエラー」の値 '3'
	 */
	public static final char UNKNOWN_SERVER_ERROR_VALUE = '3';

	/**
	 * 「開始要求」
	 * <p>
	 * {@link Circular}を開始させます。
	 */
	public static final Method START = new Method(START_VALUE, "開始要求");

	/**
	 * 「停止要求」
	 * <p>
	 * {@link Circular}を停止させます。
	 */
	public static final Method STOP = new Method(STOP_VALUE, "停止要求");

	/**
	 * 「状態要求」
	 * <p>
	 * {@link Circular}が現在、実行中か停止中かを要求します。
	 */
	public static final Method INQUIRE = new Method(INQUIRE_VALUE, "状態要求");

	/**
	 * 「実行中」
	 * <p>
	 * {@link Circular}は現在、実行中であることを表します。
	 */
	public static final Status ON = new Status(ON_VALUE, "実行中");

	/**
	 * 「停止中」
	 * <p>
	 * {@link Circular}は現在、停止中であることを表します。
	 */
	public static final Status OFF = new Status(OFF_VALUE, "停止中");

	/**
	 * 「要求不明エラー」
	 * <p>
	 * クライアントからの要求が、ContorolProtocolで定義された値ではないことを表します。
	 */
	public static final Status UNKNOWN_METHOD_ERROR = new Status(
		UNKNOWN_METHOD_ERROR_VALUE,
		"要求不明エラー");

	/**
	 * 「不明なサーバエラー」
	 * <p>
	 * サーバ側で予期しないエラーが発生していることを表します。
	 */
	public static final Status UNKNOWN_SERVER_ERROR = new Status(
		UNKNOWN_SERVER_ERROR_VALUE,
		"不明なサーバエラー");

	private final Socket socket;

	private boolean requested = false;

	/**
	 * inputをサーバからのレスポンス用、outputをサーバへのリクエスト用とするインスタンスを生成します。
	 * 
	 * @param address {@link ControlServer}のアドレス
	 * @param port {@link ControlServer}のポート
	 * @throws IOException 予期せぬIOエラーが発生した場合
	 */
	public ControlProtocol(String address, int port) throws IOException {
		try {
			socket = new Socket(InetAddress.getByName(address), port);
		} catch (UnknownHostException e) {
			throw new IllegalStateException(Configure.CONTROLSERVER_ADDRESS_KEY
				+ "で指定されている値が接続できるアドレスではありません。");
		} catch (NumberFormatException e) {
			throw new IllegalStateException(Configure.CONTROLSERVER_PORT_KEY
				+ "で指定されている値が数値ではありません。");
		}
	}

	/**
	 * {@link ControlServer}へリクエストを送信します。
	 * 
	 * @param method リクエストする指示
	 * @return 現在のサーバの状態
	 * @throws java.io.IOException 予期しない入出力例外
	 */
	public Status request(Method method) throws IOException {
		OutputStream output = socket.getOutputStream();
		output.write(method.value);
		output.flush();

		InputStream input = socket.getInputStream();
		int status = input.read();
		requested = true;
		switch (status) {
		case ON_VALUE:
			return ON;
		case OFF_VALUE:
			return OFF;
		case UNKNOWN_METHOD_ERROR_VALUE:
			return UNKNOWN_METHOD_ERROR;
		case UNKNOWN_SERVER_ERROR_VALUE:
			return UNKNOWN_SERVER_ERROR;
		default:
			throw new IllegalStateException("サーバから異常な値が送られました。Status=" + status);
		}
	}

	/**
	 * サーバが発信したエラーメッセージを返却します。
	 * <p>
	 * サーバからのレスポンスである{@link Status}が、{@link Status#isError()}=trueとなる場合、サーバ側でエラーが発生しています。その場合のみ、エラーメッセージを取得することが可能です。
	 * 
	 * @return {@link Status#isError()}=faiseの場合、""
	 * @throws IOException 予期しない入出力例外
	 */
	public String getServerError() throws IOException {
		checkRequested();
		BufferedReader reader = new BufferedReader(new InputStreamReader(
			socket.getInputStream()));
		StringBuffer buffer = new StringBuffer();
		for (String line; (line = reader.readLine()) != null;) {
			buffer.append(line);
		}
		return buffer.toString();
	}

	@Override
	protected void finalize() {
		try {
			socket.close();
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	static Method receive(InputStream input)
		throws IOException, IlleagalMethodException {
		int method = input.read();
		switch (method) {
		case START_VALUE:
			return START;
		case STOP_VALUE:
			return STOP;
		case INQUIRE_VALUE:
			return INQUIRE;
		default:
			throw new IlleagalMethodException(method);
		}
	}

	static void respond(OutputStream output, Status status, String errorMessage)
		throws IOException {
		output.write(status.value);
		output.flush();
		if (!status.isError()) return;
		PrintStream stream = new PrintStream(output);
		stream.print(errorMessage);
		stream.flush();
	}

	private void checkRequested() {
		if (!requested) throw new IllegalStateException(
			"getServerError()を実行する前に、request(CircularMethod)を実行する必要があります。");
	}

	/**
	 * {@link ControlServer}に対する要求を表したクラスです。
	 */
	public static class Method {

		final char value;

		private final String name;

		private Method(char value, String name) {
			this.value = value;
			this.name = name;
		}

		@Override
		public String toString() {
			return "[" + value + ":" + name + "]";
		}
	}

	/**
	 * {@link ControlServer}にからの状態通知を表したクラスです。
	 */
	public static class Status {

		private final char value;

		private final String name;

		private final boolean isError;

		private Status(char value, String name) {
			this.value = value;
			this.name = name;
			isError = value - '0' >> 1 == 1 ? true : false;
		}

		@Override
		public String toString() {
			return "[" + value + ":" + name + "]";
		}

		/**
		 * この状態通知が、サーバエラーを表すかを示します。
		 * <p>
		 * 戻り値がtrueの場合、{@link ControlProtocol#getServerError()}でサーバからのエラーメッセージが取得可能です。
		 * 
		 * @return サーバエラーか？
		 * @see ControlProtocol#getServerError()
		 */
		public boolean isError() {
			return isError;
		}
	}

	@SuppressWarnings("serial")
	static class IlleagalMethodException extends Exception {

		private IlleagalMethodException(int method) {
			super("クライアントから異常な値が送られました。Method=" + method);
		}
	}
}
