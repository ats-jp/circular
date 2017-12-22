package circular.framework.observer;

import java.net.InetAddress;
import java.sql.SQLException;
import java.util.Date;

import circular.framework.AbortNotice;
import circular.framework.Observer;
import circular.framework.Phase;
import circular.framework.TransactionId;
import circular.framework.ControlProtocol.Method;
import circular.framework.ControlProtocol.Status;

/**
 * オーバーライドして必要な監視だけを再実装できるようにするための、"何もしない"{@link Observer}クラスです。
 * 
 * @author 千葉 哲嗣
 * @version $Name:  $
 */
public class IdleObserver implements Observer {

	@Override
	public void receiveCircularStarted() {}

	@Override
	public void receiveCircularStopped() {}

	@Override
	public void receiveControlServerAccepted(TransactionId id, InetAddress from) {}

	@Override
	public void receiveControlServerRequested(TransactionId id, Method request) {}

	@Override
	public void receiveControlServerResponse(
		TransactionId id,
		Status response,
		Throwable error) {}

	@Override
	public void receiveControlServerTimeout(TransactionId id) {}

	@Override
	public void receiveControlServerStartuped() {}

	@Override
	public void receiveControlServerShutdowned() {}

	@Override
	public void receiveCycleStarted() {}

	@Override
	public void receiveCycleEnded() {}

	@Override
	public void receivePhaseBefore(Phase phase) {}

	@Override
	public void receivePhaseAfter(Phase phase) {}

	@Override
	public void receiveInterrupted() {}

	@Override
	public void receiveAbort(AbortNotice notice) {}

	@Override
	public void receiveIntervalMinutesChanged(int newIntervalMinutes) {}

	@Override
	public void receiveNextCycleSchedule(Date next) {}

	@Override
	public void receiveInputConnectFailure(SQLException e) {}

	@Override
	public void receiveError(String message, Throwable t) {}

	@Override
	public void receiveFatalError(String message, Throwable t) {}
}
