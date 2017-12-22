package circular.ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import circular.framework.Observer;

/**
 * config�f�B���N�g�����́Aobservers�Ƃ����t�@�C�������ɁA{@link Observer}�𐶐�����N���X�ł��B
 * <br>
 * observers�t�@�C���́A{@link Observer}�����������N���X��FQCN�𕡐��A�s���ƂɋL�q���邱�Ƃ��\�ł��B
 * 
 * @author ��t �N�k
 * @version $Name:  $
 */
public class SimpleObserverFactory implements ObserverFactory {

	private static final Observer[] emptyArray = {};

	@Override
	public Observer[] createObservers(String configName) {
		File file = new File(Main.getConfigDirectory(), "observers");
		if (!file.exists()) return emptyArray;

		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			try {
				List<Observer> observers = new LinkedList<Observer>();
				for (String className; (className = reader.readLine()) != null;) {
					try {
						Observer observer = (Observer) Class.forName(className)
							.newInstance();
						observers.add(observer);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
				return observers.toArray(new Observer[observers.size()]);
			} finally {
				reader.close();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);

		}
	}
}
