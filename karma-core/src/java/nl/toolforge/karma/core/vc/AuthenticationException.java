package nl.toolforge.karma.core.vc;

import nl.toolforge.karma.core.ErrorCode;
import nl.toolforge.karma.core.location.LocationException;

/**
 * @author D.A. Smedes
 * @version $Id$
 */
public class AuthenticationException extends Exception {

  private ErrorCode errorCode = null;
  private Object[] messageArguments = null;

  /**
   * This is the prefix that is shown when displaying the error.
   */
  public static final String EXCEPTION_PREFIX = "AUT-";
  /**
   * When the authenticator configuration is invalid. This is due to functional errors in the content of the
   * <code>&lt;&authenticator&gt;</code>-element. Some locations require certain data to be available (username and
   * password for a <code>pserver</code> location for instance).
   */
  public static final ErrorCode INVALID_AUTHENTICATOR_CONFIGURATION = new ErrorCode(EXCEPTION_PREFIX + "00001");
  /**
   * When the location-type requires authentication configuration to be present.
   */
  public static final ErrorCode MISSING_AUTHENTICATOR_CONFIGURATION = new ErrorCode(EXCEPTION_PREFIX + "00002");
  /**
   * No authenticator entry is configured that matches the location alias (the <code>id</code>-attribute).
   */
  public static final ErrorCode DUPLICATE_AUTHENTICATOR_KEY = new ErrorCode(EXCEPTION_PREFIX + "00003");
  /**
   * When the <code>authenticators.xml</code> file could not be read properly.
   */
  public static final ErrorCode AUTHENTICATOR_LOAD_ERROR = new ErrorCode(EXCEPTION_PREFIX + "00004");
  /**
   * No authenticator entry is configured that matches the location alias (the <code>id</code>-attribute).
   */
  public static final ErrorCode AUTHENTICATOR_NOT_FOUND = new ErrorCode(EXCEPTION_PREFIX + "00005");
  /** Could not write <code>authenticators.xml</code> */
  public static final ErrorCode AUTHENTICATOR_WRITE_ERROR = new ErrorCode(EXCEPTION_PREFIX + "00006");
  /** Username is missing while configuring a new authenticator. */
  public static final ErrorCode MISSING_USERNAME = new ErrorCode(EXCEPTION_PREFIX + "00007");

  public AuthenticationException(ErrorCode errorCode) {
    this(errorCode, null);
  }

  public AuthenticationException(Throwable t, ErrorCode errorCode) {
    this(t, errorCode, null);
  }

  public AuthenticationException(ErrorCode errorCode, Object[] messageArguments) {
    super();
    this.errorCode = errorCode;
    this.messageArguments = messageArguments;
  }

  public AuthenticationException(Throwable t, ErrorCode errorCode, Object[] messageArguments) {
    super(t);
    this.errorCode = errorCode;
    this.messageArguments = messageArguments;
  }

  public String getMessage() {
    if (messageArguments != null && messageArguments.length > 0) {
      errorCode.setMessageArguments(messageArguments);
    }
    return errorCode.getErrorMessage();
  }

  /**
   * Gets the exceptions' {@link nl.toolforge.karma.core.ErrorCode}.
   */
  public final ErrorCode getErrorCode() {
    return errorCode;
  }

  /**
   * Retrieves the message arguments for this exception.
   */
  public final Object[] getMessageArguments() {
    return messageArguments;
  }
}
