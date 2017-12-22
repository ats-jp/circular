package circular.ui;

import javax.swing.SwingUtilities;

import circular.framework.Phase;
import circular.framework.observer.IdleObserver;

/**
 * @author êÁót ìNék
 * @version $Name:  $
 */
class PhaseInformationObserver extends IdleObserver {

	private final TitledPanel panel;

	private String message;

	private final Runnable runnable = new Runnable() {

		@Override
		public void run() {
			String message = getMessage();
			panel.setText(message);
			panel.setToolTipText(message);
		}
	};

	PhaseInformationObserver(TitledPanel panel) {
		this.panel = panel;
	}

	@Override
	public void receiveCycleStarted() {
		setMessage(null);
		SwingUtilities.invokeLater(runnable);
	}

	@Override
	public void receiveCycleEnded() {
		setMessage(null);
		SwingUtilities.invokeLater(runnable);
	}

	@Override
	public void receivePhaseBefore(Phase phase) {
		setMessage(phase.getName());
		SwingUtilities.invokeLater(runnable);
	}

	@Override
	public void receivePhaseAfter(Phase phase) {
		setMessage(null);
		SwingUtilities.invokeLater(runnable);
	}

	synchronized String getMessage() {
		return message;
	}

	synchronized void setMessage(String message) {
		this.message = message;
	}
}