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
package nl.toolforge.karma.core.manifest;

import nl.toolforge.karma.core.ErrorCode;

import java.text.MessageFormat;


/**
 * Exception thrown by the AbstractManifest Domain.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public class ModuleTypeException extends Exception {

  private ErrorCode errorCode = null;
  private Object[] messageArguments = null;

  public static final String EXCEPTION_PREFIX = "MOD-";

  public static final ErrorCode MISSING_MODULE_DESCRIPTOR = new ErrorCode(EXCEPTION_PREFIX + "00001");

  public ModuleTypeException(ErrorCode errorCode) {
    this(errorCode, null);
  }

  public ModuleTypeException(ErrorCode errorCode, Object[] messageArguments) {
    super();
    this.errorCode = errorCode;
    this.messageArguments = messageArguments;
  }

  public ModuleTypeException(Throwable t, ErrorCode errorCode, Object[] messageArguments) {
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
    if (getMessageArguments() != null && getMessageArguments().length != 0) {
      MessageFormat messageFormat = new MessageFormat(getErrorCode().getErrorMessage());
      return messageFormat.format(getMessageArguments());
    } else {
      return getErrorCode().getErrorMessage();
    }
//    if (messageArguments != null && messageArguments.length > 0) {
//      errorCode.setMessageArguments(messageArguments);
//    }
//    return errorCode.getErrorMessage();
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
