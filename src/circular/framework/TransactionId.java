package circular.framework;

/**
 * {@link ControlServer}���A�N���C�A���g����̐ڑ����󂯕t���邽�тɃJ�E���g�A�b�v����A��ӂ�ID��\���N���X�ł��B
 * <p>
 * ID�͂��̃N���X�����[�h���ꂽ�Ƃ�����J�E���g�A�b�v����܂��B
 * 
 * @author ��t �N�k
 * @version $Name:  $
 */
public class TransactionId {

	private static final Object idLock = new Object();

	private static long globalId = 0;

	private final long id;

	TransactionId() {
		synchronized (idLock) {
			id = globalId++;
		}
	}

	/**
	 * TransactionId�̕�����\�����`���Ă��܂��B
	 * 
	 * @return TransactionId�̕�����\��
	 */
	@Override
	public String toString() {
		return "tid[" + id + "]";
	}
}
