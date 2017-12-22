package circular.framework;

/**
 * 回復不可能な例外が発生した場合スローされる例外です。
 * <p>
 * この例外がスローされると、Circular Frameworkはサイクル中でも処理を中断し、次のサイクルへ遷移します。
 * <br>
 * 運用上、極力Circular Frameworkを停止させたくない場合は、この例外をスローするようにしてください。
 * 
 * @author 千葉 哲嗣
 * @version $Name:  $
 */
@SuppressWarnings("serial")
public class CircularException extends RuntimeException {

	/**
	 * メッセージのみを持つインスタンスを生成します。
	 * 
	 * @param message 例外メッセージ
	 */
	public CircularException(String message) {
		super(message);
	}

	/**
	 * メッセージと原因となった例外を持つインスタンスを生成します。
	 * 
	 * @param message 例外メッセージ
	 * @param e 原因となる例外
	 */
	public CircularException(String message, Exception e) {
		super(message, e);
	}

	/**
	 * 原因となる例外を持つインスタンスを生成します。
	 * 
	 * @param e 原因となる例外
	 */
	public CircularException(Exception e) {
		super(e);
	}
}
