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
package nl.toolforge.karma.core.vc.subversion;

import nl.toolforge.karma.core.ErrorCode;
import nl.toolforge.karma.core.vc.VersionControlException;

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