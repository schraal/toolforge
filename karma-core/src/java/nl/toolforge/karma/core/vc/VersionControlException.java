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

	/** Generated when a branch was created on a module which already existed. */
	public static final ErrorCode DUPLICATE_BRANCH = new ErrorCode("VC-00010");

	public VersionControlException(ErrorCode errorCode) {
		super(errorCode);
	}
}