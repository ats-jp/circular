package circular.framework;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import circular.framework.ControlProtocol.IlleagalMethodException;
import circular.framework.ControlProtocol.Method;
import circular.framework.ControlProtocol.Status;
import circular.framework.Switch.Target;

/**
 * TCP/IP接続をによる指示を待ち受けるサーバとして動作し、{@link Circular}の開始／停止を行うクラスです。
 * 
 * @author 千葉 哲嗣
 * @version $Name:  $
 */
public class ControlServer {

	private final Configure config;

	private final Circular circular;

	private final Switch mySwitch;

	/**
	 * パラメータの{@link Circular}を操作するこのクラスのインスタンスを生成します。
	 * 
	 * @param config このControlServerに対する設定
	 * @param circular コントロール対象
	 */
	public ControlServer(Configure config, Circular circular) {
		this.config = config;
		this.circular = circular;
		mySwitch = new Switch(new Server());
	}

	/**
	 * サービスを開始します。
	 */
	public void startup() {
		mySwitch.on();
	}

	/**
	 * サービスを終了します。
	 */
	public void shutdown() {
		mySwitch.off();
	}

	private void respondError(
		OutputStream output,
		Status status,
		TransactionId id,
		Throwable t) throws IOException {
		ControlProtocol.respond(output, status, id + " " + t);
		config.observer.receiveControlServerResponse(id, status, t);
	}

	private void service(Socket client) {
		TransactionId myId = new TransactionId();

		config.observer.receiveControlServerAccepted(
			myId,
			client.getInetAddress());
		try {
			Method method;
			try {
				method = ControlProtocol.receive(client.getInputStream());
			} catch (SocketTimeoutException e) {
				config.observer.receiveControlServerTimeout(myId);
				return;
			} catch (IlleagalMethodException e) {
				try {
					respondError(
						client.getOutputStream(),
						ControlProtocol.UNKNOWN_METHOD_ERROR,
						myId,
						e);
					return;
				} finally {
					config.observer.receiveError(e.getMessage(), e);
				}
			}

			config.observer.receiveControlServerRequested(myId, method);

			try {
				switch (method.value) {
				case ControlProtocol.START_VALUE:
					circular.start();
					break;
				case ControlProtocol.STOP_VALUE:
					circular.stop();
					break;
				case ControlProtocol.INQUIRE_VALUE:
					break;
				default:
					//ControlProtocol.receiveで既に不明Methodははじいているので、ここはバグ等でしか到達しない
					throw new Error(String.valueOf(method.value));
				}
			} catch (Throwable t) {
				try {
					respondError(
						client.getOutputStream(),
						ControlProtocol.UNKNOWN_SERVER_ERROR,
						myId,
						t);
					return;
				} finally {
					config.observer.receiveFatalError(t.getMessage(), t);
				}
			}

			OutputStream output = client.getOutputStream();
			Status status = circular.isRunning()
				? ControlProtocol.ON
				: ControlProtocol.OFF;
			ControlProtocol.respond(output, status, null);

			config.observer.receiveControlServerResponse(myId, status, null);
		} catch (IOException e) {
			takeCareOf(e);
		} finally {
			try {
				client.close();
			} catch (IOException e) {
				takeCareOf(e);
			}
		}
	}

	private void takeCareOf(IOException e) {
		config.observer.receiveError("クライアントとの入出力中に問題が発生しました。", e);
	}

	private class Server extends Target {

		@Override
		String getName() {
			return "ControlServer";
		}

		@Override
		void execute() {
			ServerSocket socket = null;
			try {
				socket = new ServerSocket(
					config.controlServerPort,
					0,
					InetAddress.getByName(config.controlServerAddress));
				socket.setSoTimeout(config.controlServerIntervalMillis);
				while (true) {
					try {
						Socket client = socket.accept();
						client.setSoTimeout(config.controlServerTimeoutSeconds * 1000);
						service(client);
					} catch (SocketTimeoutException e) {
						if (mySwitch.isOffing()) {
							break;
						}
					}
				}
			} catch (Throwable t) {
				ErrorUtils.notifyMessagelessFatalError(
					config.observer,
					ControlServer.class,
					t);
			} finally {
				try {
					if (socket != null) socket.close();
				} catch (IOException e) {
					ErrorUtils.notifyMessagelessError(
						config.observer,
						ControlServer.class,
						e);
				}
			}
		}

		@Override
		void receiveTargetStarted() {
			config.observer.receiveControlServerStartuped();
		}

		@Override
		void receiveTargetDead() {
			config.observer.receiveControlServerShutdowned();
		}
	}
}
