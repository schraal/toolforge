package nl.toolforge.karma.core.cmd;

import nl.toolforge.karma.core.KarmaException;
import nl.toolforge.karma.core.ErrorCode;

/**
 * Exceptions thrown during the execution of a command.
 *
 * @author D.A. Smedes
 * @version $Id:
 */
public class CommandException extends Exception {

  private ErrorCode errorCode = null;
  private Object[] messageArguments = null;

  public static final String EXCEPTION_PREFIX = "CMD-";

  // todo make all errorcodes according to EXCEPTION_PREFIX

	/**
	 * Some commands apply to modules in the current manifest. When the command is called with a module that is not
	 * part of the current manifest, this error code is generated.
	 */
	public static final ErrorCode MODULE_NOT_IN_MANIFEST = new ErrorCode(EXCEPTION_PREFIX + "00010");

  /** The module has no version attribute. */
  public static final ErrorCode MODULE_WITHOUT_VERSION = new ErrorCode("CMD-00011");

	/**
	 * Used when a duplicate status update is added to a command response.
	 *
	 * @see CommandResponse#addStatusUpdate
	 */
	public static final ErrorCode DUPLICATE_COMMAND_STATUS = new ErrorCode("CMD-00020");

	/**
	 * The command that is requested by <code>CommandFactory</code> is invalid. The command could
	 * not be created.
	 *
	 * @see CommandFactory#getCommand
	 */
	public static final ErrorCode INVALID_COMMAND = new ErrorCode("CMD-00030");

	/**
	 * A required option (see &lt;required&gt;-attributes for options in <code>commands.xml</code>.
	 */
	public static ErrorCode MISSING_OPTION = new ErrorCode("CMD-00031");

	/**
	 * Argument for a command option is missing.
	 */
	public static ErrorCode MISSING_ARGUMENT = new ErrorCode("CMD-00032");

  /** The build of a module failed. */
	public static final ErrorCode BUILD_FAILED = new ErrorCode("CMD-00040");

//  public static ErrorCode


  public CommandException(ErrorCode errorCode) {
    this(errorCode, null);
  }

  public CommandException(Throwable t, ErrorCode errorCode) {
    this(t, errorCode, null);
  }

  public CommandException(ErrorCode errorCode, Object[] messageArguments) {
    super();
    this.errorCode = errorCode;
    this.messageArguments = messageArguments;
  }

  public CommandException(Throwable t, ErrorCode errorCode, Object[] messageArguments) {
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