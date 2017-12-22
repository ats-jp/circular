package circular.framework.phase;

import circular.framework.AbortNotice;

/**
 * {@link InputPhase}���\�����[�N�e�[�u���̒��́A�ꌏ�̃f�[�^�ɑΉ�����������\���܂��B
 * 
 * @author ��t �N�k
 * @version $Name:  $
 */
public interface InputPhaseElement {

	/**
	 * ��`���ꂽ���������s���܂��B
	 * 
	 * @param bean �ꌏ�̃f�[�^�\��Bean
	 * @throws InterruptedException ���̏����̓�����{@link Thread#sleep(long)}�����s���Ă���ꍇ�ŁA�����[�U�����~���w���i{@link Thread#interrupt()}���t���[�����[�N�ɂ����s�j���ꂽ�ꍇ�ɔ���
	 * @throws AbortNotice InputPhaseElement����{@link Cycle}�𒆒f�������ꍇ
	 */
	void execute(CircularBean bean) throws InterruptedException, AbortNotice;
}
