package circular.ui;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

import circular.framework.CircularException;
import circular.framework.CircularProperties;

/**
 * メール送信機能クラスです。
 * 
 * @author 千葉 哲嗣
 * @version $Name:  $
 */
public class Mail {

	/**
	 * SMTPサーバアドレスのキー
	 */
	public static final String MAIL_SMTP_HOST_KEY = "mail.smtp.host";

	/**
	 * SMTPサーバに接続する際に認証が必要かどうかのキー
	 */
	public static final String MAIL_SMTP_AUTH_KEY = "mail.smtp.auth";

	/**
	 * SMTPサーバ認証用ユーザ名のキー
	 */
	public static final String MAIL_SMTP_USERNAME_KEY = "mail.smtp.username";

	/**
	 * SMTPサーバ認証用パスワードのキー
	 */
	public static final String MAIL_SMTP_PASSWORD_KEY = "mail.smtp.password";

	/**
	 * FROMのキー
	 */
	public static final String MAIL_ADDRESS_FROM_KEY = "mail.address.from";

	/**
	 * TO（カンマ区切りで複数指定可）のキー
	 */
	public static final String MAIL_ADDRESSES_TO_KEY = "mail.addresses.to";

	/**
	 * CC（カンマ区切りで複数指定可）のキー
	 */
	public static final String MAIL_ADDRESSES_CC_KEY = "mail.addresses.cc";

	/**
	 * BCC（カンマ区切りで複数指定可）のキー
	 */
	public static final String MAIL_ADDRESSES_BCC_KEY = "mail.addresses.bcc";

	private final MimeMessage message;

	/**
	 * 渡されたpropertiesを使用してインスタンスを生成します。
	 * 
	 * @param properties 設定ファイルをロードした{@link CircularProperties}
	 */
	public Mail(CircularProperties properties) {
		Properties sessionProperties = new Properties();
		sessionProperties.setProperty(
			MAIL_SMTP_HOST_KEY,
			properties.getProperty(MAIL_SMTP_HOST_KEY));

		boolean useAuth = Boolean.valueOf(
			properties.getProperty(MAIL_SMTP_AUTH_KEY)).booleanValue();

		if (useAuth) {
			sessionProperties.setProperty(
				MAIL_SMTP_AUTH_KEY,
				properties.getProperty(MAIL_SMTP_AUTH_KEY));

			String username = properties.getProperty(MAIL_SMTP_USERNAME_KEY);
			String password = properties.getProperty(MAIL_SMTP_PASSWORD_KEY);

			message = new MimeMessage(Session.getInstance(
				sessionProperties,
				new SMTPAuthenticator(username, password)));
		} else {
			message = new MimeMessage(Session.getInstance(sessionProperties));
		}

		try {
			message.setFrom(new InternetAddress(
				properties.getProperty(MAIL_ADDRESS_FROM_KEY)));

			message.setRecipients(
				Message.RecipientType.TO,
				properties.getProperty(MAIL_ADDRESSES_TO_KEY));

			if (properties.containsKey(MAIL_ADDRESSES_CC_KEY)) message.setRecipients(
				Message.RecipientType.CC,
				properties.getProperty(MAIL_ADDRESSES_CC_KEY));

			if (properties.containsKey(MAIL_ADDRESSES_BCC_KEY)) message.setRecipients(
				Message.RecipientType.BCC,
				properties.getProperty(MAIL_ADDRESSES_BCC_KEY));
		} catch (MessagingException e) {
			throw new CircularException(e);
		}
	}

	/**
	 * サブジェクトをセットします。
	 * 
	 * @param subject このメールのサブジェクト
	 */
	public final void setSubject(String subject) {
		try {
			message.setSubject(MimeUtility.encodeText(
				treat(subject),
				"iso-2022-jp",
				"B"));
		} catch (UnsupportedEncodingException e) {
			throw new Error(e);
		} catch (MessagingException e) {
			throw new CircularException(e);
		}
	}

	/**
	 * 本文をセットします。
	 * 
	 * @param content このメールの本文
	 */
	public final void setMessage(String content) {
		try {
			message.setContent(
				treat(content),
				"text/plain; charset=\"iso-2022-jp\"");
		} catch (MessagingException e) {
			throw new CircularException(e);
		}
	}

	/**
	 * メールを送信します。
	 */
	public final void send() {
		try {
			Transport.send(message);
		} catch (MessagingException e) {
			throw new CircularException(e);
		}
	}

	private static class SMTPAuthenticator extends Authenticator {

		private final PasswordAuthentication auth;

		private SMTPAuthenticator(String username, String password) {
			auth = new PasswordAuthentication(username, password);
		}

		@Override
		public PasswordAuthentication getPasswordAuthentication() {
			return auth;
		}
	}

	private static String treat(String string) {
		if (string == null) return null;

		char[] chars = string.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			switch (chars[i]) {
			case 0xff5e:
				// FULLWIDTH TILDE -> WAVE DASH
				chars[i] = 0x301c;
				break;

			case 0x2015:
				// HORIZONTAL BAR -> EM DASH
				chars[i] = 0x2014;
				break;

			case 0x2225:
				// PARALLEL TO -> DOUBLE VERTICAL LINE
				chars[i] = 0x2016;
				break;

			case 0xff0d:
				// FULLWIDTH HYPHEN-MINUS -> MINUS SIGN
				chars[i] = 0x2212;
				break;
			}
		}
		return new String(chars);
	}
}