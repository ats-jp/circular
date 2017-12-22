package circular.framework.cyclestrategy;

import java.util.Calendar;

import circular.framework.Cycle;
import circular.framework.CycleStrategy;

/**
 * 一定間隔停止サイクルを実現する{@link CycleStrategy}です。
 * <p>
 * 1サイクル実行後、必ず指定された分数間停止します。
 * <p>
 * メリット:必ず指定数分間停止するため、負荷をかけ続ける事はありません。
 * <br>
 * デメリット:1サイクルに時間がかかった場合に遅延が発生し、処理の遅れるデータが発生する可能性があります。
 * 
 * @author 千葉 哲嗣
 * @version $Name:  $
 */
public class RestStrategy extends CycleStrategy {

	/**
	 * 1サイクル実行後、必ず指定された分数間停止します。
	 * 
	 * @param cycle 1サイクル
	 * @throws InterruptedException ユーザから停止が指示（{@link Thread#interrupt()}がフレームワークにより実行）された場合に発生
	 */
	@Override
	public void execute(Cycle cycle) throws InterruptedException {
		while (true) {
			checkInterrupted();
			cycle.execute();
			int next = getCurrentIntervalMinutes() * 60 * 1000;
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.MILLISECOND, next);
			notifyNextCycleScheduleToObserver(calendar.getTime());
			Thread.sleep(next);
		}
	}
}
