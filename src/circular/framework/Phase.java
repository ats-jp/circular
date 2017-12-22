package circular.framework;

/**
 * ポーリング処理における、汎用処理を表します。
 * <p>
 * 汎用処理は処理本体を{@link Phase#execute()}に定義するという以外は、特に制約はありません。
 *
 * @author 千葉 哲嗣
 * @version $Name:  $
 */
public interface Phase {

	/**
	 * 実行状況観察用にこのPhaseの名称を返します。
	 * 
	 * @return このPhaseの名称
	 */
	String getName();

	/**
	 * 汎用処理を実行します。
	 * 
	 * @throws InterruptedException この処理の内部で{@link Thread#sleep(long)}等を行っている場合で、かつユーザから停止が指示（{@link Thread#interrupt()}がフレームワークにより実行）された場合に発生
	 * @throws AbortNotice 自ら{@link Cycle}を中断したい場合
	 */
	void execute() throws InterruptedException, AbortNotice;
}
