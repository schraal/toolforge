package nl.toolforge.karma.core.vc.subversion;

import nl.toolforge.karma.core.exception.ErrorCode;
import nl.toolforge.karma.core.vc.VersionControlException;

/**
 * Exceptions related to CVS stuff.
 *
 * @author D.A. Smedes
 * @version $Id:
 */
public class SVNException extends VersionControlException {


	/**
	 * When no valid <code>CVSROOT</code> could be compiled from <code>CVSLocationImpl</code> instance variables.
	 */
	public static final ErrorCode INVALID_CVSROOT = new ErrorCode("CVS-00010");


	/**
	 * The module already exists in the repository.
	 */
	public static final ErrorCode MODULE_EXISTS_IN_REPOSITORY = new ErrorCode("CVS-00015");

	public SVNException(ErrorCode errorCode) {
		super(errorCode);
	}

	public SVNException(ErrorCode errorCode, Object[] messageArguments) {
		super(errorCode, messageArguments);
	}

	public SVNException(ErrorCode errorCode, Object[] messageArguments, Throwable t) {
		super(errorCode, messageArguments, t);
	}

}