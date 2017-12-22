package circular.framework;

import circular.framework.Switch.Target;

/**
 * Circular Framework�̒��S�ƂȂ�A���ۂɃ|�[�����O���s���N���X�ł��B
 * <p>
 * �|�[�����O�̊J�n�^��~���w�����邱�Ƃ��\�ł��B
 * 
 * @author ��t �N�k
 * @version $Name:  $
 */
public class Circular {

	private final Configure config;

	private final Cycle cycle;

	private final Switch mySwitch;

	/**
	 * �B��̃R���X�g���N�^�ł��B
	 * 
	 * @param config ����Circular�ɑ΂���ݒ�
	 */
	public Circular(Configure config) {
		this.config = config;
		cycle = new Cycle(config);
		mySwitch = new Switch(new CycleRunner());
	}

	/**
	 * �|�[�����O���J�n���܂��B
	 */
	public void start() {
		mySwitch.on();
	}

	/**
	 * �|�[�����O���~���܂��B
	 */
	public void stop() {
		mySwitch.off();
	}

	/**
	 * ���݂̎��s�󋵂�ԋp���܂��B
	 * <p>
	 * ���s���̏ꍇ��true�A��~���̏ꍇ��false�ƂȂ�܂��B{@link Circular#stop()}���Ă΂�Ă���A���S�ɒ�~����܂ł͎��s���Ƃ݂Ȃ���܂��B
	 * 
	 * @return ���s���̏ꍇtrue
	 */
	public boolean isRunning() {
		return mySwitch.isRunning();
	}

	private class CycleRunner extends Target {

		@Override
		String getName() {
			return "CycleRunner";
		}

		@Override
		void execute() {
			while (true) {
				try {
					config.cycleStrategy.execute(cycle);
				} catch (InterruptedException e) {
					//��~�w�����������ꍇ
					//��~
					config.observer.receiveInterrupted();
					break;
				} catch (CircularError e) {
					//�v���I�Ȗ�肪���������ꍇ
					//��~
					ErrorUtils.notifyMessagelessFatalError(
						config.observer,
						Circular.class,
						e);
					break;
				} catch (Throwable t) {
					//�z��O�̖�肪���������ꍇ�i�o�O���j
					//��~
					ErrorUtils.notifyMessagelessFatalError(
						config.observer,
						Circular.class,
						t);
					break;
				}
			}
		}

		@Override
		void receiveTargetStarted() {
			config.observer.receiveCircularStarted();
		}

		@Override
		void receiveTargetDead() {
			config.observer.receiveCircularStopped();
		}
	}
}
