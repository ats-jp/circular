package circular.ui;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

/**
 * @author êÁót ìNék
 * @version $Name:  $
 */
@SuppressWarnings("serial")
class TitledPanel extends JPanel {

	private final JTextField field;

	TitledPanel(String title, int column, int alignment) {
		setBorder(new TitledBorder(title));
		field = new JTextField(column);
		field.setEditable(false);
		field.setHorizontalAlignment(alignment);
		add(field);
	}

	void setText(String value) {
		field.setText(value);
	}
}