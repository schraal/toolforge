package nl.toolforge.karma.core.vc;

import nl.toolforge.karma.core.KarmaException;
import nl.toolforge.karma.core.exception.ErrorCode;

/**
 * Root exception for stuff relating to version control system functionality.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public abstract class VersionControlException extends KarmaException {

	/**
	 * A runner instance cannot be created to execute commands on a repository.
	 */
	public static final ErrorCode RUNNER_ERROR = new ErrorCode("VC-00001");

	/**
	 * Generated when a branch was created on a module which already existed.
	 */
	public static final ErrorCode DUPLICATE_BRANCH = new ErrorCode("VC-00010");
	/**
	 * Version already exists for this module.
	 */
	public static final ErrorCode DUPLICATE_VERSION = new ErrorCode("VC-00011");

	/**
	 * The requested file does not exist in the repository
	 */
	public static final ErrorCode FILE_NOT_FOUND = new ErrorCode("VC-00012");

	public VersionControlException(ErrorCode errorCode) {
		super(errorCode);
	}

	public VersionControlException(ErrorCode errorCode, Throwable t) {
		super(errorCode, t);
	}

	public VersionControlException(ErrorCode errorCode, Object[] messageArguments) {
		super(errorCode, messageArguments);
	}

	public VersionControlException(ErrorCode errorCode, Object[] messageArguments, Throwable t) {
		super(errorCode, messageArguments, t);
	}
}