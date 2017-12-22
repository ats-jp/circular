package circular.ui;

import java.io.PrintWriter;
import java.io.StringWriter;

import circular.framework.CircularProperties;
import circular.framework.observer.IdleObserver;

/**
 * Log4jのメール通知機能にはSMTP認証機能がないため、代替として用意された警報メール送信クラスです。
 * 
 * @author 千葉 哲嗣
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
		String content = "※これは、Circular内でエラーが発生した際に、自動で発信されるメールです。"
			+ lineSeparator
			+ lineSeparator
			+ "サーバ内部でエラーが発生しました。詳細は以下のとおりです。"
			+ separator
			+ message
			+ separator
			+ getStackTraceString(t);
		Mail mail = new Mail(properties);
		mail.setSubject("[ERROR] Circular エラー発生警報");
		mail.setMessage(content);
		mail.send();
	}

	/**
	 * @see Observer#receiveFatalError(String, Throwable)
	 */
	@Override
	public void receiveFatalError(String message, Throwable t) {
		String content = "※これは、Circular内でエラーが発生した際に、自動で発信されるメールです。"
			+ lineSeparator
			+ lineSeparator
			+ "サーバ内部で重大なエラーが発生しました。詳細は以下のとおりです。"
			+ separator
			+ message
			+ separator
			+ getStackTraceString(t);
		Mail mail = new Mail(properties);
		mail.setSubject("[FATAL ERROR] Circular 重大エラー発生警報");
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