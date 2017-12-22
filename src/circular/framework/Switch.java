package circular.framework;

import java.util.HashMap;
import java.util.Map;

/**
 * ターゲットとなる任意の処理のON/OFFを操作できるスイッチクラスです。
 * 
 * @author 千葉 哲嗣
 * @version $Name:  $
 */
class Switch {

	private static final Map<String, Integer> threadCounterMap = new HashMap<String, Integer>();

	private final Target target;

	/**
	 * 処理中
	 */
	private boolean on = false;

	/**
	 * 停止完了待ち
	 */
	private boolean offing = false;

	private Thread currentTargetThread;

	/**
	 * ターゲット処理をセットすることでインスタンスが作成されます。
	 * 
	 * @param target ON/OFFしたい処理
	 */
	Switch(Target target) {
		target.mySwitch = this;
		this.target = target;
	}

	/**
	 * 処理を開始します。
	 * <p>
	 * このメソッドから正常に復帰した場合、必ず処理が開始されています。
	 */
	synchronized void on() {
		if (isRunning()) return;
		currentTargetThread = new Thread(
			target,
			getNextThreadName(target.getName()));
		currentTargetThread.start();
		on = true;
		target.receiveTargetStarted();
	}

	/**
	 * 処理を停止します。
	 * <p>
	 * このメソッドから正常に復帰した場合、必ず処理の停止が完了しています。
	 */
	synchronized void off() {
		if (!on) return;
		if (offing) {
			waitWhileStoppingAndJoinTarget();
			return;
		}
		offing = true;
		currentTargetThread.interrupt();
		waitWhileStoppingAndJoinTarget();
		target.receiveTargetDead();
	}

	/**
	 * @return 起動されていて、停止完了待ちではない場合、true
	 */
	synchronized boolean isRunning() {
		if (offing) waitWhileStoppingAndJoinTarget();
		return on;
	}

	synchronized boolean isOffing() {
		return offing;
	}

	/**
	 * Targetが終了する直前にTargetのrun()を実行しているスレッドから呼び出されます。
	 */
	private synchronized void receiveTargetDying() {
		on = false;
		notifyAll();
		if (offing) {
			offing = false;
			return;
		}

		//offing == falseの場合、offされずにtargetの処理が終了、つまり例外／エラーが発生しているということ
		//終了監視スレッドを用意し、確実に終了してから終了を通知する
		final Thread myTargetThread = currentTargetThread;

		new Thread(getNextThreadName("Joinner")) {

			@Override
			public void run() {
				while (myTargetThread.isAlive()) {
					try {
						myTargetThread.join();
					} catch (InterruptedException e) {}
				}
				target.receiveTargetDead();
			}
		}.start();
	}

	/**
	 * 停止完了待ち状態が終わり、ターゲットスレッドが終了するまで待機します。
	 */
	private synchronized void waitWhileStoppingAndJoinTarget() {
		while (offing) {
			try {
				wait();
			} catch (InterruptedException e) {}
		}

		//このスレッドに割り込まれても、Target実行スレッドが終了するまでは復帰しない
		while (currentTargetThread != null && currentTargetThread.isAlive()) {
			try {
				currentTargetThread.join();
			} catch (InterruptedException e) {}
		}
		currentTargetThread = null;
	}

	static abstract class Target implements Runnable {

		private Switch mySwitch;

		@Override
		public void run() {
			try {
				execute();
			} finally {
				mySwitch.receiveTargetDying();
			}
		}

		abstract String getName();

		abstract void execute();

		abstract void receiveTargetStarted();

		abstract void receiveTargetDead();
	}

	private static String getNextThreadName(String name) {
		synchronized (threadCounterMap) {
			int current;
			Integer counter = threadCounterMap.get(name);
			if (counter == null) {
				current = 0;
			} else {
				current = counter.intValue();
			}
			threadCounterMap.put(name, new Integer(current + 1));
			return "CircularFramework-" + name + "-" + current;
		}
	}
}
