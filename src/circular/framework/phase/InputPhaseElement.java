package circular.framework.phase;

import circular.framework.AbortNotice;

/**
 * {@link InputPhase}が表すワークテーブルの中の、一件のデータに対応した処理を表します。
 * 
 * @author 千葉 哲嗣
 * @version $Name:  $
 */
public interface InputPhaseElement {

	/**
	 * 定義された処理を実行します。
	 * 
	 * @param bean 一件のデータ表すBean
	 * @throws InterruptedException この処理の内部で{@link Thread#sleep(long)}等を行っている場合で、かつユーザから停止が指示（{@link Thread#interrupt()}がフレームワークにより実行）された場合に発生
	 * @throws AbortNotice InputPhaseElement自ら{@link Cycle}を中断したい場合
	 */
	void execute(CircularBean bean) throws InterruptedException, AbortNotice;
}
