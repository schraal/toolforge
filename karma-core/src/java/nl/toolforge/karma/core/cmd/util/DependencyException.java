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
package nl.toolforge.karma.core.cmd.util;

import nl.toolforge.karma.core.ErrorCode;

/**
 * Thrown when
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public class DependencyException extends Exception {

  private ErrorCode errorCode = null;
  private Object[] messageArguments = null;

  public static final String EXCEPTION_PREFIX = "DEP-";

  /**
   * A dependency is configured, but cannot be found on a developers machine.
   */
  public static final ErrorCode DEPENDENCY_NOT_FOUND = new ErrorCode(EXCEPTION_PREFIX + "00001");

  /** A dependency on a module has been defined, but the module is not in the manifest */ 
  public static final ErrorCode MODULE_NOT_IN_MANIFEST     = new ErrorCode(EXCEPTION_PREFIX + "00004");

  public static final ErrorCode DUPLICATE_ARTIFACT_VERSION = new ErrorCode(EXCEPTION_PREFIX + "00002");
  
  public static final ErrorCode EAR_DEPENDENCY_NOT_DEFINED = new ErrorCode(EXCEPTION_PREFIX + "00003");
  public static final ErrorCode EAR_DEPENDENCY_NOT_FOUND   = new ErrorCode(EXCEPTION_PREFIX + "00005");
  
  /** A circualr dependency has been detected. */
  public static final ErrorCode CIRCULAR_DEPENDENCY        = new ErrorCode(EXCEPTION_PREFIX + "00006");
  

  public DependencyException(ErrorCode errorCode) {
    this(errorCode, null);
  }

  public DependencyException(ErrorCode errorCode, Object[] messageArguments) {
    super();
    this.errorCode = errorCode;
    this.messageArguments = messageArguments;
  }

  public DependencyException(Throwable t, ErrorCode errorCode, Object[] messageArguments) {
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
   * @return
   */
  public final ErrorCode getErrorCode() {
    return errorCode;
  }

  public final Object[] getMessageArguments() {
    return messageArguments;
  }



}
