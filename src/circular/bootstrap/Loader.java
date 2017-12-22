package circular.bootstrap;

import java.io.File;
import java.io.FilenameFilter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.LinkedList;
import java.util.List;

/**
 * Circular Frameworkに付属するデフォルトの操作GUIとCircularControllerを起動するためのクラスです。
 * <p>
 * Circular及びCircularControllerは、このクラスを含む実行可能jarファイルから起動するようになっています。
 * 
 * @author 千葉 哲嗣
 * @version $Name:  $
 */
public class Loader {

	private static final String schema = "jar:file:/";

	private static final String jarName = "bootstrap.jar";

	private static final String libDirName = "lib";

	private static final String classesDirName = "classes";

	private static final String configDirName = "config";

	private static final String mainClassName = "circular.ui.Main";

	private static final String controllerClassName = "circular.ui.CircularController";

	/**
	 * 起動メソッドです。
	 * <p>
	 * パラメータによって起動対象が変化します。
	 * <p>
	 * boot - Circularを起動します。
	 * <br>
	 * start - Cycleを開始します。
	 * <br>
	 * stop - Cycleを終了します。
	 * 
	 * @param args boot start stop のどれかを指定
	 * @throws Exception 起動方法が間違っている場合
	 */
	public static void main(String[] args) throws Exception {
		if (args == null || args.length != 1 || args[0].length() == 0) throw new IllegalArgumentException(
			"起動時のパラメータに誤りがあります。");

		String homeDirPath = getHomeDirectory();
		File configDir = createDirectory(homeDirPath, configDirName);

		if ("boot".equals(args[0])) {
			//Circularの起動
			Loader.start(
				homeDirPath,
				mainClassName,
				new String[] { configDir.getAbsolutePath() });
		} else if ("start".equals(args[0]) || "stop".equals(args[0])) {
			//Cycleの開始及び終了
			Loader.start(homeDirPath, controllerClassName, new String[] {
				configDir.getAbsolutePath(),
				args[0] });
		} else {
			throw new IllegalArgumentException("起動時のパラメータに誤りがあります。");
		}
	}

	static String getHomeDirectory() {
		String relativeLocation = "/"
			+ Loader.class.getName().replace('.', '/')
			+ ".class";

		String selfPath;
		try {
			selfPath = URLDecoder.decode(
				Loader.class.getResource(relativeLocation).toString(),
				"UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(e);
		}

		if (!selfPath.startsWith(schema)) throw new IllegalStateException(
			"起動方法に問題があります。");

		//jarファイルが置かれている場所を特定
		return selfPath.substring(schema.length()).replaceAll(
			jarName + "!" + relativeLocation + "$",
			"");
	}

	static void start(String homeDirPath, String mainClassName, String[] args)
		throws Exception {
		List<URL> urls = new LinkedList<URL>();

		//classesディレクトリをクラスパスに追加
		urls.add(createDirectory(homeDirPath, classesDirName).toURI().toURL());

		//libディレクトリ内のjarファイルをクラスパスに追加
		File libDir = createDirectory(homeDirPath, libDirName);
		for (File file : libDir.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".jar");
			}
		})) {
			urls.add(file.toURI().toURL());
		}

		URLClassLoader classLoader = URLClassLoader.newInstance(urls.toArray(new URL[urls.size()]));
		//コンテキストクラスローダーを使用する場合に備えてセットしておく
		Thread.currentThread().setContextClassLoader(classLoader);

		Class.forName(mainClassName, false, classLoader)
			.getDeclaredMethod("main", new Class[] { String[].class })
			.invoke(null, new Object[] { args });
	}

	static File createDirectory(String path, String name) {
		File dir = new File(path, name);
		if (!dir.exists()) throw new IllegalStateException(name
			+ "ディレクトリが見つかりません。["
			+ path
			+ "]");
		return dir;
	}
}
