package circular.framework.phase;

/**
 * {@link CircularBean}�Ɋ֌W����N���X�ɋ��ʂ�����̂��`�����C���^�[�t�F�C�X�ł��B
 * 
 * @author ��t �N�k
 * @version $Name:  $
 */
public interface Common {

	/**
	 * �V�[�P���X�ԍ��p���ږ���ԋp���܂��B
	 * 
	 * @return �V�[�P���X�ԍ��p���ږ�
	 */
	String getSequenceColumnName();

	/**
	 * �o�^�����p���ږ���ԋp���܂��B
	 * 
	 * @return �o�^�����p���ږ�
	 */
	String getTimestampColumnName();
}
