package nl.toolforge.karma.core.vc.cvs;

import nl.toolforge.karma.core.exception.ErrorCode;
import nl.toolforge.karma.core.vc.VersionControlException;

/**
 * Exceptions related to CVS stuff.
 *
 * @author D.A. Smedes 
 * 
 * @version $Id:
 */
public class CVSException extends VersionControlException {

	/** When no valid <code>CVSROOT</code> could be compiled from <code>CVSLocationImpl</code> instance variables. */
	public static final ErrorCode INVALID_CVSROOT = new ErrorCode("CVS-00010");

	/** Authentication against a CVS repository failed. */
	public static final ErrorCode AUTHENTICATION_ERROR = new ErrorCode("CVS-00011");

	/** The module already exists in the repository. */
	public static final ErrorCode MODULE_EXISTS_IN_REPOSITORY = new ErrorCode("CVS-00015"); // todo : to superclass ?

	/** The module does not exist in the repository */
  public static final ErrorCode NO_SUCH_MODULE_IN_REPOSITORY = new ErrorCode("CVS-00016"); // todo : to superclass ?

	/** The file that is added to a repository already exists */
	public static final ErrorCode FILE_EXISTS_IN_REPOSITORY = new ErrorCode("CVS-00017"); // todo : to superclass ?

	/**
	 * Version not found for the module. This error occurs when no symbolic name exists for the module that identifies
	 * the version.
	 */
	public static final ErrorCode VERSION_NOT_FOUND = new ErrorCode("CVS-00018"); // todo : to superclass ?

	/** Symbolic name rejected by CVS */
	public static final ErrorCode INVALID_SYMBOLIC_NAME = new ErrorCode("CVS-00019");

	/**
	 * Wrapper around <code>org.netbeans.lib.cvsclient.command.CommandException</code>. Can occur when processing the
	 * response from CVS or when the command was aborted.
	 */
	public static final ErrorCode INTERNAL_ERROR = new ErrorCode("CVS-00020");

	public CVSException(ErrorCode errorCode) {
		super(errorCode);
	}

	public CVSException(ErrorCode errorCode, Throwable t) {
		super(errorCode, t);
	}

	public CVSException(ErrorCode errorCode, Object[] messageArguments) {
		super(errorCode, messageArguments);
	}

	public CVSException(ErrorCode errorCode, Object[] messageArguments, Throwable t) {
		super(errorCode, messageArguments, t);
	}

}