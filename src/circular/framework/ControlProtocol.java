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
 * {@link ControlServer}�ƔC�ӂ̃N���C�A���g�Ԃ̃v���g�R�����`�����N���X�ł��B
 * 
 * @author ��t �N�k
 * @version $Name:  $
 */
public class ControlProtocol {

	/**
	 * �u�J�n�v���v�̒l '0'
	 */
	public static final char START_VALUE = '0';

	/**
	 * �u��~�v���v�̒l '1'
	 */
	public static final char STOP_VALUE = '1';

	/**
	 * �u��ԗv���v�̒l '2'
	 */
	public static final char INQUIRE_VALUE = '2';

	/**
	 * �u���s���v�̒l '0'
	 */
	public static final char ON_VALUE = '0';

	/**
	 * �u��~���v�̒l '1'
	 */
	public static final char OFF_VALUE = '1';

	/**
	 * �u�v���s���G���[�v�̒l '2'
	 */
	public static final char UNKNOWN_METHOD_ERROR_VALUE = '2';

	/**
	 * �u�s���ȃT�[�o�G���[�v�̒l '3'
	 */
	public static final char UNKNOWN_SERVER_ERROR_VALUE = '3';

	/**
	 * �u�J�n�v���v
	 * <p>
	 * {@link Circular}���J�n�����܂��B
	 */
	public static final Method START = new Method(START_VALUE, "�J�n�v��");

	/**
	 * �u��~�v���v
	 * <p>
	 * {@link Circular}���~�����܂��B
	 */
	public static final Method STOP = new Method(STOP_VALUE, "��~�v��");

	/**
	 * �u��ԗv���v
	 * <p>
	 * {@link Circular}�����݁A���s������~������v�����܂��B
	 */
	public static final Method INQUIRE = new Method(INQUIRE_VALUE, "��ԗv��");

	/**
	 * �u���s���v
	 * <p>
	 * {@link Circular}�͌��݁A���s���ł��邱�Ƃ�\���܂��B
	 */
	public static final Status ON = new Status(ON_VALUE, "���s��");

	/**
	 * �u��~���v
	 * <p>
	 * {@link Circular}�͌��݁A��~���ł��邱�Ƃ�\���܂��B
	 */
	public static final Status OFF = new Status(OFF_VALUE, "��~��");

	/**
	 * �u�v���s���G���[�v
	 * <p>
	 * �N���C�A���g����̗v�����AContorolProtocol�Œ�`���ꂽ�l�ł͂Ȃ����Ƃ�\���܂��B
	 */
	public static final Status UNKNOWN_METHOD_ERROR = new Status(
		UNKNOWN_METHOD_ERROR_VALUE,
		"�v���s���G���[");

	/**
	 * �u�s���ȃT�[�o�G���[�v
	 * <p>
	 * �T�[�o���ŗ\�����Ȃ��G���[���������Ă��邱�Ƃ�\���܂��B
	 */
	public static final Status UNKNOWN_SERVER_ERROR = new Status(
		UNKNOWN_SERVER_ERROR_VALUE,
		"�s���ȃT�[�o�G���[");

	private final Socket socket;

	private boolean requested = false;

	/**
	 * input���T�[�o����̃��X�|���X�p�Aoutput���T�[�o�ւ̃��N�G�X�g�p�Ƃ���C���X�^���X�𐶐����܂��B
	 * 
	 * @param address {@link ControlServer}�̃A�h���X
	 * @param port {@link ControlServer}�̃|�[�g
	 * @throws IOException �\������IO�G���[�����������ꍇ
	 */
	public ControlProtocol(String address, int port) throws IOException {
		try {
			socket = new Socket(InetAddress.getByName(address), port);
		} catch (UnknownHostException e) {
			throw new IllegalStateException(Configure.CONTROLSERVER_ADDRESS_KEY
				+ "�Ŏw�肳��Ă���l���ڑ��ł���A�h���X�ł͂���܂���B");
		} catch (NumberFormatException e) {
			throw new IllegalStateException(Configure.CONTROLSERVER_PORT_KEY
				+ "�Ŏw�肳��Ă���l�����l�ł͂���܂���B");
		}
	}

	/**
	 * {@link ControlServer}�փ��N�G�X�g�𑗐M���܂��B
	 * 
	 * @param method ���N�G�X�g����w��
	 * @return ���݂̃T�[�o�̏��
	 * @throws java.io.IOException �\�����Ȃ����o�͗�O
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
			throw new IllegalStateException("�T�[�o����ُ�Ȓl�������܂����BStatus=" + status);
		}
	}

	/**
	 * �T�[�o�����M�����G���[���b�Z�[�W��ԋp���܂��B
	 * <p>
	 * �T�[�o����̃��X�|���X�ł���{@link Status}���A{@link Status#isError()}=true�ƂȂ�ꍇ�A�T�[�o���ŃG���[���������Ă��܂��B���̏ꍇ�̂݁A�G���[���b�Z�[�W���擾���邱�Ƃ��\�ł��B
	 * 
	 * @return {@link Status#isError()}=faise�̏ꍇ�A""
	 * @throws IOException �\�����Ȃ����o�͗�O
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
			"getServerError()�����s����O�ɁArequest(CircularMethod)�����s����K�v������܂��B");
	}

	/**
	 * {@link ControlServer}�ɑ΂���v����\�����N���X�ł��B
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
	 * {@link ControlServer}�ɂ���̏�Ԓʒm��\�����N���X�ł��B
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
		 * ���̏�Ԓʒm���A�T�[�o�G���[��\�����������܂��B
		 * <p>
		 * �߂�l��true�̏ꍇ�A{@link ControlProtocol#getServerError()}�ŃT�[�o����̃G���[���b�Z�[�W���擾�\�ł��B
		 * 
		 * @return �T�[�o�G���[���H
		 * @see ControlProtocol#getServerError()
		 */
		public boolean isError() {
			return isError;
		}
	}

	@SuppressWarnings("serial")
	static class IlleagalMethodException extends Exception {

		private IlleagalMethodException(int method) {
			super("�N���C�A���g����ُ�Ȓl�������܂����BMethod=" + method);
		}
	}
}
