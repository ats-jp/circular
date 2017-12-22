package circular.framework.phase;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import circular.framework.AbortNotice;
import circular.framework.CircularException;
import circular.framework.Cycle;
import circular.framework.Phase;

/**
 * ポーリング処理における、他システムからの入力処理を表します。
 * <p>
 * 入力処理は
 * <ol>
 * <li>他システムのワークテーブルに新規追加されたレコードを取り込む
 * <li>追加されたレコードが持つ識別子から対応する{@link InputPhaseElement}を選択する
 * <li>{@link InputPhaseElement}を実行する
 * <li>今回の取り込みで使用したレコードを削除する
 * <li>一件処理するごとに全ての接続をcommitする
 * </ol>
 * という流れになります。
 * <br>
 * このインターフェイスを実装するクラスはワークテーブル一つに対し、それぞれ一つづつ定義する必要があります。
 *
 * @author 千葉 哲嗣
 * @version $Name:  $
 */
public abstract class InputPhase implements Common, Phase {

	/**
	 * このInputPhaseに対応するワークテーブル名を返却します。
	 * 
	 * @return ワークテーブル名
	 */
	public abstract String getInputTableName();

	/**
	 * {@link InputPhaseElement}を決定するための値をもつ項目名を返却します。
	 * <p>
	 * ただし、このInputPhaseに対応するワークテーブルが識別可能な情報を必要としない場合（{@link InputPhaseElement}が一種類で十分な場合）、nullを返却することができます。その場合、{@link InputPhase#selectInputPhaseElement(Object)}に渡される値はnullになります。
	 * <br>
	 * そうではない場合、この項目は{@link InputPhase#getInputTableName()}で指定されるテーブルに存在していなければなりません。
	 * 
	 * @return 識別可能な情報を持つ項目名又はnull
	 * @see InputPhase#getInputTableName()
	 * @see InputPhase#selectInputPhaseElement(java.lang.Object)
	 * @see InputPhase#createNewInstance()
	 */
	public abstract String getElementIdentifiableColumnName();

	/**
	 * 識別子から、対応する{@link InputPhaseElement}のインスタンスを返却します。
	 * <p>
	 * 識別子とは、{@link InputPhase#getInputTableName()}で指定されるテーブルに存在する、{@link InputPhase#getElementIdentifiableColumnName()}で指定される項目の値のことです。
	 * <br>
	 * {@link InputPhase#getElementIdentifiableColumnName()}でnullを返却した場合、パラメータにはnullが渡されます。
	 * 
	 * @param identifier InputPhaseElement識別子又はnull
	 * @return 識別子に対応するInputPhaseElement
	 * @see InputPhase#getInputTableName()
	 * @see InputPhase#getElementIdentifiableColumnName()
	 * @see InputPhase#createNewInstance()
	 */
	public abstract InputPhaseElement selectInputPhaseElement(Object identifier);

	/**
	 * 新しい{@link CircularBean}インスタンスを生成します。
	 * <p>
	 * {@link CircularBean}はこのInputPhaseが読み込むテーブル、つまり{@link InputPhase#getInputTableName()}で指定されるテーブルのレイアウトにマッチしたクラスのインスタンスでなければなりません。
	 * 
	 * @return このInputPhaseに対応する{@link circular.framework.phase.CircularBean}
	 * @see InputPhase#getInputTableName()
	 */
	public abstract CircularBean createNewInstance();

	/**
	 * このInputPhaseに特有の前処理を行います。
	 * 
	 * @throws InterruptedException この処理の内部で{@link Thread#sleep(long)}等を行っている場合で、かつユーザから停止が指示（{@link Thread#interrupt()}がフレームワークにより実行）された場合に発生
	 * @throws AbortNotice InputPhase自ら{@link Cycle}を中断したい場合
	 */
	public abstract void prepare() throws InterruptedException, AbortNotice;

	/**
	 * このInputPhaseに特有の後処理を行います。
	 * 
	 * @throws InterruptedException この処理の内部で{@link Thread#sleep(long)}等を行っている場合で、かつユーザから停止が指示（{@link Thread#interrupt()}がフレームワークにより実行）された場合に発生
	 * @throws AbortNotice InputPhase自ら{@link Cycle}を中断したい場合
	 */
	public abstract void terminate() throws InterruptedException, AbortNotice;

	@Override
	public void execute() throws InterruptedException, AbortNotice {
		prepare();
		Connection inputConnection = Cycle.getInputConnection();
		if (inputConnection != null) {
			try {
				executeInputPhase(inputConnection);
			} catch (SQLException e) {
				throw new CircularException(e);
			}
		}
		terminate();
	}

	/**
	 * InputPhaseのメインとなる処理です。
	 *
	 * @param inputConnection 使用する接続
	 * @throws InterruptedException この処理の内部で{@link Thread#sleep(long)}等を行っている場合で、かつユーザから停止が指示（{@link Thread#interrupt()}がフレームワークにより実行）された場合に発生
	 * @throws AbortNotice InputPhase自ら{@link Cycle}を中断したい場合
	 * @throws SQLException SQL関連で例外が発生した場合
	 */
	protected void executeInputPhase(Connection inputConnection)
		throws InterruptedException, AbortNotice, SQLException {
		String tableName = getInputTableName();
		Statement selectStatement = inputConnection.createStatement();
		ResultSet result = null;
		try {
			result = selectStatement.executeQuery("select * from "
				+ tableName
				+ " order by "
				+ getSequenceColumnName());
			while (result.next()) {
				//処理を開始する前に、停止されていないかチェック
				Cycle.checkInterrupted();

				String columnName = getElementIdentifiableColumnName();

				InputPhaseElement element;
				if (columnName == null) {
					element = selectInputPhaseElement(null);
				} else {
					element = selectInputPhaseElement(result.getObject(columnName));
				}

				CircularBean bean = createNewInstance();
				bean.init(result);
				element.execute(bean);
				BigDecimal sequence = result.getBigDecimal(getSequenceColumnName());
				Statement deleteStatement = inputConnection.createStatement();
				try {
					if (deleteStatement.executeUpdate("delete from "
						+ tableName
						+ " where "
						+ getSequenceColumnName()
						+ " = "
						+ sequence) != 1) {
						throw new SQLException("削除に失敗しました。table="
							+ tableName
							+ ", "
							+ getSequenceColumnName()
							+ "="
							+ sequence);
					}

					if (Cycle.getConfigure().useBackup()) bean.backup(Cycle.getBackupConnection());

					//一件取り込み完了するごとに全ての接続をcommitする
					Cycle.commitAllConnections();
				} finally {
					deleteStatement.close();
				}
			}
		} finally {
			try {
				if (result != null) result.close();
			} finally {
				selectStatement.close();
			}
		}
	}
}
