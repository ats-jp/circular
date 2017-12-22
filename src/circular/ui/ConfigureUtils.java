package circular.ui;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import circular.framework.Configure;

class ConfigureUtils {

	private static final Pattern configFilenamePattern = Pattern.compile("^CircularFrameworkConfigure[-_]?([^.]*).properties$");

	static LinkedHashMap<String, URL> getConfigureFiles(File configDir) {
		LinkedHashMap<String, URL> map = new LinkedHashMap<String, URL>();
		File[] files = configDir.listFiles();
		for (File file : files) {
			Matcher matcher = configFilenamePattern.matcher(file.getName());
			if (!matcher.matches()) continue;
			String name = matcher.group(1);
			map.put(name, parse(file, Configure.class));
		}
		return map;
	}

	static File createConfigDirectory(String path) {
		File dir = new File(path);
		if (!dir.exists()) throw new IllegalStateException(
			"configディレクトリが見つかりません。[" + path + "]");
		return dir;
	}

	static URL parse(File file, Class target) {
		try {
			return file.toURI().toURL();
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("指定された"
				+ target.getName()
				+ "用設定ファイルのパス["
				+ file.getAbsolutePath()
				+ "]が不正です。");
		}
	}

}
