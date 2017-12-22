package circular.framework.cyclestrategy;

import java.util.Calendar;

import circular.framework.Cycle;
import circular.framework.CycleStrategy;

/**
 * ���Ԋu��~�T�C�N������������{@link CycleStrategy}�ł��B
 * <p>
 * 1�T�C�N�����s��A�K���w�肳�ꂽ�����Ԓ�~���܂��B
 * <p>
 * �����b�g:�K���w�萔���Ԓ�~���邽�߁A���ׂ����������鎖�͂���܂���B
 * <br>
 * �f�����b�g:1�T�C�N���Ɏ��Ԃ����������ꍇ�ɒx�����������A�����̒x���f�[�^����������\��������܂��B
 * 
 * @author ��t �N�k
 * @version $Name:  $
 */
public class RestStrategy extends CycleStrategy {

	/**
	 * 1�T�C�N�����s��A�K���w�肳�ꂽ�����Ԓ�~���܂��B
	 * 
	 * @param cycle 1�T�C�N��
	 * @throws InterruptedException ���[�U�����~���w���i{@link Thread#interrupt()}���t���[�����[�N�ɂ����s�j���ꂽ�ꍇ�ɔ���
	 */
	@Override
	public void execute(Cycle cycle) throws InterruptedException {
		while (true) {
			checkInterrupted();
			cycle.execute();
			int next = getCurrentIntervalMinutes() * 60 * 1000;
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.MILLISECOND, next);
			notifyNextCycleScheduleToObserver(calendar.getTime());
			Thread.sleep(next);
		}
	}
}
