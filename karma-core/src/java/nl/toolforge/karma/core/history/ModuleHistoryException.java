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
package nl.toolforge.karma.core.history;

import nl.toolforge.karma.core.ErrorCode;

/**
 * Exception thrown by the module history classes in case of and error.
 *
 * @author W.H. Schraal
 */
public class ModuleHistoryException extends Exception {


  private ErrorCode errorCode = null;
  private Object[] messageArguments = null;

  /**
   * This is the prefix that is shown when displaying the error.
   */
  public static final String EXCEPTION_PREFIX = "HIS-";

  /**
   * When <code>history.xml</code> could not be parsed.
   */
  public static final ErrorCode INVALID_HISTORY_FILE = new ErrorCode(EXCEPTION_PREFIX + "00001");

  /**
   * When <code>history.xml</code> does not exist for the module.
   */
  public static final ErrorCode HISTORY_FILE_DOES_NOT_EXIST = new ErrorCode(EXCEPTION_PREFIX + "00002");

  /**
   * When the location of the <code>history.xml</code> file has not been defined.
   */
  public static final ErrorCode HISTORY_FILE_LOCATION_NOT_DEFINED = new ErrorCode(EXCEPTION_PREFIX + "00003");

  public ModuleHistoryException(ErrorCode errorCode) {
    this(errorCode, null);
  }

  public ModuleHistoryException(Throwable t, ErrorCode errorCode) {
    this(t, errorCode, null);
  }

  public ModuleHistoryException(ErrorCode errorCode, Object[] messageArguments) {
    super();
    this.errorCode = errorCode;
    this.messageArguments = messageArguments;
  }

  public ModuleHistoryException(Throwable t, ErrorCode errorCode, Object[] messageArguments) {
    super(t);
    this.errorCode = errorCode;
    this.messageArguments = messageArguments;
  }

  public String getMessage() {
    if (messageArguments != null && messageArguments.length > 0) {
      errorCode.setMessageArguments(messageArguments);
    }
    return errorCode.getErrorMessage();
  }

  /**
   * Gets the exceptions' {@link nl.toolforge.karma.core.ErrorCode}.
   */
  public final ErrorCode getErrorCode() {
    return errorCode;
  }

  /**
   * Retrieves the message arguments for this exception.
   */
  public final Object[] getMessageArguments() {
    return messageArguments;
  }

}
