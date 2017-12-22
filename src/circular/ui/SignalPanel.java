package circular.ui;

import java.awt.Color;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

import circular.framework.Observer;
import circular.framework.TransactionId;
import circular.framework.observer.IdleObserver;

/**
 * @author ��t �N�k
 * @version $Name:  $
 */
@SuppressWarnings("serial")
class SignalPanel extends JPanel {

	private static final int GREEN = 0;

	private static final int YELLOW = 1;

	private static final int ORANGE = 2;

	private static final int RED = 3;

	private static final Color[] colors = new Color[4];

	private final JTextField field = new JTextField(18);

	private final JButton reset;

	private final Observer observer = new IdleObserver() {

		@Override
		public void receiveControlServerTimeout(TransactionId id) {
			receive(YELLOW, "�T�[�o�^�C���A�E�g����");
		}

		@Override
		public void receiveInputConnectFailure(SQLException e) {
			receive(YELLOW, "���V�X�e���ڑ����s");
		}

		@Override
		public void receiveError(String message, Throwable t) {
			receive(ORANGE, "�G���[����");
		}

		@Override
		public void receiveFatalError(String message, Throwable t) {
			receive(RED, "�d��G���[����");
		}
	};

	private final JTabbedPane tabbedPane;

	private final int tabIndex;

	static {
		colors[GREEN] = Color.GREEN;
		colors[YELLOW] = Color.YELLOW;
		colors[ORANGE] = Color.ORANGE;
		colors[RED] = Color.RED;
	}

	/**
	 * ���݂̌x����
	 * this�œ�����
	 */
	private int status = GREEN;

	SignalPanel(JTabbedPane tabbedPane, int tabIndex) {
		this.tabbedPane = tabbedPane;
		this.tabIndex = tabIndex;

		setBorder(new TitledBorder("�x��"));
		field.setEditable(false);
		field.setHorizontalAlignment(SwingConstants.LEFT);
		add(field);

		reset = new JButton("Reset");
		reset.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				reset();
			}
		});
		reset.setToolTipText("�x����������܂�");

		Insets insets = new Insets(0, 0, 0, 0);
		reset.setMargin(insets);
		add(reset);
	}

	Observer getObserver() {
		return observer;
	}

	void warn(String message) {
		receive(YELLOW, message);
	}

	Color getCurrentColor() {
		synchronized (this) {
			return colors[status];
		}
	}

	private void receive(int status, final String message) {
		synchronized (this) {
			if (this.status >= status) return;
			this.status = status;
		}

		final Color color = colors[status];

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				field.setText(message);
				field.setCaretPosition(0);
				field.setBackground(color);
				tabbedPane.setIconAt(tabIndex, SignalIcon.getIcon(color));
				reset.setEnabled(true);
			}
		});
	}

	//�`��X���b�h�ȊO����Ă΂Ȃ�����
	void reset() {
		synchronized (this) {
			status = GREEN;
		}
		reset.setEnabled(false);
		field.setText("�ُ�Ȃ�");
		field.setCaretPosition(0);
		field.setBackground(Color.GREEN);
		tabbedPane.setIconAt(tabIndex, SignalIcon.getIcon(Color.GREEN));
	}
}
