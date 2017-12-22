package circular.framework;

/**
 * スローすることで{@link Cycle}を中断することができる通知クラスです。
 * <p>
 * この通知がスローされると、Circular Frameworkはサイクル中でも処理を中断し、次のサイクルへ遷移します。
 * <br>
 * プラグイン側から処理の中断を行いたい場合に使用してください。
 * 
 * @author 千葉 哲嗣
 * @version $Name:  $
 */
@SuppressWarnings("serial")
public class AbortNotice extends Throwable {}
