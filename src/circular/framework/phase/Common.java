package circular.framework.phase;

/**
 * {@link CircularBean}に関係するクラスに共通するものを定義したインターフェイスです。
 * 
 * @author 千葉 哲嗣
 * @version $Name:  $
 */
public interface Common {

	/**
	 * シーケンス番号用項目名を返却します。
	 * 
	 * @return シーケンス番号用項目名
	 */
	String getSequenceColumnName();

	/**
	 * 登録時刻用項目名を返却します。
	 * 
	 * @return 登録時刻用項目名
	 */
	String getTimestampColumnName();
}
