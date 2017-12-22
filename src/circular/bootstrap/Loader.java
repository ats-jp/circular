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
 * Circular Framework�ɕt������f�t�H���g�̑���GUI��CircularController���N�����邽�߂̃N���X�ł��B
 * <p>
 * Circular�y��CircularController�́A���̃N���X���܂ގ��s�\jar�t�@�C������N������悤�ɂȂ��Ă��܂��B
 * 
 * @author ��t �N�k
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
	 * �N�����\�b�h�ł��B
	 * <p>
	 * �p�����[�^�ɂ���ċN���Ώۂ��ω����܂��B
	 * <p>
	 * boot - Circular���N�����܂��B
	 * <br>
	 * start - Cycle���J�n���܂��B
	 * <br>
	 * stop - Cycle���I�����܂��B
	 * 
	 * @param args boot start stop �̂ǂꂩ���w��
	 * @throws Exception �N�����@���Ԉ���Ă���ꍇ
	 */
	public static void main(String[] args) throws Exception {
		if (args == null || args.length != 1 || args[0].length() == 0) throw new IllegalArgumentException(
			"�N�����̃p�����[�^�Ɍ�肪����܂��B");

		String homeDirPath = getHomeDirectory();
		File configDir = createDirectory(homeDirPath, configDirName);

		if ("boot".equals(args[0])) {
			//Circular�̋N��
			Loader.start(
				homeDirPath,
				mainClassName,
				new String[] { configDir.getAbsolutePath() });
		} else if ("start".equals(args[0]) || "stop".equals(args[0])) {
			//Cycle�̊J�n�y�яI��
			Loader.start(homeDirPath, controllerClassName, new String[] {
				configDir.getAbsolutePath(),
				args[0] });
		} else {
			throw new IllegalArgumentException("�N�����̃p�����[�^�Ɍ�肪����܂��B");
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
			"�N�����@�ɖ�肪����܂��B");

		//jar�t�@�C�����u����Ă���ꏊ�����
		return selfPath.substring(schema.length()).replaceAll(
			jarName + "!" + relativeLocation + "$",
			"");
	}

	static void start(String homeDirPath, String mainClassName, String[] args)
		throws Exception {
		List<URL> urls = new LinkedList<URL>();

		//classes�f�B���N�g�����N���X�p�X�ɒǉ�
		urls.add(createDirectory(homeDirPath, classesDirName).toURI().toURL());

		//lib�f�B���N�g������jar�t�@�C�����N���X�p�X�ɒǉ�
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
		//�R���e�L�X�g�N���X���[�_�[���g�p����ꍇ�ɔ����ăZ�b�g���Ă���
		Thread.currentThread().setContextClassLoader(classLoader);

		Class.forName(mainClassName, false, classLoader)
			.getDeclaredMethod("main", new Class[] { String[].class })
			.invoke(null, new Object[] { args });
	}

	static File createDirectory(String path, String name) {
		File dir = new File(path, name);
		if (!dir.exists()) throw new IllegalStateException(name
			+ "�f�B���N�g����������܂���B["
			+ path
			+ "]");
		return dir;
	}
}
