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

  /** The command
  public static final ErrorCode INVALID_COMMAND = new ErrorCode("CMD-00021");

  public CommandException(ErrorCode errorCode) {
    super(errorCode);
  }

}