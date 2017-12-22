package circular.framework;

/**
 * �񕜕s�\�ȗ�O�����������ꍇ�X���[������O�ł��B
 * <p>
 * ���̗�O���X���[�����ƁACircular Framework�̓T�C�N�����ł������𒆒f���A���̃T�C�N���֑J�ڂ��܂��B
 * <br>
 * �^�p��A�ɗ�Circular Framework���~���������Ȃ��ꍇ�́A���̗�O���X���[����悤�ɂ��Ă��������B
 * 
 * @author ��t �N�k
 * @version $Name:  $
 */
@SuppressWarnings("serial")
public class CircularException extends RuntimeException {

	/**
	 * ���b�Z�[�W�݂̂����C���X�^���X�𐶐����܂��B
	 * 
	 * @param message ��O���b�Z�[�W
	 */
	public CircularException(String message) {
		super(message);
	}

	/**
	 * ���b�Z�[�W�ƌ����ƂȂ�����O�����C���X�^���X�𐶐����܂��B
	 * 
	 * @param message ��O���b�Z�[�W
	 * @param e �����ƂȂ��O
	 */
	public CircularException(String message, Exception e) {
		super(message, e);
	}

	/**
	 * �����ƂȂ��O�����C���X�^���X�𐶐����܂��B
	 * 
	 * @param e �����ƂȂ��O
	 */
	public CircularException(Exception e) {
		super(e);
	}
}
