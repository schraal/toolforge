package nl.toolforge.karma.core.exception;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * <p>Class representing a Karma errorcode. These error codes are localized to support different languages. Errorcodes
 * are defined in ranges. Please refer to the online documentation for all error code ranges.
 * <p/>
 * <p>This class should be extended by modules that must implement an
 * errorcoding scheme.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public class ErrorCode {

	private static Log logger = LogFactory.getLog(ErrorCode.class);

	private String errorCode = null;

	public ErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	/**
	 * <p>Gets a localized error message for the <code>ErrorCode</code> instance. Error messages are defined in a
	 * <code>error-messages-&lt;locale&gt;.properties</code> (e.g. <code>error-messages-NL.properties</code>). A message
	 * text is identified by a key <code>message.</code> concatenated with {@link #getErrorCodeString}.
	 * <p/>
	 * </p>When no resource bundle can be found for <code>locale</code>, the default locale <code>Locale.ENGLISH</code> is
	 * used.
	 *
	 * @param locale A locale object (e.g. representing the current locale of the user environment).
	 * @return A localized error message or {@link #getErrorCodeString} when no message was found for this errorcode or the
	 *         resourcebundle could not be found for <code>locale</code>.
	 *         <p/>
	 *         TODO ResourceBundle should be cached.
	 */
	public String getErrorMessage(Locale locale) {

		ResourceBundle bundle = null;

		try {
			bundle = ResourceBundle.getBundle("error-messages", locale);
		} catch (MissingResourceException m) {
			logger.info("No resource bundle available for locale " + locale);
		}

		if (bundle == null) {
			try {
				locale = Locale.ENGLISH;
				bundle = ResourceBundle.getBundle("error_messages", locale);
			} catch (MissingResourceException m) {
				logger.error("No default resource bundle available for locale " + locale);
				return getErrorCodeString();
			}
		}

		try {
			return bundle.getString("message." + getErrorCodeString());
		} catch (RuntimeException r) {
			logger.error("No message found for errorcode : " + getErrorCodeString());
			return getErrorCodeString();
		}
	}

	/**
	 * Gets this instance' error code.
	 *
	 * @return This instance' error code.
	 */
	public String getErrorCodeString() {
		return errorCode;
	}
}

