package nl.toolforge.karma.core.vc;

import nl.toolforge.karma.core.KarmaException;
import nl.toolforge.karma.core.exception.ErrorCode;

/**
 * Root exception for stuff relating to version control system functionality.
 *
 * @author D.A. Smedes 
 * 
 * @version $Id:
 */
public abstract class VersionControlException extends KarmaException {

	public static final ErrorCode BLA = new ErrorCode("VC-00001");
}