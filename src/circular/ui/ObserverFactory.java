package circular.ui;

import circular.framework.Observer;

/**
 * �Ǝ���`��{@link Observer}�𐶐����邽�߂̃C���^�[�t�F�C�X�ł��B
 * 
 * @author ��t �N�k
 * @version $Name:  $
 */
public interface ObserverFactory {

	/**
	 * �N���X�̃L�[
	 */
	public static final String CLASS_KEY = "class";

	/**
	 * �Ǝ���`��{@link Observer}�𐶐����܂��B
	 * 
	 * @param configName �ݒ�̖���
	 * @return �ݒ�̖��̂ɑΉ����鐶�����ꂽ{@link Observer}
	 */
	Observer[] createObservers(String configName);
}
