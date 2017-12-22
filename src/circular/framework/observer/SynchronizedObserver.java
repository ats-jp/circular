package circular.framework.observer;

import java.net.InetAddress;
import java.sql.SQLException;
import java.util.Date;

import circular.framework.AbortNotice;
import circular.framework.ControlProtocol.Method;
import circular.framework.ControlProtocol.Status;
import circular.framework.Observer;
import circular.framework.Phase;
import circular.framework.TransactionId;

/**
 * 全てのメソッドを、このクラスのインスタンス自身で同期化する{@link Observer}のラッパークラスです。
 * 
 * @author 千葉 哲嗣
 * @version $Name:  $
 */
public class SynchronizedObserver implements Observer {

	private final Observer target;

	/**
	 * @param target 同期化させたい{@link Observer}
	 */
	public SynchronizedObserver(Observer target) {
		this.target = target;
	}

	@Override
	public synchronized void receiveCircularStarted() {
		target.receiveCircularStarted();
	}

	@Override
	public synchronized void receiveCircularStopped() {
		target.receiveCircularStopped();
	}

	@Override
	public synchronized void receiveControlServerAccepted(
		TransactionId id,
		InetAddress from) {
		target.receiveControlServerAccepted(id, from);
	}

	@Override
	public synchronized void receiveControlServerRequested(
		TransactionId id,
		Method request) {
		target.receiveControlServerRequested(id, request);
	}

	@Override
	public synchronized void receiveControlServerResponse(
		TransactionId id,
		Status response,
		Throwable error) {
		target.receiveControlServerResponse(id, response, error);
	}

	@Override
	public synchronized void receiveControlServerTimeout(TransactionId id) {
		target.receiveControlServerTimeout(id);
	}

	@Override
	public synchronized void receiveControlServerStartuped() {
		target.receiveControlServerStartuped();
	}

	@Override
	public synchronized void receiveControlServerShutdowned() {
		target.receiveControlServerShutdowned();
	}

	@Override
	public synchronized void receiveCycleStarted() {
		target.receiveCycleStarted();
	}

	@Override
	public synchronized void receiveCycleEnded() {
		target.receiveCycleEnded();
	}

	@Override
	public synchronized void receivePhaseBefore(Phase phase) {
		target.receivePhaseBefore(phase);
	}

	@Override
	public synchronized void receivePhaseAfter(Phase phase) {
		target.receivePhaseAfter(phase);
	}

	@Override
	public synchronized void receiveInterrupted() {
		target.receiveInterrupted();
	}

	@Override
	public synchronized void receiveAbort(AbortNotice notice) {
		target.receiveAbort(notice);
	}

	@Override
	public synchronized void receiveIntervalMinutesChanged(
		int newIntervalMinutes) {
		target.receiveIntervalMinutesChanged(newIntervalMinutes);
	}

	@Override
	public synchronized void receiveNextCycleSchedule(Date next) {
		target.receiveNextCycleSchedule(next);
	}

	@Override
	public synchronized void receiveInputConnectFailure(SQLException e) {
		target.receiveInputConnectFailure(e);
	}

	@Override
	public synchronized void receiveError(String message, Throwable t) {
		target.receiveError(message, t);
	}

	@Override
	public synchronized void receiveFatalError(String message, Throwable t) {
		target.receiveFatalError(message, t);
	}
}
