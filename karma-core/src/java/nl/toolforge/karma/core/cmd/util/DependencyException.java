package nl.toolforge.karma.core.cmd.util;

import nl.toolforge.karma.core.ErrorCode;

import java.text.MessageFormat;

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

  /** A dependency on a module has been defined, but the module is not in the manifest */ 
  public static final ErrorCode MODULE_NOT_IN_MANIFEST = new ErrorCode(EXCEPTION_PREFIX + "00004");

  public static final ErrorCode DUPLICATE_ARTIFACT_VERSION = new ErrorCode(EXCEPTION_PREFIX + "00002");
  public static final ErrorCode EAR_DEPENDENCY_NOT_DEFINED = new ErrorCode(EXCEPTION_PREFIX + "00003");

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
//    if (getMessageArguments() != null && getMessageArguments().length != 0) {
//      MessageFormat messageFormat = new MessageFormat(getErrorCode().getErrorMessage());
//      return messageFormat.format(getMessageArguments());
//    } else {
//      return getErrorCode().getErrorMessage();
//    }
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
