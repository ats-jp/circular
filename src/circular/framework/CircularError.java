package circular.framework;

/**
 * �f�[�^�̈�ѐ����ۂĂȂ��Ȃ������A�d��ȏ�Q�����������ꍇ�ɃX���[�����G���[�ł��B
 * <p>
 * ���̃G���[���X���[������Circular Framework�͂��ׂĂ̏����𒆒f���A�G���[�񍐌��~���܂��B
 * 
 * @author ��t �N�k
 * @version $Name:  $
 */
@SuppressWarnings("serial")
public class CircularError extends Error {

	/**
	 * ���b�Z�[�W�݂̂����C���X�^���X�𐶐����܂��B
	 * 
	 * @param message ��O���b�Z�[�W
	 */
	public CircularError(String message) {
		super(message);
	}

	/**
	 * ���b�Z�[�W�ƌ����ƂȂ�����O�����C���X�^���X�𐶐����܂��B
	 * 
	 * @param message ��O���b�Z�[�W
	 * @param t �����ƂȂ���
	 */
	public CircularError(String message, Throwable t) {
		super(message, t);
	}

	/**
	 * �����ƂȂ��O�����C���X�^���X�𐶐����܂��B
	 * 
	 * @param t �����ƂȂ���
	 */
	public CircularError(Throwable t) {
		super(t);
	}
}
