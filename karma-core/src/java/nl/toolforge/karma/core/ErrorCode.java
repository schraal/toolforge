/*
Karma core - Core of the Karma application
Copyright (C) 2004  Toolforge <www.toolforge.nl>

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/
package nl.toolforge.karma.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * <p>Class representing a Karma errorcode. These errorcodes are localized to support different languages. Errorcodes
 * are defined in ranges. Exceptions that are supported by Karma, define these ranges.</p>
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public final class ErrorCode {

  private static Log logger = LogFactory.getLog(ErrorCode.class);

  private static Locale currentLocale = null;

  private Object[] messageArguments = new Object[]{};

  private ResourceBundle messageBundle = null;

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

  public void setMessageBundle(ResourceBundle messageBundle) {
    this.messageBundle = messageBundle;
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

    String message = "";

    if (messageBundle == null) {
      try {
        locale = Locale.ENGLISH;
        messageBundle = ResourceBundle.getBundle("error-messages", locale);
      } catch (MissingResourceException m) {
        logger.error("No default resource bundle available for locale " + locale);
        return getErrorCodeString();
      }
    }

    try {
      message = messageBundle.getString("message." + getErrorCodeString());

      if (getMessageArguments().length != 0) {
        MessageFormat messageFormat = new MessageFormat(message);
        message = messageFormat.format(getMessageArguments());
      }

    } catch (RuntimeException r) {
      logger.error("No message found for errorcode : " + getErrorCodeString());
      message = getErrorCodeString();
    }

    if (message.startsWith(getErrorCodeString())) {
      return message;
    }

    return getErrorCodeString() + " : " + message;
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
    return (messageArguments == null ? new Object[0] : messageArguments);
  }

  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ErrorCode)) return false;

    final ErrorCode errorCode1 = (ErrorCode) o;

    if (errorCode != null ? !errorCode.equals(errorCode1.errorCode) : errorCode1.errorCode != null) return false;

    return true;
  }

  public int hashCode() {
    return (errorCode != null ? errorCode.hashCode() : 0);
  }

  public String toString() {
    return errorCode;
  }

}

