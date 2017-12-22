package circular.framework.observer;

import java.net.InetAddress;
import java.sql.SQLException;
import java.util.Date;

import circular.framework.AbortNotice;
import circular.framework.CircularException;
import circular.framework.Observer;
import circular.framework.Phase;
import circular.framework.TransactionId;
import circular.framework.ControlProtocol.Method;
import circular.framework.ControlProtocol.Status;

/**
 * 複数の{@link Observer}を一つの{@link Observer}として扱うためのクラスです。
 * <p>
 * {@link circular.framework.Configure}にObserversを渡すと、複数のモニタリングが可能です。
 * <br>
 * Compositeパターンの器クラスにあたります。
 * 
 * @author 千葉 哲嗣
 * @version $Name:  $
 */
public class Observers implements Observer {

	private final Observer[] observers;

	private final Observer observersErrorObserver;

	/**
	 * 配下となる{@link Observer}を持った新しいインスタンスを生成します。
	 * <p>
	 * 配下となった{@link Observer}には、パラメータの配列の順番に同じメッセージが渡されます。
	 * 配下の{@link Observer}にメッセージを転送中に例外／エラーが発生した場合、observersErrorObserverの{@link Observer#receiveError(String, Throwable)}又は{@link Observer#receiveFatalError(String, Throwable)}に通知します。
	 * 
	 * @param observers 使用したい複数の{@link Observer}
	 * @param observersErrorObserver 配下の{@link Observer}が起こした例外を報告するための{@link Observer}
	 */
	public Observers(Observer[] observers, Observer observersErrorObserver) {
		this.observers = observers.clone();
		this.observersErrorObserver = observersErrorObserver;
	}

	@Override
	public void receiveCircularStarted() {
		for (Observer observer : observers) {
			try {
				observer.receiveCircularStarted();
			} catch (Throwable t) {
				receiveObserversError(observer, t);
			}
		}
	}

	@Override
	public void receiveCircularStopped() {
		for (Observer observer : observers) {
			try {
				observer.receiveCircularStopped();
			} catch (Throwable t) {
				receiveObserversError(observer, t);
			}
		}
	}

	@Override
	public void receiveControlServerAccepted(TransactionId id, InetAddress from) {
		for (Observer observer : observers) {
			try {
				observer.receiveControlServerAccepted(id, from);
			} catch (Throwable t) {
				receiveObserversError(observer, t);
			}
		}
	}

	@Override
	public void receiveControlServerRequested(TransactionId id, Method request) {
		for (Observer observer : observers) {
			try {
				observer.receiveControlServerRequested(id, request);
			} catch (Throwable t) {
				receiveObserversError(observer, t);
			}
		}
	}

	@Override
	public void receiveControlServerResponse(
		TransactionId id,
		Status response,
		Throwable error) {
		for (Observer observer : observers) {
			try {
				observer.receiveControlServerResponse(id, response, error);
			} catch (Throwable t) {
				receiveObserversError(observer, t);
			}
		}
	}

	@Override
	public void receiveControlServerTimeout(TransactionId id) {
		for (Observer observer : observers) {
			try {
				observer.receiveControlServerTimeout(id);
			} catch (Throwable t) {
				receiveObserversError(observer, t);
			}
		}
	}

	@Override
	public void receiveControlServerStartuped() {
		for (Observer observer : observers) {
			try {
				observer.receiveControlServerStartuped();
			} catch (Throwable t) {
				receiveObserversError(observer, t);
			}
		}
	}

	@Override
	public void receiveControlServerShutdowned() {
		for (Observer observer : observers) {
			try {
				observer.receiveControlServerShutdowned();
			} catch (Throwable t) {
				receiveObserversError(observer, t);
			}
		}
	}

	@Override
	public void receiveCycleStarted() {
		for (Observer observer : observers) {
			try {
				observer.receiveCycleStarted();
			} catch (Throwable t) {
				receiveObserversError(observer, t);
			}
		}
	}

	@Override
	public void receiveCycleEnded() {
		for (Observer observer : observers) {
			try {
				observer.receiveCycleEnded();
			} catch (Throwable t) {
				receiveObserversError(observer, t);
			}
		}
	}

	@Override
	public void receivePhaseBefore(Phase phase) {
		for (Observer observer : observers) {
			try {
				observer.receivePhaseBefore(phase);
			} catch (Throwable t) {
				receiveObserversError(observer, t);
			}
		}
	}

	@Override
	public void receivePhaseAfter(Phase phase) {
		for (Observer observer : observers) {
			try {
				observer.receivePhaseAfter(phase);
			} catch (Throwable t) {
				receiveObserversError(observer, t);
			}
		}
	}

	@Override
	public void receiveInterrupted() {
		for (Observer observer : observers) {
			try {
				observer.receiveInterrupted();
			} catch (Throwable t) {
				receiveObserversError(observer, t);
			}
		}
	}

	@Override
	public void receiveAbort(AbortNotice notice) {
		for (Observer observer : observers) {
			try {
				observer.receiveAbort(notice);
			} catch (Throwable t) {
				receiveObserversError(observer, t);
			}
		}
	}

	@Override
	public void receiveIntervalMinutesChanged(int newIntervalMinutes) {
		for (Observer observer : observers) {
			try {
				observer.receiveIntervalMinutesChanged(newIntervalMinutes);
			} catch (Throwable t) {
				receiveObserversError(observer, t);
			}
		}
	}

	@Override
	public void receiveNextCycleSchedule(Date next) {
		for (Observer observer : observers) {
			try {
				observer.receiveNextCycleSchedule(next);
			} catch (Throwable t) {
				receiveObserversError(observer, t);
			}
		}
	}

	@Override
	public void receiveInputConnectFailure(SQLException e) {
		for (Observer observer : observers) {
			try {
				observer.receiveInputConnectFailure(e);
			} catch (Throwable t) {
				receiveObserversError(observer, t);
			}
		}
	}

	@Override
	public void receiveError(String message, Throwable t) {
		for (Observer observer : observers) {
			try {
				observer.receiveError(message, t);
			} catch (Throwable tt) {
				receiveObserversError(observer, tt);
			}
		}
	}

	@Override
	public void receiveFatalError(String message, Throwable t) {
		for (Observer observer : observers) {
			try {
				observer.receiveFatalError(message, t);
			} catch (Throwable tt) {
				receiveObserversError(observer, tt);
			}
		}
	}

	private void receiveObserversError(Observer observersMember, Throwable t) {
		String message = Observers.class.getName()
			+ "の配下の"
			+ observersMember.getClass().getName()
			+ "で問題が発生しました。";
		if (t instanceof CircularException) {
			observersErrorObserver.receiveError(message, t);
			return;
		}
		observersErrorObserver.receiveFatalError(message, t);
	}
}
