package circular.framework;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

/**
 * Circular Framework�Ŏg�p����v���p�e�B�t�@�C���̓ǂݍ��݂��T�|�[�g���郆�[�e�B���e�B�N���X�ł��B
 * <p>
 * ���{�ꓙ���L�����ꂽ�v���p�e�B�t�@�C���ł��ǂݍ��ނ��Ƃ��\�ł��B
 * 
 * @author ��t �N�k
 * @version $Name:  $
 */
public class CircularProperties {

	private static final String charsetPropertyName = "charset";

	private final Properties properties;

	private final String urlForMessage;

	/**
	 * �B��̃R���X�g���N�^�ł��B
	 * 
	 * @param propertiesUrl �v���p�e�B��Ǎ���URL
	 * @throws IOException �Ǎ��݂Ɏ��s�����ꍇ
	 */
	public CircularProperties(URL propertiesUrl) throws IOException {
		properties = createProperties(propertiesUrl);
		urlForMessage = propertiesUrl.toString();
	}

	/**
	 * �v���p�e�B�t�@�C�����̒�`���ꂽ���𕶎���Ƃ��Ď��o���܂��B
	 * @param key �~�����v���p�e�B�̃L�[
	 * @return �L�[�ɑΉ������v���p�e�B
	 */
	public String getProperty(String key) {
		if (!containsKey(key)) throw new IllegalArgumentException("key["
			+ key
			+ "]��"
			+ urlForMessage
			+ "���Ɍ�����܂���B");
		return properties.getProperty(key);
	}

	/**
	 * ���̃v���p�e�B�Ƀp�����[�^�Ŏ����ꂽ�L�[�����݂��邩�ǂ������������܂��B
	 * 
	 * @param key ���݂𒲂ׂ�L�[
	 * @return ���݂���ꍇ�Atrue
	 */
	public boolean containsKey(String key) {
		return properties.containsKey(key);
	}

	private static Properties createProperties(URL propertiesUrl)
		throws IOException {
		Properties properties = new Properties();
		InputStream input = new Native2AsciiInputStream(
			propertiesUrl.openStream());
		//��U�l�C�e�B�u�R�[�h�œǂݍ���
		try {
			properties.load(input);
		} finally {
			input.close();
		}

		String charset = properties.getProperty(charsetPropertyName);

		//���ɕ����R�[�h�̎w�肪���Ă���΂��̕����R�[�h�ōă��[�h
		if (charset != null && !charset.equals("")) {
			InputStream reinput = new Native2AsciiInputStream(
				propertiesUrl.openStream(),
				charset);
			try {
				properties.load(reinput);
			} finally {
				reinput.close();
			}
		}

		return properties;
	}
}
