package nl.toolforge.karma.core.cmd;

import nl.toolforge.karma.core.KarmaException;
import nl.toolforge.karma.core.exception.ErrorCode;

/**
 * Exceptions thrown during the execution of a command.
 *
 * @author D.A. Smedes 
 * 
 * @version $Id:
 */
public class CommandException extends KarmaException {

	/**
	 * Some commands apply to modules in the current manifest. When the command is called with a module that is not
	 * part of the current manifest, this error code is generated.
	 */
	public static final ErrorCode MODULE_NOT_IN_MANIFEST = new ErrorCode("CMD-00010");

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

//  public static ErrorCode

	public CommandException(ErrorCode errorCode) {
		super(errorCode);
	}

	public CommandException(ErrorCode errorCode, Throwable t) {
		super(errorCode, t);
	}

	public CommandException(ErrorCode errorCode, Object[] messageArguments) {
		super(errorCode, messageArguments);
	}

	/**
	 * Create a new KarmaException, with the specific errorCode and Throwable that caused the exception.
	 *
	 * @param errorCode         The errorCode that identifies the specific error that has occurred.
	 * @param messageArguments  These arguments are filled in into the error codes' message.
	 * @param t                 The Throwable that caused this specific exception.
	 */
	public CommandException(ErrorCode errorCode, Object[] messageArguments, Throwable t) {
		super(errorCode, messageArguments, t);
	}
}