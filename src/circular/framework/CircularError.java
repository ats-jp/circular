package circular.framework;

/**
 * データの一貫性が保てなくなった等、重大な障害が発生した場合にスローされるエラーです。
 * <p>
 * このエラーがスローされるとCircular Frameworkはすべての処理を中断し、エラー報告後停止します。
 * 
 * @author 千葉 哲嗣
 * @version $Name:  $
 */
@SuppressWarnings("serial")
public class CircularError extends Error {

	/**
	 * メッセージのみを持つインスタンスを生成します。
	 * 
	 * @param message 例外メッセージ
	 */
	public CircularError(String message) {
		super(message);
	}

	/**
	 * メッセージと原因となった例外を持つインスタンスを生成します。
	 * 
	 * @param message 例外メッセージ
	 * @param t 原因となる問題
	 */
	public CircularError(String message, Throwable t) {
		super(message, t);
	}

	/**
	 * 原因となる例外を持つインスタンスを生成します。
	 * 
	 * @param t 原因となる問題
	 */
	public CircularError(Throwable t) {
		super(t);
	}
}
