package circular.ui;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import circular.framework.Configure;

/**
 * @author ��t �N�k
 * @version $Name:  $
 */
@SuppressWarnings("serial")
class IntervalMinutesControlPanel extends JPanel {

	private final JTextField field = new JTextField(4);

	private final JButton plus;

	private final JButton minus;

	IntervalMinutesControlPanel(final Configure config) {
		setBorder(new TitledBorder("�T�C�N���Ԋu"));
		field.setEditable(false);
		field.setHorizontalAlignment(SwingConstants.RIGHT);
		add(field);

		plus = new JButton("�{");
		plus.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int incremented = config.getIntervalMinutes() + 1;
				config.setIntervalMinutes(incremented);
				setCurrentValue(incremented);
				minus.setEnabled(true);
			}
		});
		plus.setToolTipText("1�����₵�܂�");

		minus = new JButton("�|");
		minus.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int decremented = config.getIntervalMinutes() - 1;
				if (decremented < 1) minus.setEnabled(false);
				config.setIntervalMinutes(decremented);
				setCurrentValue(decremented);
			}
		});
		minus.setToolTipText("1�����炵�܂�");

		Insets insets = new Insets(0, 0, 0, 0);
		plus.setMargin(insets);
		minus.setMargin(insets);

		add(plus);
		add(minus);

		int intervalMinutes = config.getIntervalMinutes();

		if (intervalMinutes <= 0) {
			intervalMinutes = 0;
			minus.setEnabled(false);
		}

		setCurrentValue(intervalMinutes);
	}

	private void setCurrentValue(int value) {
		field.setText(String.valueOf(value) + "��");
	}
}