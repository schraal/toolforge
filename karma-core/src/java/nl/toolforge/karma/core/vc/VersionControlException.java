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
package nl.toolforge.karma.core.vc;

import nl.toolforge.karma.core.ErrorCode;

import java.text.MessageFormat;

/**
 * Root exception for stuff relating to version control system functionality.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public abstract class VersionControlException extends Exception {

  private ErrorCode errorCode = null;
  private Object[] messageArguments = null;

  public static String EXCEPTION_PREFIX = "VER-";
  /**
   * Version already exists for this module.
   */
  public static final ErrorCode DUPLICATE_VERSION = new ErrorCode(EXCEPTION_PREFIX + "00001");
  /**
   * The requested module does not exist in the repository
   */
  public static final ErrorCode MODULE_NOT_IN_REPOSITORY = new ErrorCode(EXCEPTION_PREFIX + "00002");


  public VersionControlException(ErrorCode errorCode) {
    this(errorCode, null);
  }

  public VersionControlException(Throwable t, ErrorCode errorCode) {
    this(t, errorCode, null);
  }

  public VersionControlException(ErrorCode errorCode, Object[] messageArguments) {
    super();
    this.errorCode = errorCode;
    this.messageArguments = messageArguments;
  }
  
  public VersionControlException(Throwable t, ErrorCode errorCode, Object[] messageArguments) {
    super(t);
    this.errorCode = errorCode;
    this.messageArguments = messageArguments;
  }

  /**
   * Helper method to get the localized error message based on the {@link nl.toolforge.karma.core.ErrorCode}.
   *
   * @return
   */
  public final String getErrorMessage() {
//    if (messageArguments != null && messageArguments.length > 0) {
//      errorCode.setMessageArguments(messageArguments);
//    }

    if (getMessageArguments() != null && getMessageArguments().length != 0) {
      MessageFormat messageFormat = new MessageFormat(getErrorCode().getErrorMessage());
      return messageFormat.format(getMessageArguments());
    } else {
      return getErrorCode().getErrorMessage();
    }

//    return getErrorCode().getErrorMessage();
  }

  public String getMessage() {
    return getErrorMessage();
  }

  /**
   * Gets the exceptions' {@link nl.toolforge.karma.core.ErrorCode}.
   * @return
   */
  public final ErrorCode getErrorCode() {
    return errorCode;
  }

  public final Object[] getMessageArguments() {
    return messageArguments;
  }
}