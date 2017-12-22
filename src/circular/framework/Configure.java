package circular.framework;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Circular Framework�̐ݒ����ێ�����N���X�ł��B
 * 
 * @author ��t �N�k
 * @version $Name:  $
 */
public class Configure {

	/**
	 * ���V�X�e�����͐ݒ薼
	 */
	public static final String INPUT_NAME = "input";

	/**
	 * ���V�X�e���o�͐ݒ薼
	 */
	public static final String OUTPUT_NAME = "output";

	/**
	 * ���V�X�e���ݒ薼
	 */
	public static final String OUR_NAME = "our";

	/**
	 * �o�b�N�A�b�v�ݒ薼
	 */
	public static final String BACKUP_NAME = "backup";

	/**
	 * JDBC�ݒ�p�L�[�ړ��q
	 */
	public static final String JDBC_KEYPREFIX = "jdbc.";

	/**
	 * JDBC�ݒ�N���X�p�ڔ��q
	 */
	public static final String JDBC_KEYSUFFIX_CLASS = ".class";

	/**
	 * JDBC�ݒ�URL�p�ڔ��q
	 */
	public static final String JDBC_KEYSUFFIX_URL = ".url";

	/**
	 * JDBC�ݒ胆�[�U���p�ڔ��q
	 */
	public static final String JDBC_KEYSUFFIX_USERNAME = ".username";

	/**
	 * JDBC�ݒ�p�X���[�h�p�ڔ��q
	 */
	public static final String JDBC_KEYSUFFIX_PASSWORD = ".password";

	/**
	 * JDBC�ݒ�p�X���[�h�p�ڔ��q
	 */
	public static final String JDBC_KEYSUFFIX_TESTSQL = ".testsql";

	/**
	 * �ݒ�\��̃L�[
	 */
	public static final String CONFIGURE_TITLE_KEY = "configure.title";

	/**
	 * {@link circular.framework.Phase}�����N���XFQCN�i�J���}��؂�ŕ����w��j�̃L�[
	 */
	public static final String PHASE_CLASSES_KEY = "phase.classes";

	/**
	 * �T�C�N���̃f�t�H���g�Ԋu�i���j�̃L�[
	 */
	public static final String CYCLE_INTERVAL_MINUTES_KEY = "cycle.interval.minutes";

	/**
	 * �T�C�N�����@��`�N���XFQCN�̃L�[
	 */
	public static final String CYCLE_STRATEGY_CLASS_KEY = "cycle.strategy.class";

	/**
	 * {@link ControlServer}���g�p����^���Ȃ�
	 */
	public static final String USE_CONTROLSERVER = "use.controlserver";

	/**
	 * {@link ControlServer}�A�h���X�̃L�[
	 */
	public static final String CONTROLSERVER_ADDRESS_KEY = "controlserver.address";

	/**
	 * {@link circular.framework.ControlServer}�|�[�g�ԍ��̃L�[
	 */
	public static final String CONTROLSERVER_PORT_KEY = "controlserver.port";

	/**
	 * {@link circular.framework.ControlServer}�҂����ԁi�b�j�̃L�[
	 */
	public static final String CONTROLSERVER_TIMEOUT_SECONDS_KEY = "controlserver.timeout.seconds";

	/**
	 * {@link circular.framework.ControlServer}�I���`�F�b�N�Ԋu�i�~���b�j�̃L�[
	 */
	public static final String CONTROLSERVER_INTERVAL_MILLISECONDS_KEY = "controlserver.interval.milliseconds";

	/**
	 * �g�p����^���Ȃ��ݒ�p�L�[�ړ��q
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
	 * �B��̃R���X�g���N�^�ł��B
	 * 
	 * @param properties ���̐ݒ�̌���������{@link CircularProperties} 
	 * @param observer ���̐ݒ�ɑ΂���{@link Observer}
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
	 * ���̐ݒ�́A��`���ꂽ���̂܂��͕\���ԋp���܂��B
	 * 
	 * @return ���̂܂��͕\��
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * �T�C�N���̎��s�Ԋu��ݒ肵�܂��B
	 * <p>
	 * �w�肳�ꂽ�l�́A���P�ʂƂ��ĉ��߂���܂��B
	 * 
	 * @param minutes �T�C�N���̎��s�Ԋu�i���j
	 */
	public void setIntervalMinutes(int minutes) {
		cycleStrategy.setCurrentIntervalMinutes(minutes);
		observer.receiveIntervalMinutesChanged(minutes);
	}

	/**
	 * ���݂̃T�C�N���̎��s�Ԋu��ԋp���܂��B
	 * <p>
	 * �Ԃ����l�́A���P�ʂł��B
	 * 
	 * @return ���݂̃T�C�N���̎��s�Ԋu
	 */
	public int getIntervalMinutes() {
		return cycleStrategy.getCurrentIntervalMinutes();
	}

	/**
	 * {@link ControlServer}���g�p���邩�ǂ�����ԋp���܂��B
	 * 
	 * @return {@link ControlServer}���g�p����ꍇ�Atrue
	 */
	public boolean useControlServer() {
		return useControlServer;
	}

	/**
	 * ���V�X�e�����͗p�ڑ����g�p���邩�ǂ�����ԋp���܂��B
	 * 
	 * @return ���V�X�e�����͗p�ڑ����g�p����ꍇ�Atrue
	 */
	public boolean useInput() {
		return inputDriver.use;
	}

	/**
	 * ���V�X�e���o�͗p�ڑ����g�p���邩�ǂ�����ԋp���܂��B
	 * 
	 * @return ���V�X�e���o�͗p�ڑ����g�p����ꍇ�Atrue
	 */
	public boolean useOutput() {
		return outputDriver.use;
	}

	/**
	 * ���V�X�e���p�ڑ����g�p���邩�ǂ�����ԋp���܂��B
	 * 
	 * @return ���V�X�e���p�ڑ����g�p����ꍇ�Atrue
	 */
	public boolean useOur() {
		return ourDriver.use;
	}

	/**
	 * �o�b�N�A�b�v�p�ڑ����g�p���邩�ǂ�����ԋp���܂��B
	 * 
	 * @return �o�b�N�A�b�v�p�ڑ����g�p����ꍇ�Atrue
	 */
	public boolean useBackup() {
		return backupDriver.use;
	}

	/**
	 * ���͗p�ڑ��̏���ԋp���܂��B
	 * 
	 * @return ���͗p�ڑ��̏��
	 */
	public String getInputDriverInformation() {
		return inputDriver.toString();
	}

	/**
	 * �o�͗p�ڑ��̏���ԋp���܂��B
	 * 
	 * @return �o�͗p�ڑ��̏��
	 */
	public String getOutputDriverInformation() {
		return outputDriver.toString();
	}

	/**
	 * ���V�X�e���p�ڑ��̏���ԋp���܂��B
	 * 
	 * @return ���V�X�e���p�ڑ��̏��
	 */
	public String getOurDriverInformation() {
		return ourDriver.toString();
	}

	/**
	 * �o�b�N�A�b�v�p�ڑ��̏���ԋp���܂��B
	 * 
	 * @return �o�b�N�A�b�v�p�ڑ��̏��
	 */
	public String getBackupDriverInformation() {
		return backupDriver.toString();
	}

	/**
	 * ���͗p�ڑ����e�X�g���܂��B
	 * 
	 * @return �e�X�g�̐���
	 */
	public boolean testInputDriver() {
		return inputDriver.test();
	}

	/**
	 * �o�͗p�ڑ����e�X�g���܂��B
	 * 
	 * @return �e�X�g�̐���
	 */
	public boolean testOutputDriver() {
		return outputDriver.test();
	}

	/**
	 * ���V�X�e���p�ڑ����e�X�g���܂��B
	 * 
	 * @return �e�X�g�̐���
	 */
	public boolean testOurDriver() {
		return ourDriver.test();
	}

	/**
	 * �o�b�N�A�b�v�p�ڑ��̏���ԋp���܂��B
	 * 
	 * @return �e�X�g�̐���
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
				+ "��["
				+ className
				+ "]��������܂���B");
		}
	}

	private static Object createInstance(String key, Class clazz) {
		try {
			return clazz.newInstance();
		} catch (Exception e) {
			throw new IllegalStateException(key
				+ "��"
				+ clazz.getName()
				+ "�̃C���X�^���X����邱�Ƃ��ł��܂���B", e);
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
			if (!use) return base + ", ���g�p]";
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

					//testSQL���ݒ肳��Ă��Ȃ��ꍇ�A�ڑ��쐬�݂̂Ő����Ƃ���
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
				observer.receiveError(type + " �̃e�X�g�ŃG���[���������܂����B", e);
				return false;
			}
		}
	}
}
