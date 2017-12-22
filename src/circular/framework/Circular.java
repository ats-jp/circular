package circular.framework;

import circular.framework.Switch.Target;

/**
 * Circular Frameworkの中心となる、実際にポーリングを行うクラスです。
 * <p>
 * ポーリングの開始／停止を指示することが可能です。
 * 
 * @author 千葉 哲嗣
 * @version $Name:  $
 */
public class Circular {

	private final Configure config;

	private final Cycle cycle;

	private final Switch mySwitch;

	/**
	 * 唯一のコンストラクタです。
	 * 
	 * @param config このCircularに対する設定
	 */
	public Circular(Configure config) {
		this.config = config;
		cycle = new Cycle(config);
		mySwitch = new Switch(new CycleRunner());
	}

	/**
	 * ポーリングを開始します。
	 */
	public void start() {
		mySwitch.on();
	}

	/**
	 * ポーリングを停止します。
	 */
	public void stop() {
		mySwitch.off();
	}

	/**
	 * 現在の実行状況を返却します。
	 * <p>
	 * 実行中の場合はtrue、停止中の場合はfalseとなります。{@link Circular#stop()}が呼ばれてから、完全に停止するまでは実行中とみなされます。
	 * 
	 * @return 実行中の場合true
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
					//停止指示があった場合
					//停止
					config.observer.receiveInterrupted();
					break;
				} catch (CircularError e) {
					//致命的な問題が発生した場合
					//停止
					ErrorUtils.notifyMessagelessFatalError(
						config.observer,
						Circular.class,
						e);
					break;
				} catch (Throwable t) {
					//想定外の問題が発生した場合（バグ等）
					//停止
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
