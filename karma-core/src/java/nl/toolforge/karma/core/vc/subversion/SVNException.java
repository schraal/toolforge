package nl.toolforge.karma.core.vc.subversion;

import nl.toolforge.karma.core.vc.VersionControlException;
import nl.toolforge.karma.core.ErrorCode;

/**
 * Exceptions related to CVS stuff.
 *
 * @author D.A. Smedes
 * @version $Id$
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
    super(errorCode);    //To change body of overridden methods use File | Settings | File Templates.
  }

  public SVNException(Throwable t, ErrorCode errorCode) {
    super(t, errorCode);    //To change body of overridden methods use File | Settings | File Templates.
  }

  public SVNException(ErrorCode errorCode, Object[] messageArguments) {
    super(errorCode, messageArguments);    //To change body of overridden methods use File | Settings | File Templates.
  }

  public SVNException(Throwable t, ErrorCode errorCode, Object[] messageArguments) {
    super(t, errorCode, messageArguments);    //To change body of overridden methods use File | Settings | File Templates.
  }
}