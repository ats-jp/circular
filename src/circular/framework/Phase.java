package circular.framework;

/**
 * �|�[�����O�����ɂ�����A�ėp������\���܂��B
 * <p>
 * �ėp�����͏����{�̂�{@link Phase#execute()}�ɒ�`����Ƃ����ȊO�́A���ɐ���͂���܂���B
 *
 * @author ��t �N�k
 * @version $Name:  $
 */
public interface Phase {

	/**
	 * ���s�󋵊ώ@�p�ɂ���Phase�̖��̂�Ԃ��܂��B
	 * 
	 * @return ����Phase�̖���
	 */
	String getName();

	/**
	 * �ėp���������s���܂��B
	 * 
	 * @throws InterruptedException ���̏����̓�����{@link Thread#sleep(long)}�����s���Ă���ꍇ�ŁA�����[�U�����~���w���i{@link Thread#interrupt()}���t���[�����[�N�ɂ����s�j���ꂽ�ꍇ�ɔ���
	 * @throws AbortNotice ����{@link Cycle}�𒆒f�������ꍇ
	 */
	void execute() throws InterruptedException, AbortNotice;
}
