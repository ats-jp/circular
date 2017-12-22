package circular.framework;

import java.util.HashMap;
import java.util.Map;

/**
 * �^�[�Q�b�g�ƂȂ�C�ӂ̏�����ON/OFF�𑀍�ł���X�C�b�`�N���X�ł��B
 * 
 * @author ��t �N�k
 * @version $Name:  $
 */
class Switch {

	private static final Map<String, Integer> threadCounterMap = new HashMap<String, Integer>();

	private final Target target;

	/**
	 * ������
	 */
	private boolean on = false;

	/**
	 * ��~�����҂�
	 */
	private boolean offing = false;

	private Thread currentTargetThread;

	/**
	 * �^�[�Q�b�g�������Z�b�g���邱�ƂŃC���X�^���X���쐬����܂��B
	 * 
	 * @param target ON/OFF����������
	 */
	Switch(Target target) {
		target.mySwitch = this;
		this.target = target;
	}

	/**
	 * �������J�n���܂��B
	 * <p>
	 * ���̃��\�b�h���琳��ɕ��A�����ꍇ�A�K���������J�n����Ă��܂��B
	 */
	synchronized void on() {
		if (isRunning()) return;
		currentTargetThread = new Thread(
			target,
			getNextThreadName(target.getName()));
		currentTargetThread.start();
		on = true;
		target.receiveTargetStarted();
	}

	/**
	 * �������~���܂��B
	 * <p>
	 * ���̃��\�b�h���琳��ɕ��A�����ꍇ�A�K�������̒�~���������Ă��܂��B
	 */
	synchronized void off() {
		if (!on) return;
		if (offing) {
			waitWhileStoppingAndJoinTarget();
			return;
		}
		offing = true;
		currentTargetThread.interrupt();
		waitWhileStoppingAndJoinTarget();
		target.receiveTargetDead();
	}

	/**
	 * @return �N������Ă��āA��~�����҂��ł͂Ȃ��ꍇ�Atrue
	 */
	synchronized boolean isRunning() {
		if (offing) waitWhileStoppingAndJoinTarget();
		return on;
	}

	synchronized boolean isOffing() {
		return offing;
	}

	/**
	 * Target���I�����钼�O��Target��run()�����s���Ă���X���b�h����Ăяo����܂��B
	 */
	private synchronized void receiveTargetDying() {
		on = false;
		notifyAll();
		if (offing) {
			offing = false;
			return;
		}

		//offing == false�̏ꍇ�Aoff���ꂸ��target�̏������I���A�܂��O�^�G���[���������Ă���Ƃ�������
		//�I���Ď��X���b�h��p�ӂ��A�m���ɏI�����Ă���I����ʒm����
		final Thread myTargetThread = currentTargetThread;

		new Thread(getNextThreadName("Joinner")) {

			@Override
			public void run() {
				while (myTargetThread.isAlive()) {
					try {
						myTargetThread.join();
					} catch (InterruptedException e) {}
				}
				target.receiveTargetDead();
			}
		}.start();
	}

	/**
	 * ��~�����҂���Ԃ��I���A�^�[�Q�b�g�X���b�h���I������܂őҋ@���܂��B
	 */
	private synchronized void waitWhileStoppingAndJoinTarget() {
		while (offing) {
			try {
				wait();
			} catch (InterruptedException e) {}
		}

		//���̃X���b�h�Ɋ��荞�܂�Ă��ATarget���s�X���b�h���I������܂ł͕��A���Ȃ�
		while (currentTargetThread != null && currentTargetThread.isAlive()) {
			try {
				currentTargetThread.join();
			} catch (InterruptedException e) {}
		}
		currentTargetThread = null;
	}

	static abstract class Target implements Runnable {

		private Switch mySwitch;

		@Override
		public void run() {
			try {
				execute();
			} finally {
				mySwitch.receiveTargetDying();
			}
		}

		abstract String getName();

		abstract void execute();

		abstract void receiveTargetStarted();

		abstract void receiveTargetDead();
	}

	private static String getNextThreadName(String name) {
		synchronized (threadCounterMap) {
			int current;
			Integer counter = threadCounterMap.get(name);
			if (counter == null) {
				current = 0;
			} else {
				current = counter.intValue();
			}
			threadCounterMap.put(name, new Integer(current + 1));
			return "CircularFramework-" + name + "-" + current;
		}
	}
}
