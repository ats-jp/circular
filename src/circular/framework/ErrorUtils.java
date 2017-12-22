package circular.framework;

/**
 * @author 千葉 哲嗣
 * @version $Name:  $
 */
class ErrorUtils {

	private ErrorUtils() {}

	static void notifyMessagelessError(
		Observer observer,
		Class clazz,
		Throwable t) {
		observer.receiveError(
			clazz.getName() + "実行中に問題が発生しました。" + formalize(t.getMessage()),
			strip(t));
	}

	static void notifyMessagelessFatalError(
		Observer observer,
		Class clazz,
		Throwable t) {
		observer.receiveFatalError(clazz.getName()
			+ "実行中に問題が発生しました。"
			+ formalize(t.getMessage()), strip(t));
	}

	private static String formalize(String message) {
		return message == null ? "" : message;
	}

	private static Throwable strip(Throwable t) {
		Throwable cause = t.getCause();
		if (cause == null) return t;
		return strip(cause);
	}
}
