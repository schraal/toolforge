/*
Karma core - Core of the Karma application
Copyright (C) 2004  Toolforge <www.toolforge.nl>

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/
package nl.toolforge.karma.core.vc.cvs;

import nl.toolforge.karma.core.ErrorCode;
import nl.toolforge.karma.core.vc.VersionControlException;

/**
 * Exceptions related to CVS stuff.
 *
 * @author D.A. Smedes
 * @version $Id:
 */
public class CVSException extends VersionControlException {

   public static String EXCEPTION_PREFIX = "CVS-";

	/**
	 * When no valid <code>CVSROOT</code> could be compiled from <code>CVSLocationImpl</code> instance variables.
	 */
	public static final ErrorCode INVALID_CVSROOT = new ErrorCode(EXCEPTION_PREFIX + "00010");

	/**
	 * Authentication against a CVS repository failed.
	 */
	public static final ErrorCode AUTHENTICATION_ERROR = new ErrorCode(EXCEPTION_PREFIX + "00011");

	/**
	 * The module already exists in the repository.
	 */
	public static final ErrorCode MODULE_EXISTS_IN_REPOSITORY = new ErrorCode(EXCEPTION_PREFIX + "00015"); // todo : to superclass ?

	/**
	 * The file that is added to a repository already exists
	 */
	public static final ErrorCode FILE_EXISTS_IN_REPOSITORY = new ErrorCode(EXCEPTION_PREFIX + "00017"); // todo : to superclass ?

	/**
	 * Version not found for the module. This error occurs when no symbolic name exists for the module that identifies
	 * the version.
	 */
	public static final ErrorCode VERSION_NOT_FOUND = new ErrorCode(EXCEPTION_PREFIX + "00018"); // todo : to superclass ?

	/**
	 * Symbolic name rejected by CVS
	 */
	public static final ErrorCode INVALID_SYMBOLIC_NAME = new ErrorCode(EXCEPTION_PREFIX + "00019");

	/**
	 * Wrapper around <code>org.netbeans.lib.cvsclient.command.CommandException</code>. Can occur when processing the
	 * response from CVS or when the command was aborted.
	 */
	public static final ErrorCode INTERNAL_ERROR = new ErrorCode(EXCEPTION_PREFIX + "00020");
  /**
   * When something is wrong with the local copy of a module.
   */
  public static final ErrorCode LOCAL_MODULE_ERROR =  new ErrorCode(EXCEPTION_PREFIX + "00021");

  /**
   * When the cvs reports a security violation. Generally caused by the fact that the user has to write access to the
   */ 
  public static final ErrorCode SECURITY_VIOLATION =  new ErrorCode(EXCEPTION_PREFIX + "00022");

  public CVSException(ErrorCode errorCode) {
    super(errorCode);
  }

  public CVSException(Throwable t, ErrorCode errorCode) {
    super(t, errorCode);
  }

  public CVSException(ErrorCode errorCode, Object[] messageArguments) {
    super(errorCode, messageArguments);
  }

  public CVSException(Throwable t, ErrorCode errorCode, Object[] messageArguments) {
    super(t, errorCode, messageArguments);
  }

}