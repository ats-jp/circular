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
 * �|�[�����O�����ɂ�����A���V�X�e������̓��͏�����\���܂��B
 * <p>
 * ���͏�����
 * <ol>
 * <li>���V�X�e���̃��[�N�e�[�u���ɐV�K�ǉ����ꂽ���R�[�h����荞��
 * <li>�ǉ����ꂽ���R�[�h�������ʎq����Ή�����{@link InputPhaseElement}��I������
 * <li>{@link InputPhaseElement}�����s����
 * <li>����̎�荞�݂Ŏg�p�������R�[�h���폜����
 * <li>�ꌏ�������邲�ƂɑS�Ă̐ڑ���commit����
 * </ol>
 * �Ƃ�������ɂȂ�܂��B
 * <br>
 * ���̃C���^�[�t�F�C�X����������N���X�̓��[�N�e�[�u����ɑ΂��A���ꂼ���Â�`����K�v������܂��B
 *
 * @author ��t �N�k
 * @version $Name:  $
 */
public abstract class InputPhase implements Common, Phase {

	/**
	 * ����InputPhase�ɑΉ����郏�[�N�e�[�u������ԋp���܂��B
	 * 
	 * @return ���[�N�e�[�u����
	 */
	public abstract String getInputTableName();

	/**
	 * {@link InputPhaseElement}�����肷�邽�߂̒l�������ږ���ԋp���܂��B
	 * <p>
	 * �������A����InputPhase�ɑΉ����郏�[�N�e�[�u�������ʉ\�ȏ���K�v�Ƃ��Ȃ��ꍇ�i{@link InputPhaseElement}�����ނŏ\���ȏꍇ�j�Anull��ԋp���邱�Ƃ��ł��܂��B���̏ꍇ�A{@link InputPhase#selectInputPhaseElement(Object)}�ɓn�����l��null�ɂȂ�܂��B
	 * <br>
	 * �����ł͂Ȃ��ꍇ�A���̍��ڂ�{@link InputPhase#getInputTableName()}�Ŏw�肳���e�[�u���ɑ��݂��Ă��Ȃ���΂Ȃ�܂���B
	 * 
	 * @return ���ʉ\�ȏ��������ږ�����null
	 * @see InputPhase#getInputTableName()
	 * @see InputPhase#selectInputPhaseElement(java.lang.Object)
	 * @see InputPhase#createNewInstance()
	 */
	public abstract String getElementIdentifiableColumnName();

	/**
	 * ���ʎq����A�Ή�����{@link InputPhaseElement}�̃C���X�^���X��ԋp���܂��B
	 * <p>
	 * ���ʎq�Ƃ́A{@link InputPhase#getInputTableName()}�Ŏw�肳���e�[�u���ɑ��݂���A{@link InputPhase#getElementIdentifiableColumnName()}�Ŏw�肳��鍀�ڂ̒l�̂��Ƃł��B
	 * <br>
	 * {@link InputPhase#getElementIdentifiableColumnName()}��null��ԋp�����ꍇ�A�p�����[�^�ɂ�null���n����܂��B
	 * 
	 * @param identifier InputPhaseElement���ʎq����null
	 * @return ���ʎq�ɑΉ�����InputPhaseElement
	 * @see InputPhase#getInputTableName()
	 * @see InputPhase#getElementIdentifiableColumnName()
	 * @see InputPhase#createNewInstance()
	 */
	public abstract InputPhaseElement selectInputPhaseElement(Object identifier);

	/**
	 * �V����{@link CircularBean}�C���X�^���X�𐶐����܂��B
	 * <p>
	 * {@link CircularBean}�͂���InputPhase���ǂݍ��ރe�[�u���A�܂�{@link InputPhase#getInputTableName()}�Ŏw�肳���e�[�u���̃��C�A�E�g�Ƀ}�b�`�����N���X�̃C���X�^���X�łȂ���΂Ȃ�܂���B
	 * 
	 * @return ����InputPhase�ɑΉ�����{@link circular.framework.phase.CircularBean}
	 * @see InputPhase#getInputTableName()
	 */
	public abstract CircularBean createNewInstance();

	/**
	 * ����InputPhase�ɓ��L�̑O�������s���܂��B
	 * 
	 * @throws InterruptedException ���̏����̓�����{@link Thread#sleep(long)}�����s���Ă���ꍇ�ŁA�����[�U�����~���w���i{@link Thread#interrupt()}���t���[�����[�N�ɂ����s�j���ꂽ�ꍇ�ɔ���
	 * @throws AbortNotice InputPhase����{@link Cycle}�𒆒f�������ꍇ
	 */
	public abstract void prepare() throws InterruptedException, AbortNotice;

	/**
	 * ����InputPhase�ɓ��L�̌㏈�����s���܂��B
	 * 
	 * @throws InterruptedException ���̏����̓�����{@link Thread#sleep(long)}�����s���Ă���ꍇ�ŁA�����[�U�����~���w���i{@link Thread#interrupt()}���t���[�����[�N�ɂ����s�j���ꂽ�ꍇ�ɔ���
	 * @throws AbortNotice InputPhase����{@link Cycle}�𒆒f�������ꍇ
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
	 * InputPhase�̃��C���ƂȂ鏈���ł��B
	 *
	 * @param inputConnection �g�p����ڑ�
	 * @throws InterruptedException ���̏����̓�����{@link Thread#sleep(long)}�����s���Ă���ꍇ�ŁA�����[�U�����~���w���i{@link Thread#interrupt()}���t���[�����[�N�ɂ����s�j���ꂽ�ꍇ�ɔ���
	 * @throws AbortNotice InputPhase����{@link Cycle}�𒆒f�������ꍇ
	 * @throws SQLException SQL�֘A�ŗ�O�����������ꍇ
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
				//�������J�n����O�ɁA��~����Ă��Ȃ����`�F�b�N
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
						throw new SQLException("�폜�Ɏ��s���܂����Btable="
							+ tableName
							+ ", "
							+ getSequenceColumnName()
							+ "="
							+ sequence);
					}

					if (Cycle.getConfigure().useBackup()) bean.backup(Cycle.getBackupConnection());

					//�ꌏ��荞�݊������邲�ƂɑS�Ă̐ڑ���commit����
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
