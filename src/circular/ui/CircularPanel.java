package circular.ui;

import java.awt.Color;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.TitledBorder;

import circular.framework.Circular;
import circular.framework.Observer;
import circular.framework.observer.IdleObserver;

/**
 * @author êÁót ìNék
 * @version $Name:  $
 */
@SuppressWarnings("serial")
class CircularPanel extends JPanel {

	private final JTextField field = new JTextField(5);

	private final TitledPanel nextTime;

	private final JButton start;

	private final JButton stop;

	private final Runnable startRunnable = new Runnable() {

		@Override
		public void run() {
			start.setEnabled(false);
			stop.setEnabled(true);
		}
	};

	private final Runnable stopRunnable = new Runnable() {

		@Override
		public void run() {
			stop.setEnabled(false);
			nextTime.setText("");
			start.setEnabled(true);
		}
	};

	private final Observer observer = new IdleObserver() {

		@Override
		public void receiveCircularStarted() {
			if (SwingUtilities.isEventDispatchThread()) return;
			SwingUtilities.invokeLater(startRunnable);
		}

		@Override
		public void receiveCircularStopped() {
			if (SwingUtilities.isEventDispatchThread()) return;
			SwingUtilities.invokeLater(stopRunnable);
		}
	};

	private Circular circular;

	private final JTabbedPane tabbedPane;

	private final int tabIndex;

	private final SignalPanel signalPanel;

	CircularPanel(
		TitledPanel nextTime,
		JTabbedPane tabbedPane,
		int tabIndex,
		SignalPanel signalPanel) {
		this.nextTime = nextTime;
		this.tabbedPane = tabbedPane;
		this.tabIndex = tabIndex;
		this.signalPanel = signalPanel;

		setBorder(new TitledBorder("Circular"));
		field.setEditable(false);
		field.setHorizontalAlignment(SwingConstants.LEFT);
		add(field);

		start = new JButton("Start");
		start.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				start.setEnabled(false);
				circular.start();
				stop.setEnabled(true);
			}
		});
		start.setToolTipText("CircularÇäJénÇµÇ‹Ç∑");

		stop = new JButton("Stop");
		stop.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				stop.setEnabled(false);
				circular.stop();
				CircularPanel.this.nextTime.setText("");
				start.setEnabled(true);
			}
		});
		stop.setToolTipText("CircularÇí‚é~ÇµÇ‹Ç∑");
		stop.setEnabled(false);

		Insets insets = new Insets(0, 0, 0, 0);
		start.setMargin(insets);
		stop.setMargin(insets);
		add(start);
		add(stop);
	}

	void setCircular(Circular circular) {
		this.circular = circular;
	}

	Observer getObserver() {
		return observer;
	}

	void start() {
		new Timer(1000, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (circular == null || !circular.isRunning()) {
					setStopped();
					return;
				}

				field.setText("é¿çsíÜ...");

				boolean whiteNow = field.getBackground() == Color.WHITE;

				field.setBackground(whiteNow ? null : Color.WHITE);

				tabbedPane.setIconAt(
					tabIndex,
					whiteNow
						? SignalIcon.getIcon(signalPanel.getCurrentColor())
						: SignalIcon.WHITE);
			}
		}).start();
	}

	//ï`âÊÉXÉåÉbÉhà»äOÇ©ÇÁåƒÇŒÇ»Ç¢Ç±Ç∆
	void setStopped() {
		field.setText("í‚é~");
		field.setBackground(Color.GRAY);
		tabbedPane.setIconAt(
			tabIndex,
			SignalIcon.getIcon(signalPanel.getCurrentColor()));
	}
}