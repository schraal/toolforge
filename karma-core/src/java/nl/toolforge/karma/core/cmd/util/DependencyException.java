package nl.toolforge.karma.core.cmd.util;

import nl.toolforge.karma.core.ErrorCode;

/**
 * Thrown when
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public class DependencyException extends Exception {

  private ErrorCode errorCode = null;
  private Object[] messageArguments = null;

  public static final String EXCEPTION_PREFIX = "DEP-";

  /**
   * A dependency is configured, but cannot be found on a developers machine.
   */
  public static final ErrorCode DEPENDENCY_NOT_FOUND = new ErrorCode(EXCEPTION_PREFIX + "00001");

  public static final ErrorCode DUPLICATE_ARTIFACT_VERSION = new ErrorCode(EXCEPTION_PREFIX + "00002");

  public DependencyException(ErrorCode errorCode) {
    this(errorCode, null);
  }

  public DependencyException(ErrorCode errorCode, Object[] messageArguments) {
    super();
    this.errorCode = errorCode;
    this.messageArguments = messageArguments;
  }

  public DependencyException(Throwable t, ErrorCode errorCode, Object[] messageArguments) {
    super(t);
    this.errorCode = errorCode;
    this.messageArguments = messageArguments;
  }

  /**
   * Helper method to get the localized error message based on the {@link nl.toolforge.karma.core.ErrorCode}.
   *
   * @return
   */
  public final String getErrorMessage() {
    if (messageArguments != null && messageArguments.length > 0) {
      errorCode.setMessageArguments(messageArguments);
    }
    return errorCode.getErrorMessage();
  }

  public String getMessage() {
    return getErrorMessage();
  }

  /**
   * Gets the exceptions' {@link nl.toolforge.karma.core.ErrorCode}.
   * @return
   */
  public final ErrorCode getErrorCode() {
    return errorCode;
  }

  public final Object[] getMessageArguments() {
    return messageArguments;
  }



}
