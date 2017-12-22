package circular.ui;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;

import circular.framework.Circular;
import circular.framework.CircularProperties;
import circular.framework.Configure;
import circular.framework.ControlProtocol;
import circular.framework.ControlProtocol.Status;
import circular.framework.ControlServer;

/**
 * コマンドラインからCircular Frameworkを操作するツールです。
 * 
 * @author 千葉 哲嗣
 * @version $Name:  $
 */
public class CircularController {

	private CircularController() {}

	/**
	 * パラメータの要求で全ての{@link Circular}を開始又は停止させます。
	 * 
	 * @param args 第一パラメータにconfigディレクトリへのパス、第二パラメータに"start"または"stop"
	 */
	public static void main(String[] args) {
		if (args.length != 2) throw new IllegalArgumentException(
			"パラメータにはconfigディレクトリの絶対パスと、startもしくはstopの二つが必要です。");

		Map<String, URL> map = ConfigureUtils.getConfigureFiles(ConfigureUtils.createConfigDirectory(args[0]));
		for (Entry<String, URL> entry : map.entrySet()) {
			try {
				CircularProperties properties = new CircularProperties(
					entry.getValue());
				String address = properties.getProperty(Configure.CONTROLSERVER_ADDRESS_KEY);
				int port = Integer.parseInt(properties.getProperty(Configure.CONTROLSERVER_PORT_KEY));

				if ("start".equals(args[1])) {
					startCircular(address, port);
				} else if ("stop".equals(args[1])) {
					stopCircular(address, port);
				} else {
					throw new IllegalArgumentException(
						"使用できるパラメータは start と stop のみです。");
				}
			} catch (IOException e) {
				throw new IllegalStateException("入出力処理でエラーが発生しました。", e);
			} catch (ServerSideException e) {
				throw new IllegalStateException("サーバ側に問題が発生しているようです。", e);
			}
		}
	}

	/**
	 * {@link ControlServer}を通じて、{@link Circular}を開始させます。
	 * 
	 * @param address {@link ControlServer}のアドレス
	 * @param port {@link ControlServer}のポート
	 * @throws ServerSideException サーバ側でエラーが発生した場合
	 * @throws IOException 予期せぬIOエラーが発生した場合
	 * @see Circular#start()
	 */
	public static void startCircular(String address, int port)
		throws ServerSideException, IOException {
		ControlProtocol protocol = new ControlProtocol(address, port);
		checkError(protocol, protocol.request(ControlProtocol.START));
	}

	/**
	 * {@link ControlServer}を通じて、{@link Circular}を停止させます。
	 * 
	 * @param address {@link ControlServer}のアドレス
	 * @param port {@link ControlServer}のポート
	 * @throws ServerSideException サーバ側でエラーが発生した場合
	 * @throws IOException 予期せぬIOエラーが発生した場合
	 * @see Circular#stop()
	 */
	public static void stopCircular(String address, int port)
		throws ServerSideException, IOException {
		ControlProtocol protocol = new ControlProtocol(address, port);
		checkError(protocol, protocol.request(ControlProtocol.STOP));
	}

	/**
	 * {@link ControlServer}を通じて、{@link Circular}の現在の実行状況を取得します。
	 * 
	 * @param address {@link ControlServer}のアドレス
	 * @param port {@link ControlServer}のポート
	 * @return Circularが実行中の場合、true
	 * @throws ServerSideException サーバ側でエラーが発生した場合
	 * @throws IOException 予期せぬIOエラーが発生した場合
	 * @see Circular#isRunning()
	 */
	public static boolean isRunning(String address, int port)
		throws ServerSideException, IOException {
		ControlProtocol protocol = new ControlProtocol(address, port);
		Status status = protocol.request(ControlProtocol.INQUIRE);
		checkError(protocol, status);
		return status == ControlProtocol.ON ? true : false;
	}

	private static void checkError(ControlProtocol protocol, Status status)
		throws ServerSideException, IOException {
		if (status.isError()) throw new ServerSideException(
			protocol.getServerError());
	}

	/**
	 * サーバ側で発生したエラーのメッセージをもつ例外です。
	 */
	@SuppressWarnings("serial")
	public static class ServerSideException extends Exception {

		private ServerSideException(String message) {
			super(message);
		}
	}
}