package circular.framework;

import java.util.Date;

/**
 * サイクル方法を表す抽象基底クラスです。
 * 
 * @author 千葉 哲嗣
 * @version $Name:  $
 */
public abstract class CycleStrategy {

	private int intervalMinutes;

	private Observer observer;

	/**
	 * サイクルのループを行います。
	 * <p>
	 * このメソッドから復帰する場合はユーザから停止を指示されたか、予期しない例外が発生した場合です。
	 * <br>
	 * ループ方法そのものはサブクラスで定義してください。
	 * 
	 * @param cycle 1サイクル
	 * @throws InterruptedException ユーザから停止が指示（{@link Thread#interrupt()}がフレームワークにより実行）された場合に発生
	 */
	protected abstract void execute(Cycle cycle) throws InterruptedException;

	/**
	 * サブクラスが、現在設定されている停止間隔を取得するために用意されているメソッドです。
	 * 
	 * @return 現在の停止間隔（分）
	 */
	protected synchronized int getCurrentIntervalMinutes() {
		return intervalMinutes;
	}

	/**
	 * サブクラスが、現在ユーザから停止指示が来ているかどうか調べるために用意されたメソッドです。
	 * 
	 * @throws InterruptedException ユーザから停止が指示（{@link Thread#interrupt()}がフレームワークにより実行）された場合に発生
	 */
	protected final void checkInterrupted() throws InterruptedException {
		if (Thread.interrupted()) throw new InterruptedException();
	}

	/**
	 * サブクラスが、次のサイクル開始予定時刻を{@link Observer}に通知するために用意されたメソッドです。
	 * 
	 * @param next 次のサイクル開始予定時刻
	 */
	protected final synchronized void notifyNextCycleScheduleToObserver(
		Date next) {
		observer.receiveNextCycleSchedule((Date) next.clone());
	}

	synchronized void setCurrentIntervalMinutes(int minutes) {
		intervalMinutes = minutes;
	}

	synchronized void setObserver(Observer observer) {
		this.observer = observer;
	}
}
