package nl.toolforge.karma.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * <p>Class representing a Karma errorcode. These error codes are localized to support different languages. Errorcodes
 * are defined in ranges. Exceptions that are supported by Karma, define these ranges.</p>
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public final class ErrorCode {

  private static Log logger = LogFactory.getLog(ErrorCode.class);

  private static Locale currentLocale = null;

  private Object[] messageArguments = new Object[]{};

  static {

    // todo : ensure that LocalEnvironment knows about the current Locale and pass it on to here.
    //
    currentLocale = Locale.ENGLISH;
  }

  private String errorCode = null;

  /**
   * Creates an error code. Error codes must comply to the following pattern : <code>[A-Z]{3}-\d{5}</code>. Examples are:
   * <code>MAN-00001</code>, <code>CMD-10020</code>.
   *
   * @param errorCode
   */
  public ErrorCode(String errorCode) {

    if (!errorCode.matches("[A-Z]{3}-\\d{5}")) {
      throw new IllegalArgumentException("Illegal error code format.");
    }
    this.errorCode = errorCode;
  }

  /**
   * Assigns message arguments to this error code as per the <code>MessageFormat</code> definition.
   *
   * @param messageArguments An Object array (currently only <code>String</code> instances are supported).
   */
  public final void setMessageArguments(Object[] messageArguments) {
    this.messageArguments = messageArguments;
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
   *   resourcebundle could not be found for <code>locale</code>.
   */
  public String getErrorMessage(Locale locale) {

    if (locale == null) {
      locale = currentLocale;
    }

    ResourceBundle bundle = null;

    try {
      bundle = ResourceBundle.getBundle("error-messages", locale);
    } catch (MissingResourceException m) {
      logger.info("No resource bundle available for locale " + locale);
      return getErrorCodeString();
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
      String message = bundle.getString("message." + getErrorCodeString());

      if (getMessageArguments().length != 0) {
        MessageFormat messageFormat = new MessageFormat(message);
        return messageFormat.format(getMessageArguments());
      } else {
        return message;
      }

    } catch (RuntimeException r) {
      logger.error("No message found for errorcode : " + getErrorCodeString());
      return getErrorCodeString();
    }
  }

  /**
   * Gets the error message for the current locale.
   *
   * @return The error message for the error code.
   */
  public String getErrorMessage() {
    return getErrorMessage(currentLocale);
  }

  /**
   * Gets this instance' error code.
   *
   * @return This instance' error code.
   */
  public String getErrorCodeString() {
    return errorCode;
  }

  private Object[] getMessageArguments() {
    return messageArguments;
  }
}
