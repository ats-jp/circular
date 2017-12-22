package circular.framework.phase;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import circular.framework.CircularException;
import circular.framework.Cycle;

/**
 * Circular Framework���g�p���郏�[�N�e�[�u���̈�s��\�����ۊ��N���X�ł��B
 * 
 * @author ��t �N�k
 * @version $Name:  $
 */
public abstract class CircularBean implements Common {

	private String timestampColumnName;

	private String sequenceColumnName;

	private Timestamp timestamp;

	private BigInteger sequence;

	/**
	 * �f�t�H���g�R���X�g���N�^�ł��B
	 */
	public CircularBean() {
		timestampColumnName = getTimestampColumnName();
		sequenceColumnName = getSequenceColumnName();
	}

	/**
	 * {@link CircularBean#getTableName()}�ŕԂ����e�[�u�����ɁA{@link CircularBean#setValuesTo(Inserter)}���ŃZ�b�g���ꂽ�l�����s��ǉ����܂��B
	 * <p>
	 * �o�͐�͑��V�X�e���ւ̏o�͗p���[�N�e�[�u���̂���DB�ƂȂ�܂��B
	 * 
	 * @throws CircularException DB�֘A�̃G���[�����������ꍇ
	 */
	public void insert() {
		Inserter inserter = createNewInserter();
		setValuesTo(inserter);
		try {
			inserter.executeInsert(Cycle.getOutputConnection());
		} catch (SQLException e) {
			throw new CircularException(e);
		}
	}

	/**
	 * ����Bean���ǉ����ꂽ������\���V�[�P���X�ԍ���߂��܂��B
	 * 
	 * @return �V�[�P���X�ԍ�
	 */
	public BigInteger getSequence() {
		return sequence;
	}

	/**
	 * ����Bean���e�[�u���ɒǉ����ꂽ������Ԃ��܂��B
	 * 
	 * @return �o�^����
	 */
	public Timestamp getTimestamp() {
		return timestamp;
	}

	/**
	 * ����Bean�̕�����\����Ԃ��܂��B
	 * <p>
	 * ���̃��\�b�h�͂����܂ł��A�e�X�g�p�Ƃ��Ďg�p���Ă��������B
	 * 
	 * @return ����Bean�̕�����\��
	 */
	@Override
	public String toString() {
		Method[] methods = this.getClass().getMethods();
		Object[] parameters = new Object[0];
		StringBuffer buffer = new StringBuffer();
		for (Method method : methods) {
			if (method.getName().startsWith("get")) {
				try {
					buffer.append(method.getName()
						+ "()=["
						+ method.invoke(this, parameters)
						+ "], ");
				} catch (Exception e) {
					throw new Error(e);
				}
			}
		}
		return buffer.toString().substring(0, buffer.length() - 2);
	}

	/**
	 * ����Bean�ɑΉ����郏�[�N�e�[�u������ԋp���܂��B
	 * 
	 * @return ���[�N�e�[�u����
	 */
	protected abstract String getTableName();

	/**
	 * {@link ResultSet}�̎��l������Bean�Ɏ�荞�݂܂��B
	 * 
	 * @param result �J�[�\��������Bean�ɑ΂��郌�R�[�h�ɂ���{@link ResultSet}
	 * @throws SQLException {@link ResultSet}���쎞�ɗ�O�����������ꍇ
	 */
	protected abstract void initializeBean(ResultSet result)
		throws SQLException;

	/**
	 * {@link Inserter}�ɁA����Bean�̎��l���Z�b�g���܂��B
	 * 
	 * @param inserter INSERT�p�I�u�W�F�N�g
	 */
	protected abstract void setValuesTo(Inserter inserter);

	/**
	 * {@link InputPhase}�œǂݍ��񂾃f�[�^���A�o�b�N�A�b�v�̈�ɏo�͂��܂��B
	 * <p>
	 * �Ǝ��ɐ��������C���X�^���X�ł͓o�^�����ƃV�[�P���X�ԍ��������Ȃ����߁A�o�b�N�A�b�v�͂ł��܂���B
	 * 
	 * @param connection �o�b�N�A�b�v�Ɏg�p����ڑ�
	 * @throws CircularException DB�֘A�̃G���[�����������ꍇ
	 */
	protected void backup(Connection connection) {
		Inserter inserter = createNewInserter();
		setValuesTo(inserter);
		try {
			inserter.executeBackup(connection);
		} catch (SQLException e) {
			throw new CircularException(e);
		}
	}

	/**
	 * {@link ResultSet}�̎��o�^�����ƃV�[�P���X�ԍ����܂ޑS�Ă̒l������Bean�Ɏ�荞�݂܂��B
	 * 
	 * @param result �J�[�\��������Bean�ɑ΂��郌�R�[�h�ɂ���{@link ResultSet}
	 * @throws SQLException {@link ResultSet}���쎞�ɗ�O�����������ꍇ
	 */
	protected void init(ResultSet result) throws SQLException {
		timestamp = result.getTimestamp(timestampColumnName);
		sequence = result.getBigDecimal(sequenceColumnName).toBigInteger();
		initializeBean(result);
	}

	/**
	 * �o�b�N�A�b�v�e�[�u���p�̐ڔ����Ԃ��܂��B
	 * <p>
	 * �K�v�ɉ����ăI�[�o�[���C�h���Ă��������B
	 * <p>
	 * �f�t�H���g�� _BACKUP �ł��B
	 * 
	 * @return �o�b�N�A�b�v�e�[�u���p�̐ڔ���
	 */
	protected String getBackupTableSuffix() {
		return "_BACKUP";
	}

	/**
	 * ����Bean�������Ŏg�p����{@link Inserter}�̃C���X�^���X�𐶐����܂��B
	 * <p>
	 * �Ǝ���{@link Inserter}���g�p����ꍇ�̓I�[�o�[���C�h���Ă��������B
	 * 
	 * @return INSERT�p�I�u�W�F�N�g
	 */
	protected Inserter createNewInserter() {
		return new Inserter();
	}

	private static String join(Collection values) {
		StringBuffer buffer = new StringBuffer();
		for (Object object : values) {
			buffer.append(object);
			buffer.append(", ");
		}
		String value = buffer.toString();
		if (value.length() == 0) return value;
		return value.substring(0, value.length() - 2);
	}

	/**
	 * ���V�X�e�����[�N�e�[�u���ɐV�K�Ƀf�[�^���o�͂��邽�߂̃c�[���N���X�ł��B
	 * <p>
	 * {@link CircularBean}���ŃC���X�^���X������A�g�p����܂��B�U������ύX����ɂ̓I�[�o�[���C�h����K�v������܂��B
	 * 
	 * @author ��t �N�k
	 * @version $Name:  $
	 */
	public class Inserter {

		private final List<String> columnNames = new ArrayList<String>();

		private final List<Binder> binders = new ArrayList<Binder>();

		/**
		 * ���̃N���X�̃R���X�g���N�^�ł��B
		 */
		protected Inserter() {}

		/**
		 * �w�肳�ꂽ���ڂɁA�w�肳�ꂽ�l�𐔒l�^�Ƃ��ēo�^���܂��B
		 * @param columnName ���l�^���ڂ̖���
		 * @param value �l
		 */
		public void addInt(String columnName, int value) {
			columnNames.add(columnName);
			binders.add(new IntBinder(value));
		}

		/**
		 * �w�肳�ꂽ���ڂɁA�w�肳�ꂽ�l�𐔒l�^�Ƃ��ēo�^���܂��B
		 * @param columnName ���l�^���ڂ̖���
		 * @param value �l
		 */
		public void addBigDecimal(String columnName, BigDecimal value) {
			columnNames.add(columnName);
			binders.add(new BigDecimalBinder(value));
		}

		/**
		 * �w�肳�ꂽ���ڂɁA�w�肳�ꂽ�l�𕶎���^�Ƃ��ēo�^���܂��B
		 * @param columnName ������^���ڂ̖���
		 * @param value �l
		 */
		public void addString(String columnName, String value) {
			columnNames.add(columnName);
			binders.add(new StringBinder(value));
		}

		/**
		 * �w�肳�ꂽ���ڂɁA�w�肳�ꂽ�l����t�^�Ƃ��ēo�^���܂��B
		 * @param columnName ���t�^���ڂ̖���
		 * @param value �l
		 */
		public void addDate(String columnName, Date value) {
			columnNames.add(columnName);
			binders.add(new DateBinder(value));
		}

		/**
		 * �w�肳�ꂽ���ڂɁA�w�肳�ꂽ�l���^�C���X�^���v�^�Ƃ��ēo�^���܂��B
		 * @param columnName �^�C���X�^���v�^���ڂ̖���
		 * @param value �l
		 */
		public void addTimestamp(String columnName, Timestamp value) {
			columnNames.add(columnName);
			binders.add(new TimestampBinder(value));
		}

		/**
		 * INSERT�����s���܂��B
		 * 
		 * @param connection INSERT�����s����ڑ�
		 * @throws SQLException {@link Connection}���쎞�ɗ�O�����������ꍇ
		 */
		protected void executeInsert(Connection connection) throws SQLException {
			String sql = "insert into "
				+ getTableName()
				+ " ("
				+ getColumnNames()
				+ ", "
				+ timestampColumnName
				+ ") values ("
				+ getPlaceHolders()
				+ ", SYSDATE)";

			execute(connection.prepareStatement(sql));
		}

		/**
		 * �o�b�N�A�b�v�����s���܂��B
		 * 
		 * @param connection �o�b�N�A�b�v�����s����ڑ�
		 * @throws SQLException {@link Connection}���쎞�ɗ�O�����������ꍇ
		 */
		protected void executeBackup(Connection connection) throws SQLException {
			String sql = "insert into "
				+ getTableName()
				+ getBackupTableSuffix()
				+ " ("
				+ getColumnNames()
				+ ", "
				+ timestampColumnName
				+ ", "
				+ sequenceColumnName
				+ ") values ("
				+ getPlaceHolders()
				+ ", ?, ?)";

			binders.add(new TimestampBinder(timestamp));
			binders.add(new StringBinder(sequence.toString()));

			execute(connection.prepareStatement(sql));
		}

		/**
		 * ���ݕێ����Ă��鍀�ڂ�SQL���Ŏg�p����悤�ɃJ���}�ŘA�������������Ԃ��܂��B
		 * 
		 * @return �J���}�ŘA���������ږ�
		 */
		protected String getColumnNames() {
			return join(columnNames);
		}

		/**
		 * ���ݕێ����Ă���l���v���[�X�z���_�Ƃ���SQL���Ŏg�p����悤�ɃJ���}�ŘA�������������Ԃ��܂��B
		 * 
		 * @return �J���}�ŘA�������v���[�X�z���_
		 */
		protected String getPlaceHolders() {
			return join(binders);
		}

		/**
		 * �l�����o�C���h��{@link PreparedStatement}�ɁA�ێ�����l���o�C���h���ASQL�����s���܂��B
		 * 
		 * @param statement �l�����o�C���h��{@link PreparedStatement}
		 * @throws SQLException {@link PreparedStatement}���쎞�ɗ�O�����������ꍇ
		 */
		protected void execute(PreparedStatement statement) throws SQLException {
			try {
				int size = binders.size();
				for (int i = 0; i < size; i++) {
					binders.get(i).bind(i + 1, statement);
				}

				statement.executeUpdate();
			} finally {
				statement.close();
			}
		}
	}

	/**
	 * {@link PreparedStatement}�Ƀo�C���h����l��ێ����邽�߂̒��ۊ��N���X�ł��B
	 */
	public static abstract class Binder {

		/**
		 * {@link PreparedStatement}�ɑ΂��āA�^�ɑΉ��������\�b�h�ɕێ�����l��ݒ肵�܂��B
		 * <p>
		 * {@link SQLException}���X���[�����ƁA�t���[�����[�N���ŉ񕜕s�\�ȗ�O�Ƃ��Ĉ����܂��B
		 * @param index {@link PreparedStatement}�̃v���[�X�z���_�ʒu
		 * @param statement �ΏۂƂȂ�{@link PreparedStatement}
		 * @throws SQLException �񕜕s�\�ȗ�O�����������ꍇ
		 */
		abstract void bind(int index, PreparedStatement statement)
			throws SQLException;

		/**
		 * ���̃N���X�̃C���X�^���X�𕶎���Ƃ��ĕ]������ƁASQL�v���[�X�z���_��"?"�ƂȂ�܂��B
		 * @return �v���[�X�z���_
		 */
		@Override
		public String toString() {
			return "?";
		}
	}

	/**
	 * {@link PreparedStatement}�Ƀo�C���h���鐮���^�̒l��ێ����邽�߂̃N���X�ł��B
	 */
	public static class IntBinder extends Binder {

		private final int value;

		/**
		 * ���̃N���X�̃R���X�g���N�^�ł��B
		 * 
		 * @param value ����Binder�̒l
		 */
		public IntBinder(int value) {
			this.value = value;
		}

		@Override
		void bind(int index, PreparedStatement statement) throws SQLException {
			statement.setInt(index, value);
		}
	}

	/**
	 * {@link PreparedStatement}�Ƀo�C���h���鐔�l�^�̒l��ێ����邽�߂̃N���X�ł��B
	 */
	public static class BigDecimalBinder extends Binder {

		private final BigDecimal value;

		/**
		 * ���̃N���X�̃R���X�g���N�^�ł��B
		 * 
		 * @param value ����Binder�̒l
		 */
		public BigDecimalBinder(BigDecimal value) {
			this.value = value;
		}

		@Override
		void bind(int index, PreparedStatement statement) throws SQLException {
			statement.setBigDecimal(index, value);
		}
	}

	/**
	 * {@link PreparedStatement}�Ƀo�C���h���镶����^�̒l��ێ����邽�߂̃N���X�ł��B
	 */
	public static class StringBinder extends Binder {

		private final String value;

		/**
		 * ���̃N���X�̃R���X�g���N�^�ł��B
		 * 
		 * @param value ����Binder�̒l
		 */
		public StringBinder(String value) {
			this.value = value;
		}

		@Override
		void bind(int index, PreparedStatement statement) throws SQLException {
			statement.setString(index, value);
		}
	}

	/**
	 * {@link PreparedStatement}�Ƀo�C���h������t�^�̒l��ێ����邽�߂̃N���X�ł��B
	 */
	public static class DateBinder extends Binder {

		private final Date value;

		/**
		 * ���̃N���X�̃R���X�g���N�^�ł��B
		 * 
		 * @param value ����Binder�̒l
		 */
		public DateBinder(Date value) {
			this.value = value;
		}

		@Override
		void bind(int index, PreparedStatement statement) throws SQLException {
			statement.setDate(index, value);
		}
	}

	/**
	 * {@link PreparedStatement}�Ƀo�C���h����TIMESTAMP�^�̒l��ێ����邽�߂̃N���X�ł��B
	 */
	public static class TimestampBinder extends Binder {

		private final Timestamp value;

		/**
		 * ���̃N���X�̃R���X�g���N�^�ł��B
		 * 
		 * @param value ����Binder�̒l
		 */
		public TimestampBinder(Timestamp value) {
			this.value = value;
		}

		@Override
		void bind(int index, PreparedStatement statement) throws SQLException {
			statement.setTimestamp(index, value);
		}
	}
}
