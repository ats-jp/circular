package circular.framework;

/**
 * @author ��t �N�k
 * @version $Name:  $
 */
class ErrorUtils {

	private ErrorUtils() {}

	static void notifyMessagelessError(
		Observer observer,
		Class clazz,
		Throwable t) {
		observer.receiveError(
			clazz.getName() + "���s���ɖ�肪�������܂����B" + formalize(t.getMessage()),
			strip(t));
	}

	static void notifyMessagelessFatalError(
		Observer observer,
		Class clazz,
		Throwable t) {
		observer.receiveFatalError(clazz.getName()
			+ "���s���ɖ�肪�������܂����B"
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
