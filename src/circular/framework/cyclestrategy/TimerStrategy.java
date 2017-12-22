package circular.framework.cyclestrategy;

import java.util.Calendar;

import circular.framework.Cycle;
import circular.framework.CycleStrategy;

/**
 * 等間隔停止サイクルを実現する{@link CycleStrategy}です。
 * <p>
 * 1サイクル実行後、次の開始時刻まで停止します。開始時刻の間隔を指定することができます。
 * <p>
 * メリット:遅延が発生している場合一時停止しないため処理の遅れを解消する可能性があります。
 * <br>
 * デメリット:1サイクルに指定間隔以上の時間がかかった場合、負荷をかけ続ける可能性があります。
 * 
 * @author 千葉 哲嗣
 * @version $Name:  $
 */
public class TimerStrategy extends CycleStrategy {

	private static final int sleepMillis = 1 * 1000;

	/**
	 * 1サイクル実行後、次の開始時刻まで停止します。開始時刻の間隔を指定することができます。
	 * 
	 * @param cycle 1サイクル
	 * @throws InterruptedException ユーザから停止が指示（{@link Thread#interrupt()}がフレームワークにより実行）された場合に発生
	 */
	@Override
	public void execute(Cycle cycle) throws InterruptedException {
		Calendar next = Calendar.getInstance();
		while (true) {
			checkInterrupted();
			cycle.execute();
			next.add(Calendar.MINUTE, getCurrentIntervalMinutes());
			notifyNextCycleScheduleToObserver(next.getTime());
			while (next.after(Calendar.getInstance())) {
				Thread.sleep(sleepMillis);
			}
		}
	}
}
