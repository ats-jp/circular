package circular.ui;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * @author êÁót ìNék
 * @version $Name:  $
 */
class SignalIcon {

	static final Icon GREEN;

	static final Icon YELLOW;

	static final Icon ORANGE;

	static final Icon RED;

	static final Icon WHITE;

	private static final Map<Color, Icon> map = new HashMap<Color, Icon>();

	static {
		GREEN = loadIcon("green");
		map.put(Color.GREEN, GREEN);

		YELLOW = loadIcon("yellow");
		map.put(Color.YELLOW, YELLOW);

		ORANGE = loadIcon("orange");
		map.put(Color.ORANGE, ORANGE);

		RED = loadIcon("red");
		map.put(Color.RED, RED);

		WHITE = loadIcon("white");
	}

	static Icon getIcon(Color color) {
		return map.get(color);
	}

	private static Icon loadIcon(String color) {
		return new ImageIcon(SignalIcon.class.getResource("/circular/ui/image/"
			+ color
			+ ".png"));
	}
}
