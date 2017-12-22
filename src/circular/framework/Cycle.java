package circular.framework;

import java.sql.Connection;
import java.sql.SQLException;

import circular.framework.Configure.JDBC;

/**
 * 1�T�C�N���̏�����\���N���X�ł��B
 * 
 * @author ��t �N�k
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
	 * ���V�X�e���ւ̓��͗p���[�N�e�[�u���̂���DB�ւ̐ڑ����擾���܂��B
	 * <p>
	 * ���͗p��DB�́A���V�X�e����ɂ�����̂Ƒz�肵�Ă��邽�߁A���V�X�e������~���Ă���ꍇ�A�T�C�N���O����Ăяo�����ꍇ��null���ԋp����܂��B
	 * <p>
	 * null���ԋp���ꂽ�ꍇ�́A�v���O�C���ł̏����̓X�L�b�v����悤�ɂ��Ă��������B
	 * 
	 * @return ���͗p���[�N�e�[�u���̂���DB�ւ̐ڑ�
	 */
	public static Connection getInputConnection() {
		return inputConnectionHolder.get();
	}

	/**
	 * ���V�X�e���ւ̏o�͗p���[�N�e�[�u���̂���DB�ւ̐ڑ����擾���܂��B
	 * <p>
	 * ���̃��\�b�h��public static�ł��邱�Ƃɒ��ӂ��Ă��������B�܂�A���̃��\�b�h�́A�ǂ�����ł��Ăяo�����Ƃ��\�ł����A�ڑ����擾�ł���̂́A�T�C�N�����A�܂�{@link Cycle#execute()}�������ł��B���̂ق��̏ꏊ����擾���悤�Ƃ����ꍇ�A��O���������܂��B
	 * 
	 * @return �o�͗p���[�N�e�[�u���̂���DB�ւ̐ڑ�
	 * @throws IllegalStateException {@link Cycle#execute()}�O����Ăяo�����ꍇ
	 */
	public static Connection getOutputConnection() {
		return getConnectionFrom(outputConnectionHolder);
	}

	/**
	 * ���V�X�e��DB�ւ̐ڑ����擾���܂��B
	 * <p>
	 * ���̃��\�b�h��public static�ł��邱�Ƃɒ��ӂ��Ă��������B�܂�A���̃��\�b�h�́A�ǂ�����ł��Ăяo�����Ƃ��\�ł����A�ڑ����擾�ł���̂́A�T�C�N�����A�܂�{@link Cycle#execute()}�������ł��B���̂ق��̏ꏊ����擾���悤�Ƃ����ꍇ�A��O���������܂��B
	 * 
	 * @return ���V�X�e��DB�ւ̐ڑ�
	 * @throws IllegalStateException {@link Cycle#execute()}�O����Ăяo�����ꍇ
	 */
	public static Connection getOurConnection() {
		return getConnectionFrom(ourConnectionHolder);
	}

	/**
	 * �o�b�N�A�b�v�p���[�N�e�[�u���̂���DB�ւ̐ڑ����擾���܂��B
	 * <p>
	 * ���̃��\�b�h��public static�ł��邱�Ƃɒ��ӂ��Ă��������B�܂�A���̃��\�b�h�́A�ǂ�����ł��Ăяo�����Ƃ��\�ł����A�ڑ����擾�ł���̂́A�T�C�N�����A�܂�{@link Cycle#execute()}�������ł��B���̂ق��̏ꏊ����擾���悤�Ƃ����ꍇ�A��O���������܂��B
	 * 
	 * @return �o�b�N�A�b�v�p���[�N�e�[�u���̂���DB�ւ̐ڑ�
	 * @throws java.lang.IllegalStateException {@link Cycle#execute()}�O����Ăяo�����ꍇ
	 */
	public static Connection getBackupConnection() {
		return getConnectionFrom(backupConnectionHolder);
	}

	/**
	 * ����Cycle�p�ɒ�`����Ă���ݒ����ԋp���܂��B
	 * <p>
	 * ���̃��\�b�h�́A{@link #execute()}���ŌĂяo�����v���O�C������̂݌Ăяo�����Ƃ��\�ł��BCircular�𕡐������N�������Ă���ꍇ�A���ꂼ��̐ݒ��ԋp���܂��B
	 * 
	 * @return ����Cycle�p�̐ݒ�
	 * @throws IllegalStateException {@link #execute()}�̊O�ŌĂяo�����ꍇ
	 */
	public static Configure getConfigure() {
		Configure config = configHolder.get();
		if (config == null) throw new IllegalStateException(
			"Cycle.execute()���ł̂݁AConfigure���擾���邱�Ƃ��ł��܂��B");
		return config;
	}

	/**
	 * ���[�U�[����̒�~�w�����o�Ă��邩���`�F�b�N���܂��B��~�w�����o�Ă���ꍇ�́A{@link InterruptedException}���X���[���܂��B
	 * 
	 * @throws InterruptedException ���[�U�[����̒�~�w�����o�Ă���ꍇ
	 */
	public static void checkInterrupted() throws InterruptedException {
		if (Thread.interrupted()) throw new InterruptedException();
	}

	/**
	 * ���ׂĂ̐ڑ����R�~�b�g���܂��B
	 * <p>
	 * �ʂɐڑ����R�~�b�g����K�v������ꍇ�́A�e�ڑ��ɑ΂��Ē��ڃR�~�b�g���Ă��������B
	 */
	public static void commitAllConnections() {
		Configure config = configHolder.get();
		if (config == null) throw new IllegalStateException(
			"Cycle.execute()���ł̂݁AcommitAllConnections()�����s���邱�Ƃ��ł��܂��B");

		String message = "";
		try {
			//���͗p�ڑ��́A���V�X�e������~���Ă���ꍇ�Anull�̂܂܏�����i�߂�̂�
			//�R�~�b�g���ɑ��݃`�F�b�N���s���K�v������
			//���̑��̐ڑ��́A�ڑ��ł��Ȃ��ꍇ�������i�܂Ȃ��̂ŁA�`�F�b�N����K�v�͂Ȃ�
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
	 * 1�T�C�N�������s���܂��B
	 * <p>
	 * 1�T�C�N���Ƃ́A�����_�ő��V�X�e�����[�N�e�[�u�����ɂ���f�[�^��S�ď������A���V�X�e���ւ̃f�[�^��S�ďo�͂���܂ł̂��Ƃ������܂��B
	 * 
	 * @throws InterruptedException ���[�U�����~���w���i{@link Thread#interrupt()}���t���[�����[�N�ɂ����s�j���ꂽ�ꍇ�ɔ���
	 */
	public void execute() throws InterruptedException {
		try {
			executeInternal();
		} catch (AbortNotice n) {
			config.observer.receiveAbort(n);
		} catch (CircularException e) {
			//�ꎞ�I�Ȗ�肪���������ꍇ
			//�����𒆒f���A��O�̕񍐂͍s�����A�X���[�͂��Ȃ�
			//��O��CycleStrategy��ʉ߂��āACycleStrategy�̏�Ԃ����Z�b�g���Ȃ����߂ɁA�����ŃL���b�`����K�v������
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
				//���V�X�e��DB�ɐڑ��ł��Ȃ������ꍇ
				config.observer.receiveInputConnectFailure(e);
			}

			outputConnection = config.outputDriver.getConnection();
			outputConnectionHolder.set(outputConnection);
			ourConnection = config.ourDriver.getConnection();
			ourConnectionHolder.set(ourConnection);
			backupConnection = config.backupDriver.getConnection();
			backupConnectionHolder.set(backupConnection);

			for (Phase phase : config.phases) {
				//Phase���J�n����O�ɁA��~����Ă��Ȃ����`�F�b�N
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
					"Connection��close�Ɏ��s���܂����B");
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
		return driverInformation + "��commit�͊��Ɏ��s����Ă��܂��܂����B";
	}

	private static Connection getConnectionFrom(
		ThreadLocal<Connection> threadLocal) {
		Connection connection = threadLocal.get();
		if (connection == null) throw new IllegalStateException(
			"���g�p�ݒ�̐ڑ����g�p���悤�Ƃ��Ă��邩�A��������Cycle.execute()�O�ŁAConnection���擾���悤�Ƃ��Ă��܂��B");
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
			config.observer.receiveError(jdbc + "��close�Ɏ��s���܂����B", e);
			return false;
		} catch (Throwable t) {
			config.observer.receiveError(jdbc + "��close���ɖ�肪�������܂����B", t);
		}
		return true;
	}
}
