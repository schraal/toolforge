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
package nl.toolforge.karma.core;


import nl.toolforge.karma.core.location.LocationException;
import nl.toolforge.karma.core.manifest.ManifestException;

import java.text.MessageFormat;

/**
 *
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public class KarmaException extends Exception {

  // TODO : Work trough all errorcodes and come up with a nice setup of codes.
  //

  public static final String EXCEPTION_PREFIX = "KAR-";

  /**
   * Can be used to identify something that is not implemented
   */
  public static final ErrorCode NOT_IMPLEMENTED = new ErrorCode(EXCEPTION_PREFIX + "00000");
  /**
   * Default configuration has been created.
   */
  public static final ErrorCode DEFAULT_CONFIGURATION_CREATED = new ErrorCode(EXCEPTION_PREFIX + "00002");
  /**
   * Vital configuration is missing
   */
  public static final ErrorCode MISSING_CONFIGURATION = new ErrorCode(EXCEPTION_PREFIX + "00001");

  public static final ErrorCode WORKING_CONTEXT_NOT_FOUND = new ErrorCode(EXCEPTION_PREFIX + "00002");;

  /**
   * When the manifest store directory could not be found.
   */
  public static final ErrorCode MANIFEST_STORE_NOT_FOUND = new ErrorCode(ManifestException.EXCEPTION_PREFIX + "00010");
  /**
   * When the location store directory could not be found.
   */
  public static final ErrorCode LOCATION_STORE_NOT_FOUND = new ErrorCode(LocationException.EXCEPTION_PREFIX + "00011");
  /**
   * No development home directory could be referenced to. This is panic, because without it, nothing will work.
   */
  public static final ErrorCode DEVELOPMENT_HOME_NOT_FOUND = new ErrorCode(EXCEPTION_PREFIX + "00012");
  /**
   * When updating the manifest store failed.
   */
  public static final ErrorCode MANIFEST_STORE_UPDATE_FAILED = new ErrorCode(EXCEPTION_PREFIX + "00013");;
  /**
   * When updating the location store failed.
   */
  public static final ErrorCode LOCATION_STORE_UPDATE_FAILED = new ErrorCode(EXCEPTION_PREFIX + "00014");;

  /**
   * The build of a module failed.
   */
  // todo NO_MAVEN_PROJECT_XML should be moved to CommandException ?
  public static final ErrorCode NO_MAVEN_PROJECT_XML = new ErrorCode(EXCEPTION_PREFIX + "00100");

  protected ErrorCode errorCode = null;

  protected Object[] messageArguments = new Object[]{};

  /**
   * Create a new KarmaException, with the specific errorCode.
   *
   * @param errorCode The errorCode that identifies the specific error that has occurred.
   */
  public KarmaException(ErrorCode errorCode) {
    this.errorCode = errorCode;
  }

  /**
   * Create a new KarmaException, with the specific errorCode and messageArguments.
   *
   * @param errorCode        The errorCode that identifies the specific error that has occurred.
   * @param messageArguments These arguments are filled in into the error codes' message.
   */
  public KarmaException(ErrorCode errorCode, Object[] messageArguments) {
    this.errorCode = errorCode;
    this.messageArguments = messageArguments;
  }

  /**
   * Create a new KarmaException, with the specific errorCode and Throwable that caused the exception.
   *
   * @param errorCode The errorCode that identifies the specific error that has occurred.
   * @param t         The Throwable that caused this specific exception.
   */
  public KarmaException(ErrorCode errorCode, Throwable t) {
    super(t);
    this.errorCode = errorCode;
  }

  /**
   * Create a new KarmaException, with the specific errorCode and Throwable that caused the exception.
   *
   * @param errorCode        The errorCode that identifies the specific error that has occurred.
   * @param messageArguments These arguments are filled in into the error codes' message.
   * @param t                The Throwable that caused this specific exception.
   */
  public KarmaException(ErrorCode errorCode, Object[] messageArguments, Throwable t) {
    super(t);
    this.errorCode = errorCode;
    this.messageArguments = messageArguments;
  }

  /**
   * Gets this instance' {@link nl.toolforge.karma.core.ErrorCode}.
   *
   * @return This instance' {@link nl.toolforge.karma.core.ErrorCode} or <code>null</code> if this exception was not initialized with an
   *         <code>ErrorCode</code>.
   */
  public ErrorCode getErrorCode() {
    return errorCode;
  }

  /**
   * A <code>KarmaException</code> can be constructed with a structured error code {@link nl.toolforge.karma.core.ErrorCode}. When this is
   * done, the error message will return {@link nl.toolforge.karma.core.ErrorCode#getErrorMessage} for this exception. If no
   * <code>ErrorCode</code> was used for initialization, the exceptions' {@link #getMessage} is returned, so there is
   * always something to tell the developer or user.
   *
   * @return Return's the <code>ErrorCode</code>s' error message, if the <code>ErrorCode</code> was set, otherwise it
   *         will return <code>Throwable.getMessage()</code>.
   */
  public String getErrorMessage() {
//    if (messageArguments != null && messageArguments.length > 0) {
//      errorCode.setMessageArguments(messageArguments);
//    }
//    return errorCode.getErrorMessage();
    if (getMessageArguments() != null && getMessageArguments().length != 0) {
      MessageFormat messageFormat = new MessageFormat(getErrorCode().getErrorMessage());
      return messageFormat.format(getMessageArguments());
    } else {
      return getErrorCode().getErrorMessage();
    }
  }

  /**
   * @return The arguments that are to be filled in into the error codes' message.
   */
  public final Object[] getMessageArguments() {
    return messageArguments;
  }

  public String getMessage() {
    return getErrorMessage();
  }
}
