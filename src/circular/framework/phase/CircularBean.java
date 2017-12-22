package circular.framework.phase;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import circular.framework.CircularException;
import circular.framework.Cycle;

/**
 * Circular Frameworkが使用するワークテーブルの一行を表す抽象基底クラスです。
 * 
 * @author 千葉 哲嗣
 * @version $Name:  $
 */
public abstract class CircularBean implements Common {

	private String timestampColumnName;

	private String sequenceColumnName;

	private Timestamp timestamp;

	private BigInteger sequence;

	/**
	 * デフォルトコンストラクタです。
	 */
	public CircularBean() {
		timestampColumnName = getTimestampColumnName();
		sequenceColumnName = getSequenceColumnName();
	}

	/**
	 * {@link CircularBean#getTableName()}で返されるテーブル宛に、{@link CircularBean#setValuesTo(Inserter)}内でセットされた値をもつ行を追加します。
	 * <p>
	 * 出力先は他システムへの出力用ワークテーブルのあるDBとなります。
	 * 
	 * @throws CircularException DB関連のエラーが発生した場合
	 */
	public void insert() {
		Inserter inserter = createNewInserter();
		setValuesTo(inserter);
		try {
			inserter.executeInsert(Cycle.getOutputConnection());
		} catch (SQLException e) {
			throw new CircularException(e);
		}
	}

	/**
	 * このBeanが追加された順序を表すシーケンス番号を戻します。
	 * 
	 * @return シーケンス番号
	 */
	public BigInteger getSequence() {
		return sequence;
	}

	/**
	 * このBeanがテーブルに追加された時刻を返します。
	 * 
	 * @return 登録時刻
	 */
	public Timestamp getTimestamp() {
		return timestamp;
	}

	/**
	 * このBeanの文字列表現を返します。
	 * <p>
	 * このメソッドはあくまでも、テスト用として使用してください。
	 * 
	 * @return このBeanの文字列表現
	 */
	@Override
	public String toString() {
		Method[] methods = this.getClass().getMethods();
		Object[] parameters = new Object[0];
		StringBuffer buffer = new StringBuffer();
		for (Method method : methods) {
			if (method.getName().startsWith("get")) {
				try {
					buffer.append(method.getName()
						+ "()=["
						+ method.invoke(this, parameters)
						+ "], ");
				} catch (Exception e) {
					throw new Error(e);
				}
			}
		}
		return buffer.toString().substring(0, buffer.length() - 2);
	}

	/**
	 * このBeanに対応するワークテーブル名を返却します。
	 * 
	 * @return ワークテーブル名
	 */
	protected abstract String getTableName();

	/**
	 * {@link ResultSet}の持つ値をこのBeanに取り込みます。
	 * 
	 * @param result カーソルがこのBeanに対するレコードにある{@link ResultSet}
	 * @throws SQLException {@link ResultSet}操作時に例外が発生した場合
	 */
	protected abstract void initializeBean(ResultSet result)
		throws SQLException;

	/**
	 * {@link Inserter}に、このBeanの持つ値をセットします。
	 * 
	 * @param inserter INSERT用オブジェクト
	 */
	protected abstract void setValuesTo(Inserter inserter);

	/**
	 * {@link InputPhase}で読み込んだデータを、バックアップ領域に出力します。
	 * <p>
	 * 独自に生成したインスタンスでは登録時刻とシーケンス番号を持たないため、バックアップはできません。
	 * 
	 * @param connection バックアップに使用する接続
	 * @throws CircularException DB関連のエラーが発生した場合
	 */
	protected void backup(Connection connection) {
		Inserter inserter = createNewInserter();
		setValuesTo(inserter);
		try {
			inserter.executeBackup(connection);
		} catch (SQLException e) {
			throw new CircularException(e);
		}
	}

	/**
	 * {@link ResultSet}の持つ登録時刻とシーケンス番号を含む全ての値をこのBeanに取り込みます。
	 * 
	 * @param result カーソルがこのBeanに対するレコードにある{@link ResultSet}
	 * @throws SQLException {@link ResultSet}操作時に例外が発生した場合
	 */
	protected void init(ResultSet result) throws SQLException {
		timestamp = result.getTimestamp(timestampColumnName);
		sequence = result.getBigDecimal(sequenceColumnName).toBigInteger();
		initializeBean(result);
	}

	/**
	 * バックアップテーブル用の接尾語を返します。
	 * <p>
	 * 必要に応じてオーバーライドしてください。
	 * <p>
	 * デフォルトは _BACKUP です。
	 * 
	 * @return バックアップテーブル用の接尾語
	 */
	protected String getBackupTableSuffix() {
		return "_BACKUP";
	}

	/**
	 * このBeanが内部で使用する{@link Inserter}のインスタンスを生成します。
	 * <p>
	 * 独自の{@link Inserter}を使用する場合はオーバーライドしてください。
	 * 
	 * @return INSERT用オブジェクト
	 */
	protected Inserter createNewInserter() {
		return new Inserter();
	}

	private static String join(Collection values) {
		StringBuffer buffer = new StringBuffer();
		for (Object object : values) {
			buffer.append(object);
			buffer.append(", ");
		}
		String value = buffer.toString();
		if (value.length() == 0) return value;
		return value.substring(0, value.length() - 2);
	}

	/**
	 * 自システムワークテーブルに新規にデータを出力するためのツールクラスです。
	 * <p>
	 * {@link CircularBean}内でインスタンス化され、使用されます。振舞いを変更するにはオーバーライドする必要があります。
	 * 
	 * @author 千葉 哲嗣
	 * @version $Name:  $
	 */
	public class Inserter {

		private final List<String> columnNames = new ArrayList<String>();

		private final List<Binder> binders = new ArrayList<Binder>();

		/**
		 * このクラスのコンストラクタです。
		 */
		protected Inserter() {}

		/**
		 * 指定された項目に、指定された値を数値型として登録します。
		 * @param columnName 数値型項目の名称
		 * @param value 値
		 */
		public void addInt(String columnName, int value) {
			columnNames.add(columnName);
			binders.add(new IntBinder(value));
		}

		/**
		 * 指定された項目に、指定された値を数値型として登録します。
		 * @param columnName 数値型項目の名称
		 * @param value 値
		 */
		public void addBigDecimal(String columnName, BigDecimal value) {
			columnNames.add(columnName);
			binders.add(new BigDecimalBinder(value));
		}

		/**
		 * 指定された項目に、指定された値を文字列型として登録します。
		 * @param columnName 文字列型項目の名称
		 * @param value 値
		 */
		public void addString(String columnName, String value) {
			columnNames.add(columnName);
			binders.add(new StringBinder(value));
		}

		/**
		 * 指定された項目に、指定された値を日付型として登録します。
		 * @param columnName 日付型項目の名称
		 * @param value 値
		 */
		public void addDate(String columnName, Date value) {
			columnNames.add(columnName);
			binders.add(new DateBinder(value));
		}

		/**
		 * 指定された項目に、指定された値をタイムスタンプ型として登録します。
		 * @param columnName タイムスタンプ型項目の名称
		 * @param value 値
		 */
		public void addTimestamp(String columnName, Timestamp value) {
			columnNames.add(columnName);
			binders.add(new TimestampBinder(value));
		}

		/**
		 * INSERTを実行します。
		 * 
		 * @param connection INSERTを実行する接続
		 * @throws SQLException {@link Connection}操作時に例外が発生した場合
		 */
		protected void executeInsert(Connection connection) throws SQLException {
			String sql = "insert into "
				+ getTableName()
				+ " ("
				+ getColumnNames()
				+ ", "
				+ timestampColumnName
				+ ") values ("
				+ getPlaceHolders()
				+ ", SYSDATE)";

			execute(connection.prepareStatement(sql));
		}

		/**
		 * バックアップを実行します。
		 * 
		 * @param connection バックアップを実行する接続
		 * @throws SQLException {@link Connection}操作時に例外が発生した場合
		 */
		protected void executeBackup(Connection connection) throws SQLException {
			String sql = "insert into "
				+ getTableName()
				+ getBackupTableSuffix()
				+ " ("
				+ getColumnNames()
				+ ", "
				+ timestampColumnName
				+ ", "
				+ sequenceColumnName
				+ ") values ("
				+ getPlaceHolders()
				+ ", ?, ?)";

			binders.add(new TimestampBinder(timestamp));
			binders.add(new StringBinder(sequence.toString()));

			execute(connection.prepareStatement(sql));
		}

		/**
		 * 現在保持している項目をSQL内で使用するようにカンマで連結した文字列を返します。
		 * 
		 * @return カンマで連結した項目名
		 */
		protected String getColumnNames() {
			return join(columnNames);
		}

		/**
		 * 現在保持している値をプレースホルダとしてSQL内で使用するようにカンマで連結した文字列を返します。
		 * 
		 * @return カンマで連結したプレースホルダ
		 */
		protected String getPlaceHolders() {
			return join(binders);
		}

		/**
		 * 値が未バインドの{@link PreparedStatement}に、保持する値をバインドし、SQLを実行します。
		 * 
		 * @param statement 値が未バインドの{@link PreparedStatement}
		 * @throws SQLException {@link PreparedStatement}操作時に例外が発生した場合
		 */
		protected void execute(PreparedStatement statement) throws SQLException {
			try {
				int size = binders.size();
				for (int i = 0; i < size; i++) {
					binders.get(i).bind(i + 1, statement);
				}

				statement.executeUpdate();
			} finally {
				statement.close();
			}
		}
	}

	/**
	 * {@link PreparedStatement}にバインドする値を保持するための抽象基底クラスです。
	 */
	public static abstract class Binder {

		/**
		 * {@link PreparedStatement}に対して、型に対応したメソッドに保持する値を設定します。
		 * <p>
		 * {@link SQLException}がスローされると、フレームワーク側で回復不可能な例外として扱われます。
		 * @param index {@link PreparedStatement}のプレースホルダ位置
		 * @param statement 対象となる{@link PreparedStatement}
		 * @throws SQLException 回復不可能な例外が発生した場合
		 */
		abstract void bind(int index, PreparedStatement statement)
			throws SQLException;

		/**
		 * このクラスのインスタンスを文字列として評価すると、SQLプレースホルダの"?"となります。
		 * @return プレースホルダ
		 */
		@Override
		public String toString() {
			return "?";
		}
	}

	/**
	 * {@link PreparedStatement}にバインドする整数型の値を保持するためのクラスです。
	 */
	public static class IntBinder extends Binder {

		private final int value;

		/**
		 * このクラスのコンストラクタです。
		 * 
		 * @param value このBinderの値
		 */
		public IntBinder(int value) {
			this.value = value;
		}

		@Override
		void bind(int index, PreparedStatement statement) throws SQLException {
			statement.setInt(index, value);
		}
	}

	/**
	 * {@link PreparedStatement}にバインドする数値型の値を保持するためのクラスです。
	 */
	public static class BigDecimalBinder extends Binder {

		private final BigDecimal value;

		/**
		 * このクラスのコンストラクタです。
		 * 
		 * @param value このBinderの値
		 */
		public BigDecimalBinder(BigDecimal value) {
			this.value = value;
		}

		@Override
		void bind(int index, PreparedStatement statement) throws SQLException {
			statement.setBigDecimal(index, value);
		}
	}

	/**
	 * {@link PreparedStatement}にバインドする文字列型の値を保持するためのクラスです。
	 */
	public static class StringBinder extends Binder {

		private final String value;

		/**
		 * このクラスのコンストラクタです。
		 * 
		 * @param value このBinderの値
		 */
		public StringBinder(String value) {
			this.value = value;
		}

		@Override
		void bind(int index, PreparedStatement statement) throws SQLException {
			statement.setString(index, value);
		}
	}

	/**
	 * {@link PreparedStatement}にバインドする日付型の値を保持するためのクラスです。
	 */
	public static class DateBinder extends Binder {

		private final Date value;

		/**
		 * このクラスのコンストラクタです。
		 * 
		 * @param value このBinderの値
		 */
		public DateBinder(Date value) {
			this.value = value;
		}

		@Override
		void bind(int index, PreparedStatement statement) throws SQLException {
			statement.setDate(index, value);
		}
	}

	/**
	 * {@link PreparedStatement}にバインドするTIMESTAMP型の値を保持するためのクラスです。
	 */
	public static class TimestampBinder extends Binder {

		private final Timestamp value;

		/**
		 * このクラスのコンストラクタです。
		 * 
		 * @param value このBinderの値
		 */
		public TimestampBinder(Timestamp value) {
			this.value = value;
		}

		@Override
		void bind(int index, PreparedStatement statement) throws SQLException {
			statement.setTimestamp(index, value);
		}
	}
}
