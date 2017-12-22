package circular.ui;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import circular.framework.Observer;
import circular.framework.observer.IdleObserver;

/**
 * @author êÁót ìNék
 * @version $Name:  $
 */
@SuppressWarnings("serial")
class NextTimePanel extends TitledPanel {

	private final Runnable startedRunnable = new Runnable() {

		@Override
		public void run() {
			setText("");
		}
	};

	private final Runnable nextRunnable = new Runnable() {

		@Override
		public void run() {
			DateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			setText(format.format(getNext()));
		}
	};

	private final Observer observer = new IdleObserver() {

		@Override
		public void receiveCycleStarted() {
			SwingUtilities.invokeLater(startedRunnable);
		}

		@Override
		public void receiveNextCycleSchedule(Date next) {
			setNext(next);
			SwingUtilities.invokeLater(nextRunnable);
		}
	};

	private Date next;

	NextTimePanel() {
		super("éüâÒãNìÆó\íËéûçè", 12, SwingConstants.RIGHT);
	}

	Observer getObserver() {
		return observer;
	}

	private synchronized Date getNext() {
		return next;
	}

	private synchronized void setNext(Date next) {
		this.next = next;
	}
}