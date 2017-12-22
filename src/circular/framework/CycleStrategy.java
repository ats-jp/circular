package circular.framework;

import java.util.Date;

/**
 * �T�C�N�����@��\�����ۊ��N���X�ł��B
 * 
 * @author ��t �N�k
 * @version $Name:  $
 */
public abstract class CycleStrategy {

	private int intervalMinutes;

	private Observer observer;

	/**
	 * �T�C�N���̃��[�v���s���܂��B
	 * <p>
	 * ���̃��\�b�h���畜�A����ꍇ�̓��[�U�����~���w�����ꂽ���A�\�����Ȃ���O�����������ꍇ�ł��B
	 * <br>
	 * ���[�v���@���̂��̂̓T�u�N���X�Œ�`���Ă��������B
	 * 
	 * @param cycle 1�T�C�N��
	 * @throws InterruptedException ���[�U�����~���w���i{@link Thread#interrupt()}���t���[�����[�N�ɂ����s�j���ꂽ�ꍇ�ɔ���
	 */
	protected abstract void execute(Cycle cycle) throws InterruptedException;

	/**
	 * �T�u�N���X���A���ݐݒ肳��Ă����~�Ԋu���擾���邽�߂ɗp�ӂ���Ă��郁�\�b�h�ł��B
	 * 
	 * @return ���݂̒�~�Ԋu�i���j
	 */
	protected synchronized int getCurrentIntervalMinutes() {
		return intervalMinutes;
	}

	/**
	 * �T�u�N���X���A���݃��[�U�����~�w�������Ă��邩�ǂ������ׂ邽�߂ɗp�ӂ��ꂽ���\�b�h�ł��B
	 * 
	 * @throws InterruptedException ���[�U�����~���w���i{@link Thread#interrupt()}���t���[�����[�N�ɂ����s�j���ꂽ�ꍇ�ɔ���
	 */
	protected final void checkInterrupted() throws InterruptedException {
		if (Thread.interrupted()) throw new InterruptedException();
	}

	/**
	 * �T�u�N���X���A���̃T�C�N���J�n�\�莞����{@link Observer}�ɒʒm���邽�߂ɗp�ӂ��ꂽ���\�b�h�ł��B
	 * 
	 * @param next ���̃T�C�N���J�n�\�莞��
	 */
	protected final synchronized void notifyNextCycleScheduleToObserver(
		Date next) {
		observer.receiveNextCycleSchedule((Date) next.clone());
	}

	synchronized void setCurrentIntervalMinutes(int minutes) {
		intervalMinutes = minutes;
	}

	synchronized void setObserver(Observer observer) {
		this.observer = observer;
	}
}
