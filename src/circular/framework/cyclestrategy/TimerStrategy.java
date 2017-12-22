package circular.framework.cyclestrategy;

import java.util.Calendar;

import circular.framework.Cycle;
import circular.framework.CycleStrategy;

/**
 * ���Ԋu��~�T�C�N������������{@link CycleStrategy}�ł��B
 * <p>
 * 1�T�C�N�����s��A���̊J�n�����܂Œ�~���܂��B�J�n�����̊Ԋu���w�肷�邱�Ƃ��ł��܂��B
 * <p>
 * �����b�g:�x�����������Ă���ꍇ�ꎞ��~���Ȃ����ߏ����̒x�����������\��������܂��B
 * <br>
 * �f�����b�g:1�T�C�N���Ɏw��Ԋu�ȏ�̎��Ԃ����������ꍇ�A���ׂ�����������\��������܂��B
 * 
 * @author ��t �N�k
 * @version $Name:  $
 */
public class TimerStrategy extends CycleStrategy {

	private static final int sleepMillis = 1 * 1000;

	/**
	 * 1�T�C�N�����s��A���̊J�n�����܂Œ�~���܂��B�J�n�����̊Ԋu���w�肷�邱�Ƃ��ł��܂��B
	 * 
	 * @param cycle 1�T�C�N��
	 * @throws InterruptedException ���[�U�����~���w���i{@link Thread#interrupt()}���t���[�����[�N�ɂ����s�j���ꂽ�ꍇ�ɔ���
	 */
	@Override
	public void execute(Cycle cycle) throws InterruptedException {
		Calendar next = Calendar.getInstance();
		while (true) {
			checkInterrupted();
			cycle.execute();
			next.add(Calendar.MINUTE, getCurrentIntervalMinutes());
			notifyNextCycleScheduleToObserver(next.getTime());
			while (next.after(Calendar.getInstance())) {
				Thread.sleep(sleepMillis);
			}
		}
	}
}
