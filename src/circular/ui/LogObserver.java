package circular.ui;

import java.net.InetAddress;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import circular.framework.AbortNotice;
import circular.framework.ControlProtocol.Method;
import circular.framework.ControlProtocol.Status;
import circular.framework.Cycle;
import circular.framework.Observer;
import circular.framework.Phase;
import circular.framework.TransactionId;

/**
 * org.apache.commons.logging.Logに対して実行状況をログとして出力する{@link Observer}です。
 * 
 * @author 千葉 哲嗣
 * @version $Name:  $
 */
public class LogObserver implements Observer {

	private static final Log log = LogFactory.getLog(LogObserver.class);

	private final String configName;

	/**
	 * 設定ごとのインスタンスを生成します。
	 *  
	 * @param configName 設定名
	 */
	public LogObserver(String configName) {
		this.configName = configName;
	}

	/**
	 * ログレベル：info
	 * 
	 * @see Observer#receiveCircularStarted()
	 */
	@Override
	public void receiveCircularStarted() {
		log.info(finishMessage("Circularが開始しました。"));
	}

	/**
	 * ログレベル：info
	 * 
	 * @see Observer#receiveCircularStopped()
	 */
	@Override
	public void receiveCircularStopped() {
		log.info(finishMessage("Circularが停止しました。"));
	}

	/**
	 * ログレベル：info
	 * 
	 * @see Observer#receiveControlServerAccepted(TransactionId, InetAddress)
	 */
	@Override
	public void receiveControlServerAccepted(TransactionId id, InetAddress from) {
		log.info(finishMessage("ControlServerは接続を受け付けました。address="
			+ from
			+ " "
			+ id));
	}

	/**
	 * ログレベル：info
	 * 
	 * @see Observer#receiveControlServerRequested(TransactionId, Method)
	 */
	@Override
	public void receiveControlServerRequested(TransactionId id, Method request) {
		log.info(finishMessage(id + "の要求は" + request + "です。"));
	}

	/**
	 * ログレベル：info（サーバからエラーを返す場合はerror）
	 * 
	 * @see Observer#receiveControlServerResponse(TransactionId, Status, Throwable)
	 */
	@Override
	public void receiveControlServerResponse(
		TransactionId id,
		Status response,
		Throwable error) {
		String message = id + "への返答は" + response + "です。";
		if (error != null) log.error(message, error);
		else log.info(finishMessage(message));
	}

	/**
	 * ログレベル：warn
	 * 
	 * @see Observer#receiveControlServerTimeout(TransactionId)
	 */
	@Override
	public void receiveControlServerTimeout(TransactionId id) {
		log.warn(finishMessage(id + "からの接続がタイムアウトしました。"));
	}

	/**
	 * ログレベル：info
	 * 
	 * @see Observer#receiveControlServerStartuped()
	 */
	@Override
	public void receiveControlServerStartuped() {
		log.info(finishMessage("ControlServerが起動しました。"));
	}

	/**
	 * ログレベル：info
	 * 
	 * @see Observer#receiveControlServerShutdowned()
	 */
	@Override
	public void receiveControlServerShutdowned() {
		log.info(finishMessage("ControlServerが終了しました。"));
	}

	/**
	 * ログレベル：info
	 * 
	 * @see Observer#receiveCycleStarted()
	 */
	@Override
	public void receiveCycleStarted() {
		log.info(finishMessage("サイクルが開始しました。"));
	}

	/**
	 * ログレベル：info
	 * 
	 * @see Observer#receiveCycleEnded()
	 */
	@Override
	public void receiveCycleEnded() {
		log.info(finishMessage("サイクルが終了しました。"));
	}

	/**
	 * ログレベル：trace
	 * 
	 * @see Observer#receivePhaseBefore(Phase)
	 */
	@Override
	public void receivePhaseBefore(Phase phase) {
		log.trace(finishMessage("フェイズ[" + phase.getName() + "]を開始します。"));
	}

	/**
	 * ログレベル：trace
	 * 
	 * @see Observer#receivePhaseAfter(Phase)
	 */
	@Override
	public void receivePhaseAfter(Phase phase) {
		log.trace(finishMessage("フェイズ[" + phase.getName() + "]が終了しました。"));
	}

	/**
	 * ログレベル：trace
	 * 
	 * @see Observer#receiveInterrupted()
	 */
	@Override
	public void receiveInterrupted() {
		log.trace(finishMessage("Circularは停止命令を受け付けました。"));
	}

	/**
	 * ログレベル：info
	 * 
	 * @see Observer#receiveAbort(AbortNotice)
	 */
	@Override
	public void receiveAbort(AbortNotice notice) {
		log.info(finishMessage("プラグインから処理の中断が指示されました。"), notice);
	}

	/**
	 * ログレベル：trace
	 * 
	 * @see Observer#receiveIntervalMinutesChanged(int)
	 */
	@Override
	public void receiveIntervalMinutesChanged(int newIntervalMinutes) {
		log.trace(finishMessage("実行間隔が" + newIntervalMinutes + "分に変更されました。"));
	}

	/**
	 * ログレベル：trace
	 * 
	 * @see Observer#receiveNextCycleSchedule(Date)
	 */
	@Override
	public void receiveNextCycleSchedule(Date next) {
		log.trace(finishMessage("次のサイクル実行時刻は"
			+ new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(next)
			+ "の予定です。"));
	}

	/**
	 * ログレベル：warn
	 * 
	 * @see Observer#receiveInputConnectFailure(SQLException)
	 */
	@Override
	public void receiveInputConnectFailure(SQLException e) {
		log.warn(finishMessage("他システムへの接続に失敗しました。"), e);
	}

	/**
	 * ログレベル：error
	 * 
	 * @see Observer#receiveError(String, Throwable)
	 */
	@Override
	public void receiveError(String message, Throwable t) {
		log.error(finishMessage(message), t);
	}

	/**
	 * ログレベル：fatal
	 * 
	 * @see Observer#receiveFatalError(String, Throwable)
	 */
	@Override
	public void receiveFatalError(String message, Throwable t) {
		log.fatal(finishMessage(message), t);
	}

	/**
	 * 単にログを出力します。
	 * <p>
	 * このメソッドは、{@link Cycle#execute()}内で呼び出す必要があります。
	 *
	 * @param message fatal ログ
	 */
	public static void fatal(String message) {
		log.fatal(finishMessage(Main.getConfigName(), message));
	}

	/**
	 * 単にログを出力します。
	 * <p>
	 * このメソッドは、{@link Cycle#execute()}内で呼び出す必要があります。
	 *
	 * @param message error ログ
	 */
	public static void error(String message) {
		log.error(finishMessage(Main.getConfigName(), message));
	}

	/**
	 * 単にログを出力します。
	 * <p>
	 * このメソッドは、{@link Cycle#execute()}内で呼び出す必要があります。
	 *
	 * @param message warn ログ
	 */
	public static void warn(String message) {
		log.warn(finishMessage(Main.getConfigName(), message));
	}

	/**
	 * 単にログを出力します。
	 * <p>
	 * このメソッドは、{@link Cycle#execute()}内で呼び出す必要があります。
	 *
	 * @param message info ログ
	 */
	public static void info(String message) {
		log.info(finishMessage(Main.getConfigName(), message));
	}

	/**
	 * 単にログを出力します。
	 * <p>
	 * このメソッドは、{@link Cycle#execute()}内で呼び出す必要があります。
	 *
	 * @param message debug ログ
	 */
	public static void debug(String message) {
		log.debug(finishMessage(Main.getConfigName(), message));
	}

	/**
	 * 単にログを出力します。
	 * <p>
	 * このメソッドは、{@link Cycle#execute()}内で呼び出す必要があります。
	 *
	 * @param message trace ログ
	 */
	public static void trace(String message) {
		log.trace(finishMessage(Main.getConfigName(), message));
	}

	private static String finishMessage(String configName, String baseMessage) {
		if (configName == null || configName.length() == 0) return baseMessage;
		return "[" + configName + "] " + baseMessage;
	}

	private String finishMessage(String baseMessage) {
		return finishMessage(configName, baseMessage);
	}
}