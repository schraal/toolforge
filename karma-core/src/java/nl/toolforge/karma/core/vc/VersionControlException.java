package nl.toolforge.karma.core.vc;

import nl.toolforge.karma.core.ErrorCode;

/**
 * Root exception for stuff relating to version control system functionality.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public abstract class VersionControlException extends Exception {

  private ErrorCode errorCode = null;
  private Object[] messageArguments = null;

  public static String EXCEPTION_PREFIX = "VER-";
  /**
   * Version already exists for this module.
   */
  public static final ErrorCode DUPLICATE_VERSION = new ErrorCode(EXCEPTION_PREFIX + "00001");
  /**
   * The requested module does not exist in the repository
   */
  public static final ErrorCode MODULE_NOT_IN_REPOSITORY = new ErrorCode(EXCEPTION_PREFIX + "00002");


  public VersionControlException(ErrorCode errorCode) {
    this(errorCode, null);
  }

  public VersionControlException(Throwable t, ErrorCode errorCode) {
    this(t, errorCode, null);
  }

  public VersionControlException(ErrorCode errorCode, Object[] messageArguments) {
    super();
    this.errorCode = errorCode;
    this.messageArguments = messageArguments;
  }

  public VersionControlException(Throwable t, ErrorCode errorCode, Object[] messageArguments) {
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
    return getErrorCode().getErrorMessage();
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