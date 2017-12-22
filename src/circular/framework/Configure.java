package circular.framework;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Circular Frameworkの設定情報を保持するクラスです。
 * 
 * @author 千葉 哲嗣
 * @version $Name:  $
 */
public class Configure {

	/**
	 * 他システム入力設定名
	 */
	public static final String INPUT_NAME = "input";

	/**
	 * 自システム出力設定名
	 */
	public static final String OUTPUT_NAME = "output";

	/**
	 * 自システム設定名
	 */
	public static final String OUR_NAME = "our";

	/**
	 * バックアップ設定名
	 */
	public static final String BACKUP_NAME = "backup";

	/**
	 * JDBC設定用キー接頭子
	 */
	public static final String JDBC_KEYPREFIX = "jdbc.";

	/**
	 * JDBC設定クラス用接尾子
	 */
	public static final String JDBC_KEYSUFFIX_CLASS = ".class";

	/**
	 * JDBC設定URL用接尾子
	 */
	public static final String JDBC_KEYSUFFIX_URL = ".url";

	/**
	 * JDBC設定ユーザ名用接尾子
	 */
	public static final String JDBC_KEYSUFFIX_USERNAME = ".username";

	/**
	 * JDBC設定パスワード用接尾子
	 */
	public static final String JDBC_KEYSUFFIX_PASSWORD = ".password";

	/**
	 * JDBC設定パスワード用接尾子
	 */
	public static final String JDBC_KEYSUFFIX_TESTSQL = ".testsql";

	/**
	 * 設定表題のキー
	 */
	public static final String CONFIGURE_TITLE_KEY = "configure.title";

	/**
	 * {@link circular.framework.Phase}実装クラスFQCN（カンマ区切りで複数指定可）のキー
	 */
	public static final String PHASE_CLASSES_KEY = "phase.classes";

	/**
	 * サイクルのデフォルト間隔（分）のキー
	 */
	public static final String CYCLE_INTERVAL_MINUTES_KEY = "cycle.interval.minutes";

	/**
	 * サイクル方法定義クラスFQCNのキー
	 */
	public static final String CYCLE_STRATEGY_CLASS_KEY = "cycle.strategy.class";

	/**
	 * {@link ControlServer}を使用する／しない
	 */
	public static final String USE_CONTROLSERVER = "use.controlserver";

	/**
	 * {@link ControlServer}アドレスのキー
	 */
	public static final String CONTROLSERVER_ADDRESS_KEY = "controlserver.address";

	/**
	 * {@link circular.framework.ControlServer}ポート番号のキー
	 */
	public static final String CONTROLSERVER_PORT_KEY = "controlserver.port";

	/**
	 * {@link circular.framework.ControlServer}待ち時間（秒）のキー
	 */
	public static final String CONTROLSERVER_TIMEOUT_SECONDS_KEY = "controlserver.timeout.seconds";

	/**
	 * {@link circular.framework.ControlServer}終了チェック間隔（ミリ秒）のキー
	 */
	public static final String CONTROLSERVER_INTERVAL_MILLISECONDS_KEY = "controlserver.interval.milliseconds";

	/**
	 * 使用する／しない設定用キー接頭子
	 */
	public static final String USE_KEYPREFIX = "use.";

	final Observer observer;

	final JDBC inputDriver;

	final JDBC outputDriver;

	final JDBC ourDriver;

	final JDBC backupDriver;

	final Phase[] phases;

	final CycleStrategy cycleStrategy;

	final boolean useControlServer;

	final String controlServerAddress;

	final int controlServerPort;

	final int controlServerIntervalMillis;

	final int controlServerTimeoutSeconds;

	private final String title;

	/**
	 * 唯一のコンストラクタです。
	 * 
	 * @param properties この設定の元情報をもつ{@link CircularProperties} 
	 * @param observer この設定に対する{@link Observer}
	 */
	public Configure(CircularProperties properties, Observer observer) {
		this.observer = observer;

		if (properties.containsKey(CONFIGURE_TITLE_KEY)) {
			title = properties.getProperty(CONFIGURE_TITLE_KEY);
		} else {
			title = null;
		}

		phases = createInstances(
			PHASE_CLASSES_KEY,
			properties.getProperty(PHASE_CLASSES_KEY));

		inputDriver = new JDBC(INPUT_NAME, properties);
		outputDriver = new JDBC(OUTPUT_NAME, properties);
		ourDriver = new JDBC(OUR_NAME, properties);
		backupDriver = new JDBC(BACKUP_NAME, properties);

		cycleStrategy = (CycleStrategy) createInstance(
			CYCLE_STRATEGY_CLASS_KEY,
			loadClass(
				CYCLE_STRATEGY_CLASS_KEY,
				properties.getProperty(CYCLE_STRATEGY_CLASS_KEY)));

		cycleStrategy.setCurrentIntervalMinutes(Integer.parseInt(properties.getProperty(CYCLE_INTERVAL_MINUTES_KEY)));
		cycleStrategy.setObserver(observer);

		useControlServer = Boolean.valueOf(
			properties.getProperty(USE_CONTROLSERVER)).booleanValue();

		if (useControlServer) {
			controlServerAddress = properties.getProperty(CONTROLSERVER_ADDRESS_KEY);
			controlServerPort = Integer.parseInt(properties.getProperty(CONTROLSERVER_PORT_KEY));
			controlServerIntervalMillis = Integer.parseInt(properties.getProperty(CONTROLSERVER_INTERVAL_MILLISECONDS_KEY));
			controlServerTimeoutSeconds = Integer.parseInt(properties.getProperty(CONTROLSERVER_TIMEOUT_SECONDS_KEY));
		} else {
			controlServerAddress = null;
			controlServerPort = 0;
			controlServerIntervalMillis = 0;
			controlServerTimeoutSeconds = 0;
		}
	}

	/**
	 * この設定の、定義された名称または表題を返却します。
	 * 
	 * @return 名称または表題
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * サイクルの実行間隔を設定します。
	 * <p>
	 * 指定された値は、分単位として解釈されます。
	 * 
	 * @param minutes サイクルの実行間隔（分）
	 */
	public void setIntervalMinutes(int minutes) {
		cycleStrategy.setCurrentIntervalMinutes(minutes);
		observer.receiveIntervalMinutesChanged(minutes);
	}

	/**
	 * 現在のサイクルの実行間隔を返却します。
	 * <p>
	 * 返される値は、分単位です。
	 * 
	 * @return 現在のサイクルの実行間隔
	 */
	public int getIntervalMinutes() {
		return cycleStrategy.getCurrentIntervalMinutes();
	}

	/**
	 * {@link ControlServer}を使用するかどうかを返却します。
	 * 
	 * @return {@link ControlServer}を使用する場合、true
	 */
	public boolean useControlServer() {
		return useControlServer;
	}

	/**
	 * 他システム入力用接続を使用するかどうかを返却します。
	 * 
	 * @return 他システム入力用接続を使用する場合、true
	 */
	public boolean useInput() {
		return inputDriver.use;
	}

	/**
	 * 自システム出力用接続を使用するかどうかを返却します。
	 * 
	 * @return 自システム出力用接続を使用する場合、true
	 */
	public boolean useOutput() {
		return outputDriver.use;
	}

	/**
	 * 自システム用接続を使用するかどうかを返却します。
	 * 
	 * @return 自システム用接続を使用する場合、true
	 */
	public boolean useOur() {
		return ourDriver.use;
	}

	/**
	 * バックアップ用接続を使用するかどうかを返却します。
	 * 
	 * @return バックアップ用接続を使用する場合、true
	 */
	public boolean useBackup() {
		return backupDriver.use;
	}

	/**
	 * 入力用接続の情報を返却します。
	 * 
	 * @return 入力用接続の情報
	 */
	public String getInputDriverInformation() {
		return inputDriver.toString();
	}

	/**
	 * 出力用接続の情報を返却します。
	 * 
	 * @return 出力用接続の情報
	 */
	public String getOutputDriverInformation() {
		return outputDriver.toString();
	}

	/**
	 * 自システム用接続の情報を返却します。
	 * 
	 * @return 自システム用接続の情報
	 */
	public String getOurDriverInformation() {
		return ourDriver.toString();
	}

	/**
	 * バックアップ用接続の情報を返却します。
	 * 
	 * @return バックアップ用接続の情報
	 */
	public String getBackupDriverInformation() {
		return backupDriver.toString();
	}

	/**
	 * 入力用接続をテストします。
	 * 
	 * @return テストの成否
	 */
	public boolean testInputDriver() {
		return inputDriver.test();
	}

	/**
	 * 出力用接続をテストします。
	 * 
	 * @return テストの成否
	 */
	public boolean testOutputDriver() {
		return outputDriver.test();
	}

	/**
	 * 自システム用接続をテストします。
	 * 
	 * @return テストの成否
	 */
	public boolean testOurDriver() {
		return ourDriver.test();
	}

	/**
	 * バックアップ用接続の情報を返却します。
	 * 
	 * @return テストの成否
	 */
	public boolean testBackupDriver() {
		return backupDriver.test();
	}

	private static Phase[] createInstances(String entryName, String classes) {
		StringTokenizer tokenizer = new StringTokenizer(classes, ",");
		List<Phase> list = new LinkedList<Phase>();
		while (tokenizer.hasMoreTokens()) {
			list.add((Phase) createInstance(
				entryName,
				loadClass(entryName, tokenizer.nextToken())));
		}
		return list.toArray(new Phase[list.size()]);
	}

	private static Class loadClass(String entryName, String className) {
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException(entryName
				+ "の["
				+ className
				+ "]が見つかりません。");
		}
	}

	private static Object createInstance(String key, Class clazz) {
		try {
			return clazz.newInstance();
		} catch (Exception e) {
			throw new IllegalStateException(key
				+ "の"
				+ clazz.getName()
				+ "のインスタンスを作ることができません。", e);
		}
	}

	class JDBC {

		private final String type;

		private final boolean use;

		private final String url;

		private final String username;

		private final String password;

		private final String testSQL;

		private JDBC(String type, CircularProperties properties) {
			this.type = type;

			use = Boolean.valueOf(properties.getProperty(USE_KEYPREFIX + type))
				.booleanValue();

			if (use) {
				String key = JDBC_KEYPREFIX + type + JDBC_KEYSUFFIX_CLASS;
				loadClass(key, properties.getProperty(key));
				url = properties.getProperty(JDBC_KEYPREFIX
					+ type
					+ JDBC_KEYSUFFIX_URL);
				username = properties.getProperty(JDBC_KEYPREFIX
					+ type
					+ JDBC_KEYSUFFIX_USERNAME);
				password = properties.getProperty(JDBC_KEYPREFIX
					+ type
					+ JDBC_KEYSUFFIX_PASSWORD);
				testSQL = properties.getProperty(JDBC_KEYPREFIX
					+ type
					+ JDBC_KEYSUFFIX_TESTSQL);
			} else {
				url = null;
				username = null;
				password = null;
				testSQL = null;
			}
		}

		@Override
		public String toString() {
			String base = "[type=" + type;
			if (!use) return base + ", 未使用]";
			return base + ", url=" + url + ", username=" + username + "]";
		}

		Connection getConnection() throws SQLException {
			if (!use) return null;
			Connection connection = DriverManager.getConnection(
				url,
				username,
				password);
			connection.setAutoCommit(false);
			return connection;
		}

		String getInformation() {
			return type + " " + toString();
		}

		boolean test() {
			if (!use) return true;

			try {
				Connection connection = null;
				try {
					connection = getConnection();

					//testSQLが設定されていない場合、接続作成のみで成功とする
					if (testSQL == null || testSQL.trim().equals("")) return !connection.isClosed();

					ResultSet result = getConnection().createStatement()
						.executeQuery(testSQL);
					while (result.next()) {}
					result.close();
				} finally {
					if (connection != null) connection.close();
				}

				return true;
			} catch (Exception e) {
				observer.receiveError(type + " のテストでエラーが発生しました。", e);
				return false;
			}
		}
	}
}
