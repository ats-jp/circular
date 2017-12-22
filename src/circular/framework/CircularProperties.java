package circular.framework;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

/**
 * Circular Frameworkで使用するプロパティファイルの読み込みをサポートするユーティリティクラスです。
 * <p>
 * 日本語等が記入されたプロパティファイルでも読み込むことが可能です。
 * 
 * @author 千葉 哲嗣
 * @version $Name:  $
 */
public class CircularProperties {

	private static final String charsetPropertyName = "charset";

	private final Properties properties;

	private final String urlForMessage;

	/**
	 * 唯一のコンストラクタです。
	 * 
	 * @param propertiesUrl プロパティを読込むURL
	 * @throws IOException 読込みに失敗した場合
	 */
	public CircularProperties(URL propertiesUrl) throws IOException {
		properties = createProperties(propertiesUrl);
		urlForMessage = propertiesUrl.toString();
	}

	/**
	 * プロパティファイル内の定義された情報を文字列として取り出します。
	 * @param key 欲しいプロパティのキー
	 * @return キーに対応したプロパティ
	 */
	public String getProperty(String key) {
		if (!containsKey(key)) throw new IllegalArgumentException("key["
			+ key
			+ "]が"
			+ urlForMessage
			+ "内に見つかりません。");
		return properties.getProperty(key);
	}

	/**
	 * このプロパティにパラメータで示されたキーが存在するかどうかを検査します。
	 * 
	 * @param key 存在を調べるキー
	 * @return 存在する場合、true
	 */
	public boolean containsKey(String key) {
		return properties.containsKey(key);
	}

	private static Properties createProperties(URL propertiesUrl)
		throws IOException {
		Properties properties = new Properties();
		InputStream input = new Native2AsciiInputStream(
			propertiesUrl.openStream());
		//一旦ネイティブコードで読み込み
		try {
			properties.load(input);
		} finally {
			input.close();
		}

		String charset = properties.getProperty(charsetPropertyName);

		//中に文字コードの指定がしてあればその文字コードで再ロード
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
