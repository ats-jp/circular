package circular.ui;

import java.net.InetAddress;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import circular.framework.AbortNotice;
import circular.framework.ControlProtocol.Method;
import circular.framework.ControlProtocol.Status;
import circular.framework.Cycle;
import circular.framework.Observer;
import circular.framework.Phase;
import circular.framework.TransactionId;

/**
 * org.apache.commons.logging.Log�ɑ΂��Ď��s�󋵂����O�Ƃ��ďo�͂���{@link Observer}�ł��B
 * 
 * @author ��t �N�k
 * @version $Name:  $
 */
public class LogObserver implements Observer {

	private static final Log log = LogFactory.getLog(LogObserver.class);

	private final String configName;

	/**
	 * �ݒ育�Ƃ̃C���X�^���X�𐶐����܂��B
	 *  
	 * @param configName �ݒ薼
	 */
	public LogObserver(String configName) {
		this.configName = configName;
	}

	/**
	 * ���O���x���Finfo
	 * 
	 * @see Observer#receiveCircularStarted()
	 */
	@Override
	public void receiveCircularStarted() {
		log.info(finishMessage("Circular���J�n���܂����B"));
	}

	/**
	 * ���O���x���Finfo
	 * 
	 * @see Observer#receiveCircularStopped()
	 */
	@Override
	public void receiveCircularStopped() {
		log.info(finishMessage("Circular����~���܂����B"));
	}

	/**
	 * ���O���x���Finfo
	 * 
	 * @see Observer#receiveControlServerAccepted(TransactionId, InetAddress)
	 */
	@Override
	public void receiveControlServerAccepted(TransactionId id, InetAddress from) {
		log.info(finishMessage("ControlServer�͐ڑ����󂯕t���܂����Baddress="
			+ from
			+ " "
			+ id));
	}

	/**
	 * ���O���x���Finfo
	 * 
	 * @see Observer#receiveControlServerRequested(TransactionId, Method)
	 */
	@Override
	public void receiveControlServerRequested(TransactionId id, Method request) {
		log.info(finishMessage(id + "�̗v����" + request + "�ł��B"));
	}

	/**
	 * ���O���x���Finfo�i�T�[�o����G���[��Ԃ��ꍇ��error�j
	 * 
	 * @see Observer#receiveControlServerResponse(TransactionId, Status, Throwable)
	 */
	@Override
	public void receiveControlServerResponse(
		TransactionId id,
		Status response,
		Throwable error) {
		String message = id + "�ւ̕ԓ���" + response + "�ł��B";
		if (error != null) log.error(message, error);
		else log.info(finishMessage(message));
	}

	/**
	 * ���O���x���Fwarn
	 * 
	 * @see Observer#receiveControlServerTimeout(TransactionId)
	 */
	@Override
	public void receiveControlServerTimeout(TransactionId id) {
		log.warn(finishMessage(id + "����̐ڑ����^�C���A�E�g���܂����B"));
	}

	/**
	 * ���O���x���Finfo
	 * 
	 * @see Observer#receiveControlServerStartuped()
	 */
	@Override
	public void receiveControlServerStartuped() {
		log.info(finishMessage("ControlServer���N�����܂����B"));
	}

	/**
	 * ���O���x���Finfo
	 * 
	 * @see Observer#receiveControlServerShutdowned()
	 */
	@Override
	public void receiveControlServerShutdowned() {
		log.info(finishMessage("ControlServer���I�����܂����B"));
	}

	/**
	 * ���O���x���Finfo
	 * 
	 * @see Observer#receiveCycleStarted()
	 */
	@Override
	public void receiveCycleStarted() {
		log.info(finishMessage("�T�C�N�����J�n���܂����B"));
	}

	/**
	 * ���O���x���Finfo
	 * 
	 * @see Observer#receiveCycleEnded()
	 */
	@Override
	public void receiveCycleEnded() {
		log.info(finishMessage("�T�C�N�����I�����܂����B"));
	}

	/**
	 * ���O���x���Ftrace
	 * 
	 * @see Observer#receivePhaseBefore(Phase)
	 */
	@Override
	public void receivePhaseBefore(Phase phase) {
		log.trace(finishMessage("�t�F�C�Y[" + phase.getName() + "]���J�n���܂��B"));
	}

	/**
	 * ���O���x���Ftrace
	 * 
	 * @see Observer#receivePhaseAfter(Phase)
	 */
	@Override
	public void receivePhaseAfter(Phase phase) {
		log.trace(finishMessage("�t�F�C�Y[" + phase.getName() + "]���I�����܂����B"));
	}

	/**
	 * ���O���x���Ftrace
	 * 
	 * @see Observer#receiveInterrupted()
	 */
	@Override
	public void receiveInterrupted() {
		log.trace(finishMessage("Circular�͒�~���߂��󂯕t���܂����B"));
	}

	/**
	 * ���O���x���Finfo
	 * 
	 * @see Observer#receiveAbort(AbortNotice)
	 */
	@Override
	public void receiveAbort(AbortNotice notice) {
		log.info(finishMessage("�v���O�C�����珈���̒��f���w������܂����B"), notice);
	}

	/**
	 * ���O���x���Ftrace
	 * 
	 * @see Observer#receiveIntervalMinutesChanged(int)
	 */
	@Override
	public void receiveIntervalMinutesChanged(int newIntervalMinutes) {
		log.trace(finishMessage("���s�Ԋu��" + newIntervalMinutes + "���ɕύX����܂����B"));
	}

	/**
	 * ���O���x���Ftrace
	 * 
	 * @see Observer#receiveNextCycleSchedule(Date)
	 */
	@Override
	public void receiveNextCycleSchedule(Date next) {
		log.trace(finishMessage("���̃T�C�N�����s������"
			+ new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(next)
			+ "�̗\��ł��B"));
	}

	/**
	 * ���O���x���Fwarn
	 * 
	 * @see Observer#receiveInputConnectFailure(SQLException)
	 */
	@Override
	public void receiveInputConnectFailure(SQLException e) {
		log.warn(finishMessage("���V�X�e���ւ̐ڑ��Ɏ��s���܂����B"), e);
	}

	/**
	 * ���O���x���Ferror
	 * 
	 * @see Observer#receiveError(String, Throwable)
	 */
	@Override
	public void receiveError(String message, Throwable t) {
		log.error(finishMessage(message), t);
	}

	/**
	 * ���O���x���Ffatal
	 * 
	 * @see Observer#receiveFatalError(String, Throwable)
	 */
	@Override
	public void receiveFatalError(String message, Throwable t) {
		log.fatal(finishMessage(message), t);
	}

	/**
	 * �P�Ƀ��O���o�͂��܂��B
	 * <p>
	 * ���̃��\�b�h�́A{@link Cycle#execute()}���ŌĂяo���K�v������܂��B
	 *
	 * @param message fatal ���O
	 */
	public static void fatal(String message) {
		log.fatal(finishMessage(Main.getConfigName(), message));
	}

	/**
	 * �P�Ƀ��O���o�͂��܂��B
	 * <p>
	 * ���̃��\�b�h�́A{@link Cycle#execute()}���ŌĂяo���K�v������܂��B
	 *
	 * @param message error ���O
	 */
	public static void error(String message) {
		log.error(finishMessage(Main.getConfigName(), message));
	}

	/**
	 * �P�Ƀ��O���o�͂��܂��B
	 * <p>
	 * ���̃��\�b�h�́A{@link Cycle#execute()}���ŌĂяo���K�v������܂��B
	 *
	 * @param message warn ���O
	 */
	public static void warn(String message) {
		log.warn(finishMessage(Main.getConfigName(), message));
	}

	/**
	 * �P�Ƀ��O���o�͂��܂��B
	 * <p>
	 * ���̃��\�b�h�́A{@link Cycle#execute()}���ŌĂяo���K�v������܂��B
	 *
	 * @param message info ���O
	 */
	public static void info(String message) {
		log.info(finishMessage(Main.getConfigName(), message));
	}

	/**
	 * �P�Ƀ��O���o�͂��܂��B
	 * <p>
	 * ���̃��\�b�h�́A{@link Cycle#execute()}���ŌĂяo���K�v������܂��B
	 *
	 * @param message debug ���O
	 */
	public static void debug(String message) {
		log.debug(finishMessage(Main.getConfigName(), message));
	}

	/**
	 * �P�Ƀ��O���o�͂��܂��B
	 * <p>
	 * ���̃��\�b�h�́A{@link Cycle#execute()}���ŌĂяo���K�v������܂��B
	 *
	 * @param message trace ���O
	 */
	public static void trace(String message) {
		log.trace(finishMessage(Main.getConfigName(), message));
	}

	private static String finishMessage(String configName, String baseMessage) {
		if (configName == null || configName.length() == 0) return baseMessage;
		return "[" + configName + "] " + baseMessage;
	}

	private String finishMessage(String baseMessage) {
		return finishMessage(configName, baseMessage);
	}
}