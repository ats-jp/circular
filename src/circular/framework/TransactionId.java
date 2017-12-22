package circular.framework;

/**
 * {@link ControlServer}が、クライアントからの接続を受け付けるたびにカウントアップする、一意なIDを表すクラスです。
 * <p>
 * IDはこのクラスがロードされたときからカウントアップされます。
 * 
 * @author 千葉 哲嗣
 * @version $Name:  $
 */
public class TransactionId {

	private static final Object idLock = new Object();

	private static long globalId = 0;

	private final long id;

	TransactionId() {
		synchronized (idLock) {
			id = globalId++;
		}
	}

	/**
	 * TransactionIdの文字列表現を定義しています。
	 * 
	 * @return TransactionIdの文字列表現
	 */
	@Override
	public String toString() {
		return "tid[" + id + "]";
	}
}
