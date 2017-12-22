package circular.framework;

import java.net.InetAddress;
import java.sql.SQLException;
import java.util.Date;

import circular.framework.ControlProtocol.Method;
import circular.framework.ControlProtocol.Status;

/**
 * Circular Frameworkの実行状況を観測するためのインターフェイスです。
 * 
 * @author 千葉 哲嗣
 * @version $Name:  $
 */
public interface Observer {

	/**
	 * {@link Circular}のポーリング処理を開始したことが、Circular Frameworkによって通知されます。
	 * 
	 * @see Circular#start()
	 */
	void receiveCircularStarted();

	/**
	 * {@link Circular}のポーリング処理を停止したことが、Circular Frameworkによって通知されます。
	 * 
	 * @see Circular#stop()
	 */
	void receiveCircularStopped();

	/**
	 * TCP/IP接続経由で{@link ControlServer}への指示がきたことを、Circular Frameworkによって通知されます。
	 * 
	 * @param id {@link ControlServer}がロードされてからカウントアップしているID番号
	 * @param from 接続先クライアントのアドレス
	 */
	void receiveControlServerAccepted(TransactionId id, InetAddress from);

	/**
	 * パラメータの{@link TransactionId}で接続してきているクライアントからの要求を受け付けたことを、Circular Frameworkによって通知されます。
	 * 
	 * @param id {@link Observer#receiveControlServerAccepted(TransactionId, InetAddress)}のtransactionIdと同じ値となるのが、同一トランザクションとなる
	 * @param request 指示
	 */
	void receiveControlServerRequested(TransactionId id, Method request);

	/**
	 * パラメータの{@link TransactionId}で接続してきているクライアントへの返答内容を、Circular Frameworkによって通知されます。
	 * 
	 * @param id {@link Observer#receiveControlServerAccepted(TransactionId, InetAddress)}のtransactionIdと同じ値となるのが、同一トランザクションとなる
	 * @param response 返答
	 * @param error このトランザクションで発生した例外又はエラー、但し発生しなかった場合はnull
	 */
	void receiveControlServerResponse(
		TransactionId id,
		Status response,
		Throwable error);

	/**
	 * パラメータの{@link TransactionId}で接続してきているクライアントからの要求が来る前に{@link ControlServer}がタイムアウトしたことが、Circular Frameworkによって通知されます。
	 * 
	 * @param id {@link Observer#receiveControlServerAccepted(TransactionId, InetAddress)}のtransactionIdと同じ値となるのが、同一トランザクションとなる
	 */
	void receiveControlServerTimeout(TransactionId id);

	/**
	 * {@link ControlServer}が起動したことが、Circular Frameworkによって通知されます。
	 */
	void receiveControlServerStartuped();

	/**
	 * {@link ControlServer}が終了したことが、Circular Frameworkによって通知されます。
	 */
	void receiveControlServerShutdowned();

	/**
	 * 1サイクルが開始されることが、Circular Frameworkによって通知されます。
	 * 
	 * @see circular.framework.Cycle
	 */
	void receiveCycleStarted();

	/**
	 * 1サイクルが終了したことが、Circular Frameworkによって通知されます。
	 * 
	 * @see circular.framework.Cycle
	 */
	void receiveCycleEnded();

	/**
	 * この{@link Phase}がこれから開始することが、Circular Frameworkによって通知されます。
	 * 
	 * @param phase 開始した{@link Phase}
	 */
	void receivePhaseBefore(Phase phase);

	/**
	 * この{@link Phase}が終了したことが、Circular Frameworkによって通知されます。
	 * 
	 * @param phase 終了した{@link Phase}
	 */
	void receivePhaseAfter(Phase phase);

	/**
	 * 処理の中断が指示されたことが、Circular Frameworkによって通知されます。
	 * 
	 * @see Circular#stop()
	 */
	void receiveInterrupted();

	/**
	 * サイクルの中断がプラグイン側から指示されたことが、Circular Frameworkによって通知されます。
	 * 
	 * @param notice そのとき発生した通知
	 */
	void receiveAbort(AbortNotice notice);

	/**
	 * {@link Cycle}の実行間隔が変更されたことが、Circular Frameworkによって通知されます。
	 * 
	 * @param newIntervalMinutes
	 */
	void receiveIntervalMinutesChanged(int newIntervalMinutes);

	/**
	 * {@link Cycle}の次の実行予定時刻が、Circular Frameworkによって通知されます。
	 * 
	 * @param next 次の実行予定時刻
	 */
	void receiveNextCycleSchedule(Date next);

	/**
	 * 他システムDBへの接続に失敗したことが、Circular Frameworkによって通知されます。
	 * 
	 * @param e そのとき発生した例外
	 */
	void receiveInputConnectFailure(SQLException e);

	/**
	 * サイクル実行中に予期しない問題が発生した場合に、Circular Frameworkによって通知されます。
	 * <p>
	 * この通知が行われた場合、実行中だったサイクルは中断されますが、{@link Circular}のポーリングは停止せず、次のサイクルにトライします。
	 * 
	 * @param message 問題の発生した状況
	 * @param t そのとき発生した問題
	 */
	void receiveError(String message, Throwable t);

	/**
	 * サイクル実行中に予期しない致命的な問題が発生した場合に、Circular Frameworkによって通知されます。
	 * <p>
	 * この通知が行われた場合、実行中だったサイクルは中断され、{@link Circular}のポーリングも停止しています。
	 * <br>
	 * 指示がない限り再開されません。
	 * 
	 * @param message 問題の発生した状況
	 * @param t そのとき発生した問題
	 */
	void receiveFatalError(String message, Throwable t);
}
