package circular.ui;

import circular.framework.Observer;

/**
 * 独自定義の{@link Observer}を生成するためのインターフェイスです。
 * 
 * @author 千葉 哲嗣
 * @version $Name:  $
 */
public interface ObserverFactory {

	/**
	 * クラスのキー
	 */
	public static final String CLASS_KEY = "class";

	/**
	 * 独自定義の{@link Observer}を生成します。
	 * 
	 * @param configName 設定の名称
	 * @return 設定の名称に対応する生成された{@link Observer}
	 */
	Observer[] createObservers(String configName);
}
