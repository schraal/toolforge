package nl.toolforge.karma.core.cmd;

import nl.toolforge.karma.core.ErrorCode;

import java.text.MessageFormat;

/**
 * Thrown when command loading failed.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public class CommandLoadException extends Exception {

  private ErrorCode errorCode = null;
  private Object[] messageArguments = null;

  public static final String EXCEPTION_PREFIX = "CML-";

  public static final ErrorCode LOAD_FAILURE_FOR_DEFAULT_COMMANDS = new ErrorCode(EXCEPTION_PREFIX + "00001");

  public static final ErrorCode LOAD_FAILURE_FOR_COMMAND_FILE = new ErrorCode(EXCEPTION_PREFIX + "00002");

  public CommandLoadException(ErrorCode errorCode) {
    this(errorCode, null);
  }

  public CommandLoadException(ErrorCode errorCode, Object[] messageArguments) {
    super();
    this.errorCode = errorCode;
    this.messageArguments = messageArguments;
  }

  public CommandLoadException(Throwable t, ErrorCode errorCode, Object[] messageArguments) {
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
//    if (messageArguments != null && messageArguments.length > 0) {
//      errorCode.setMessageArguments(messageArguments);
//    }
//    return errorCode.getErrorMessage();
//
    if (getMessageArguments() != null && getMessageArguments().length != 0) {
      MessageFormat messageFormat = new MessageFormat(getErrorCode().getErrorMessage());
      return messageFormat.format(getMessageArguments());
    } else {
      return getErrorCode().getErrorMessage();
    }

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
