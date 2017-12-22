package circular.ui;

import java.io.PrintWriter;
import java.io.StringWriter;

import circular.framework.CircularProperties;
import circular.framework.observer.IdleObserver;

/**
 * Log4j�̃��[���ʒm�@�\�ɂ�SMTP�F�؋@�\���Ȃ����߁A��ւƂ��ėp�ӂ��ꂽ�x�񃁁[�����M�N���X�ł��B
 * 
 * @author ��t �N�k
 * @version $Name:  $
 */
class AlertObserver extends IdleObserver {

	private static final String lineSeparator = System.getProperty("line.separator");

	private static final String separator = System.getProperty("line.separator")
		+ "=================================================="
		+ System.getProperty("line.separator");

	private final CircularProperties properties;

	AlertObserver(CircularProperties properties) {
		this.properties = properties;
	}

	/**
	 * @see Observer#receiveError(String, Throwable)
	 */
	@Override
	public void receiveError(String message, Throwable t) {
		String content = "������́ACircular���ŃG���[�����������ۂɁA�����Ŕ��M����郁�[���ł��B"
			+ lineSeparator
			+ lineSeparator
			+ "�T�[�o�����ŃG���[���������܂����B�ڍׂ͈ȉ��̂Ƃ���ł��B"
			+ separator
			+ message
			+ separator
			+ getStackTraceString(t);
		Mail mail = new Mail(properties);
		mail.setSubject("[ERROR] Circular �G���[�����x��");
		mail.setMessage(content);
		mail.send();
	}

	/**
	 * @see Observer#receiveFatalError(String, Throwable)
	 */
	@Override
	public void receiveFatalError(String message, Throwable t) {
		String content = "������́ACircular���ŃG���[�����������ۂɁA�����Ŕ��M����郁�[���ł��B"
			+ lineSeparator
			+ lineSeparator
			+ "�T�[�o�����ŏd��ȃG���[���������܂����B�ڍׂ͈ȉ��̂Ƃ���ł��B"
			+ separator
			+ message
			+ separator
			+ getStackTraceString(t);
		Mail mail = new Mail(properties);
		mail.setSubject("[FATAL ERROR] Circular �d��G���[�����x��");
		mail.setMessage(content);
		mail.send();
	}

	private static String getStackTraceString(Throwable t) {
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		t.printStackTrace(printWriter);
		printWriter.flush();
		return stringWriter.toString();
	}
}