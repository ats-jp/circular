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
 * �R�}���h���C������Circular Framework�𑀍삷��c�[���ł��B
 * 
 * @author ��t �N�k
 * @version $Name:  $
 */
public class CircularController {

	private CircularController() {}

	/**
	 * �p�����[�^�̗v���őS�Ă�{@link Circular}���J�n���͒�~�����܂��B
	 * 
	 * @param args ���p�����[�^��config�f�B���N�g���ւ̃p�X�A���p�����[�^��"start"�܂���"stop"
	 */
	public static void main(String[] args) {
		if (args.length != 2) throw new IllegalArgumentException(
			"�p�����[�^�ɂ�config�f�B���N�g���̐�΃p�X�ƁAstart��������stop�̓���K�v�ł��B");

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
						"�g�p�ł���p�����[�^�� start �� stop �݂̂ł��B");
				}
			} catch (IOException e) {
				throw new IllegalStateException("���o�͏����ŃG���[���������܂����B", e);
			} catch (ServerSideException e) {
				throw new IllegalStateException("�T�[�o���ɖ�肪�������Ă���悤�ł��B", e);
			}
		}
	}

	/**
	 * {@link ControlServer}��ʂ��āA{@link Circular}���J�n�����܂��B
	 * 
	 * @param address {@link ControlServer}�̃A�h���X
	 * @param port {@link ControlServer}�̃|�[�g
	 * @throws ServerSideException �T�[�o���ŃG���[�����������ꍇ
	 * @throws IOException �\������IO�G���[�����������ꍇ
	 * @see Circular#start()
	 */
	public static void startCircular(String address, int port)
		throws ServerSideException, IOException {
		ControlProtocol protocol = new ControlProtocol(address, port);
		checkError(protocol, protocol.request(ControlProtocol.START));
	}

	/**
	 * {@link ControlServer}��ʂ��āA{@link Circular}���~�����܂��B
	 * 
	 * @param address {@link ControlServer}�̃A�h���X
	 * @param port {@link ControlServer}�̃|�[�g
	 * @throws ServerSideException �T�[�o���ŃG���[�����������ꍇ
	 * @throws IOException �\������IO�G���[�����������ꍇ
	 * @see Circular#stop()
	 */
	public static void stopCircular(String address, int port)
		throws ServerSideException, IOException {
		ControlProtocol protocol = new ControlProtocol(address, port);
		checkError(protocol, protocol.request(ControlProtocol.STOP));
	}

	/**
	 * {@link ControlServer}��ʂ��āA{@link Circular}�̌��݂̎��s�󋵂��擾���܂��B
	 * 
	 * @param address {@link ControlServer}�̃A�h���X
	 * @param port {@link ControlServer}�̃|�[�g
	 * @return Circular�����s���̏ꍇ�Atrue
	 * @throws ServerSideException �T�[�o���ŃG���[�����������ꍇ
	 * @throws IOException �\������IO�G���[�����������ꍇ
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
	 * �T�[�o���Ŕ��������G���[�̃��b�Z�[�W������O�ł��B
	 */
	@SuppressWarnings("serial")
	public static class ServerSideException extends Exception {

		private ServerSideException(String message) {
			super(message);
		}
	}
}