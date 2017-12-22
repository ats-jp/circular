package circular.framework;

import java.net.InetAddress;
import java.sql.SQLException;
import java.util.Date;

import circular.framework.ControlProtocol.Method;
import circular.framework.ControlProtocol.Status;

/**
 * Circular Framework�̎��s�󋵂��ϑ����邽�߂̃C���^�[�t�F�C�X�ł��B
 * 
 * @author ��t �N�k
 * @version $Name:  $
 */
public interface Observer {

	/**
	 * {@link Circular}�̃|�[�����O�������J�n�������Ƃ��ACircular Framework�ɂ���Ēʒm����܂��B
	 * 
	 * @see Circular#start()
	 */
	void receiveCircularStarted();

	/**
	 * {@link Circular}�̃|�[�����O�������~�������Ƃ��ACircular Framework�ɂ���Ēʒm����܂��B
	 * 
	 * @see Circular#stop()
	 */
	void receiveCircularStopped();

	/**
	 * TCP/IP�ڑ��o�R��{@link ControlServer}�ւ̎w�����������Ƃ��ACircular Framework�ɂ���Ēʒm����܂��B
	 * 
	 * @param id {@link ControlServer}�����[�h����Ă���J�E���g�A�b�v���Ă���ID�ԍ�
	 * @param from �ڑ���N���C�A���g�̃A�h���X
	 */
	void receiveControlServerAccepted(TransactionId id, InetAddress from);

	/**
	 * �p�����[�^��{@link TransactionId}�Őڑ����Ă��Ă���N���C�A���g����̗v�����󂯕t�������Ƃ��ACircular Framework�ɂ���Ēʒm����܂��B
	 * 
	 * @param id {@link Observer#receiveControlServerAccepted(TransactionId, InetAddress)}��transactionId�Ɠ����l�ƂȂ�̂��A����g�����U�N�V�����ƂȂ�
	 * @param request �w��
	 */
	void receiveControlServerRequested(TransactionId id, Method request);

	/**
	 * �p�����[�^��{@link TransactionId}�Őڑ����Ă��Ă���N���C�A���g�ւ̕ԓ����e���ACircular Framework�ɂ���Ēʒm����܂��B
	 * 
	 * @param id {@link Observer#receiveControlServerAccepted(TransactionId, InetAddress)}��transactionId�Ɠ����l�ƂȂ�̂��A����g�����U�N�V�����ƂȂ�
	 * @param response �ԓ�
	 * @param error ���̃g�����U�N�V�����Ŕ���������O���̓G���[�A�A���������Ȃ������ꍇ��null
	 */
	void receiveControlServerResponse(
		TransactionId id,
		Status response,
		Throwable error);

	/**
	 * �p�����[�^��{@link TransactionId}�Őڑ����Ă��Ă���N���C�A���g����̗v��������O��{@link ControlServer}���^�C���A�E�g�������Ƃ��ACircular Framework�ɂ���Ēʒm����܂��B
	 * 
	 * @param id {@link Observer#receiveControlServerAccepted(TransactionId, InetAddress)}��transactionId�Ɠ����l�ƂȂ�̂��A����g�����U�N�V�����ƂȂ�
	 */
	void receiveControlServerTimeout(TransactionId id);

	/**
	 * {@link ControlServer}���N���������Ƃ��ACircular Framework�ɂ���Ēʒm����܂��B
	 */
	void receiveControlServerStartuped();

	/**
	 * {@link ControlServer}���I���������Ƃ��ACircular Framework�ɂ���Ēʒm����܂��B
	 */
	void receiveControlServerShutdowned();

	/**
	 * 1�T�C�N�����J�n����邱�Ƃ��ACircular Framework�ɂ���Ēʒm����܂��B
	 * 
	 * @see circular.framework.Cycle
	 */
	void receiveCycleStarted();

	/**
	 * 1�T�C�N�����I���������Ƃ��ACircular Framework�ɂ���Ēʒm����܂��B
	 * 
	 * @see circular.framework.Cycle
	 */
	void receiveCycleEnded();

	/**
	 * ����{@link Phase}�����ꂩ��J�n���邱�Ƃ��ACircular Framework�ɂ���Ēʒm����܂��B
	 * 
	 * @param phase �J�n����{@link Phase}
	 */
	void receivePhaseBefore(Phase phase);

	/**
	 * ����{@link Phase}���I���������Ƃ��ACircular Framework�ɂ���Ēʒm����܂��B
	 * 
	 * @param phase �I������{@link Phase}
	 */
	void receivePhaseAfter(Phase phase);

	/**
	 * �����̒��f���w�����ꂽ���Ƃ��ACircular Framework�ɂ���Ēʒm����܂��B
	 * 
	 * @see Circular#stop()
	 */
	void receiveInterrupted();

	/**
	 * �T�C�N���̒��f���v���O�C��������w�����ꂽ���Ƃ��ACircular Framework�ɂ���Ēʒm����܂��B
	 * 
	 * @param notice ���̂Ƃ����������ʒm
	 */
	void receiveAbort(AbortNotice notice);

	/**
	 * {@link Cycle}�̎��s�Ԋu���ύX���ꂽ���Ƃ��ACircular Framework�ɂ���Ēʒm����܂��B
	 * 
	 * @param newIntervalMinutes
	 */
	void receiveIntervalMinutesChanged(int newIntervalMinutes);

	/**
	 * {@link Cycle}�̎��̎��s�\�莞�����ACircular Framework�ɂ���Ēʒm����܂��B
	 * 
	 * @param next ���̎��s�\�莞��
	 */
	void receiveNextCycleSchedule(Date next);

	/**
	 * ���V�X�e��DB�ւ̐ڑ��Ɏ��s�������Ƃ��ACircular Framework�ɂ���Ēʒm����܂��B
	 * 
	 * @param e ���̂Ƃ�����������O
	 */
	void receiveInputConnectFailure(SQLException e);

	/**
	 * �T�C�N�����s���ɗ\�����Ȃ���肪���������ꍇ�ɁACircular Framework�ɂ���Ēʒm����܂��B
	 * <p>
	 * ���̒ʒm���s��ꂽ�ꍇ�A���s���������T�C�N���͒��f����܂����A{@link Circular}�̃|�[�����O�͒�~�����A���̃T�C�N���Ƀg���C���܂��B
	 * 
	 * @param message ���̔���������
	 * @param t ���̂Ƃ������������
	 */
	void receiveError(String message, Throwable t);

	/**
	 * �T�C�N�����s���ɗ\�����Ȃ��v���I�Ȗ�肪���������ꍇ�ɁACircular Framework�ɂ���Ēʒm����܂��B
	 * <p>
	 * ���̒ʒm���s��ꂽ�ꍇ�A���s���������T�C�N���͒��f����A{@link Circular}�̃|�[�����O����~���Ă��܂��B
	 * <br>
	 * �w�����Ȃ�����ĊJ����܂���B
	 * 
	 * @param message ���̔���������
	 * @param t ���̂Ƃ������������
	 */
	void receiveFatalError(String message, Throwable t);
}
