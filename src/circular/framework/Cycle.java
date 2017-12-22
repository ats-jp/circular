package circular.framework;

import java.sql.Connection;
import java.sql.SQLException;

import circular.framework.Configure.JDBC;

/**
 * 1サイクルの処理を表すクラスです。
 * 
 * @author 千葉 哲嗣
 * @version $Name:  $
 */
public class Cycle {

	private static final ThreadLocal<Configure> configHolder = new ThreadLocal<Configure>();

	private static final ThreadLocal<Connection> inputConnectionHolder = new ThreadLocal<Connection>();

	private static final ThreadLocal<Connection> outputConnectionHolder = new ThreadLocal<Connection>();

	private static final ThreadLocal<Connection> ourConnectionHolder = new ThreadLocal<Connection>();

	private static final ThreadLocal<Connection> backupConnectionHolder = new ThreadLocal<Connection>();

	private final Configure config;

	/**
	 * 自システムへの入力用ワークテーブルのあるDBへの接続を取得します。
	 * <p>
	 * 入力用のDBは、他システム上にあるものと想定しているため、他システムが停止している場合、サイクル外から呼び出した場合はnullが返却されます。
	 * <p>
	 * nullが返却された場合は、プラグインでの処理はスキップするようにしてください。
	 * 
	 * @return 入力用ワークテーブルのあるDBへの接続
	 */
	public static Connection getInputConnection() {
		return inputConnectionHolder.get();
	}

	/**
	 * 他システムへの出力用ワークテーブルのあるDBへの接続を取得します。
	 * <p>
	 * このメソッドがpublic staticであることに注意してください。つまり、このメソッドは、どこからでも呼び出すことが可能ですが、接続が取得できるのは、サイクル内、つまり{@link Cycle#execute()}内だけです。そのほかの場所から取得しようとした場合、例外が発生します。
	 * 
	 * @return 出力用ワークテーブルのあるDBへの接続
	 * @throws IllegalStateException {@link Cycle#execute()}外から呼び出した場合
	 */
	public static Connection getOutputConnection() {
		return getConnectionFrom(outputConnectionHolder);
	}

	/**
	 * 自システムDBへの接続を取得します。
	 * <p>
	 * このメソッドがpublic staticであることに注意してください。つまり、このメソッドは、どこからでも呼び出すことが可能ですが、接続が取得できるのは、サイクル内、つまり{@link Cycle#execute()}内だけです。そのほかの場所から取得しようとした場合、例外が発生します。
	 * 
	 * @return 自システムDBへの接続
	 * @throws IllegalStateException {@link Cycle#execute()}外から呼び出した場合
	 */
	public static Connection getOurConnection() {
		return getConnectionFrom(ourConnectionHolder);
	}

	/**
	 * バックアップ用ワークテーブルのあるDBへの接続を取得します。
	 * <p>
	 * このメソッドがpublic staticであることに注意してください。つまり、このメソッドは、どこからでも呼び出すことが可能ですが、接続が取得できるのは、サイクル内、つまり{@link Cycle#execute()}内だけです。そのほかの場所から取得しようとした場合、例外が発生します。
	 * 
	 * @return バックアップ用ワークテーブルのあるDBへの接続
	 * @throws java.lang.IllegalStateException {@link Cycle#execute()}外から呼び出した場合
	 */
	public static Connection getBackupConnection() {
		return getConnectionFrom(backupConnectionHolder);
	}

	/**
	 * このCycle用に定義されている設定情報を返却します。
	 * <p>
	 * このメソッドは、{@link #execute()}内で呼び出されるプラグインからのみ呼び出すことが可能です。Circularを複数同時起動させている場合、それぞれの設定を返却します。
	 * 
	 * @return このCycle用の設定
	 * @throws IllegalStateException {@link #execute()}の外で呼び出した場合
	 */
	public static Configure getConfigure() {
		Configure config = configHolder.get();
		if (config == null) throw new IllegalStateException(
			"Cycle.execute()内でのみ、Configureを取得することができます。");
		return config;
	}

	/**
	 * ユーザーからの停止指示が出ているかをチェックします。停止指示が出ている場合は、{@link InterruptedException}をスローします。
	 * 
	 * @throws InterruptedException ユーザーからの停止指示が出ている場合
	 */
	public static void checkInterrupted() throws InterruptedException {
		if (Thread.interrupted()) throw new InterruptedException();
	}

	/**
	 * すべての接続をコミットします。
	 * <p>
	 * 個別に接続をコミットする必要がある場合は、各接続に対して直接コミットしてください。
	 */
	public static void commitAllConnections() {
		Configure config = configHolder.get();
		if (config == null) throw new IllegalStateException(
			"Cycle.execute()内でのみ、commitAllConnections()を実行することができます。");

		String message = "";
		try {
			//入力用接続は、他システムが停止している場合、nullのまま処理を進めるので
			//コミット時に存在チェックを行う必要がある
			//その他の接続は、接続できない場合処理が進まないので、チェックする必要はない
			Connection inputConnection = getInputConnection();
			if (config.useInput() && inputConnection != null) message = commit(
				inputConnection,
				config.getInputDriverInformation());

			if (config.useBackup()) message += commit(
				getBackupConnection(),
				config.getBackupDriverInformation());

			if (config.useOutput()) message += commit(
				getOutputConnection(),
				config.getOutputDriverInformation());

			if (config.useOur()) message += commit(
				getOurConnection(),
				config.getOurDriverInformation());
		} catch (SQLException e) {
			throw new CircularException(message, e);
		}
	}

	Cycle(Configure config) {
		this.config = config;
	}

	/**
	 * 1サイクルを実行します。
	 * <p>
	 * 1サイクルとは、現時点で他システムワークテーブル内にあるデータを全て処理し、他システムへのデータを全て出力するまでのことを言います。
	 * 
	 * @throws InterruptedException ユーザから停止が指示（{@link Thread#interrupt()}がフレームワークにより実行）された場合に発生
	 */
	public void execute() throws InterruptedException {
		try {
			executeInternal();
		} catch (AbortNotice n) {
			config.observer.receiveAbort(n);
		} catch (CircularException e) {
			//一時的な問題が発生した場合
			//処理を中断し、例外の報告は行うが、スローはしない
			//例外がCycleStrategyを通過して、CycleStrategyの状態をリセットしないために、ここでキャッチする必要がある
			ErrorUtils.notifyMessagelessError(config.observer, Cycle.class, e);
		}
	}

	private void executeInternal() throws InterruptedException, AbortNotice {
		config.observer.receiveCycleStarted();

		Connection inputConnection = null;
		Connection outputConnection = null;
		Connection ourConnection = null;
		Connection backupConnection = null;

		boolean needRollback = false;

		try {
			configHolder.set(config);

			try {
				inputConnection = config.inputDriver.getConnection();
				inputConnectionHolder.set(inputConnection);
			} catch (SQLException e) {
				//他システムDBに接続できなかった場合
				config.observer.receiveInputConnectFailure(e);
			}

			outputConnection = config.outputDriver.getConnection();
			outputConnectionHolder.set(outputConnection);
			ourConnection = config.ourDriver.getConnection();
			ourConnectionHolder.set(ourConnection);
			backupConnection = config.backupDriver.getConnection();
			backupConnectionHolder.set(backupConnection);

			for (Phase phase : config.phases) {
				//Phaseを開始する前に、停止されていないかチェック
				checkInterrupted();
				try {
					config.observer.receivePhaseBefore(phase);
					phase.execute();
					commitAllConnections();
				} catch (InterruptedException e) {
					needRollback = true;
					throw e;
				} catch (AbortNotice notice) {
					needRollback = true;
					throw notice;
				} catch (RuntimeException e) {
					needRollback = true;
					throw e;
				} catch (Error e) {
					needRollback = true;
					throw e;
				} finally {
					config.observer.receivePhaseAfter(phase);
				}
			}
		} catch (SQLException e) {
			throw new CircularException(e);
		} finally {
			try {
				boolean exceptionOccurs = false;

				if (ourConnection != null) {
					exceptionOccurs = exceptionOccurs
						& !terminate(
							needRollback,
							ourConnection,
							config.ourDriver);
				}

				if (inputConnection != null) exceptionOccurs = exceptionOccurs
					& !terminate(
						needRollback,
						inputConnection,
						config.inputDriver);

				if (outputConnection != null) {
					exceptionOccurs = exceptionOccurs
						& !terminate(
							needRollback,
							outputConnection,
							config.outputDriver);
				}

				if (backupConnection != null) exceptionOccurs = exceptionOccurs
					& !terminate(
						needRollback,
						backupConnection,
						config.backupDriver);

				if (exceptionOccurs) throw new CircularException(
					"Connectionのcloseに失敗しました。");
			} finally {
				inputConnectionHolder.set(null);
				outputConnectionHolder.set(null);
				ourConnectionHolder.set(null);
				backupConnectionHolder.set(null);
				configHolder.set(null);
				config.observer.receiveCycleEnded();
			}
		}
	}

	private static final String commit(
		Connection connection,
		String driverInformation) throws SQLException {
		connection.commit();
		return driverInformation + "のcommitは既に実行されてしまいました。";
	}

	private static Connection getConnectionFrom(
		ThreadLocal<Connection> threadLocal) {
		Connection connection = threadLocal.get();
		if (connection == null) throw new IllegalStateException(
			"未使用設定の接続を使用しようとしているか、もしくはCycle.execute()外で、Connectionを取得しようとしています。");
		return connection;
	}

	private boolean terminate(
		boolean needRollback,
		Connection connection,
		JDBC jdbc) {
		try {
			try {
				if (needRollback) connection.rollback();
			} finally {
				connection.close();
			}
		} catch (SQLException e) {
			config.observer.receiveError(jdbc + "のcloseに失敗しました。", e);
			return false;
		} catch (Throwable t) {
			config.observer.receiveError(jdbc + "のclose中に問題が発生しました。", t);
		}
		return true;
	}
}
