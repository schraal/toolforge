package nl.toolforge.karma.core.cmd;

import nl.toolforge.karma.core.KarmaException;
import nl.toolforge.karma.core.exception.ErrorCode;

/**
 *
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
}