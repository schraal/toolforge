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
package nl.toolforge.karma.core.location;

import nl.toolforge.karma.core.ErrorCode;

import java.text.MessageFormat;

/**
 * <p>Exceptions relating to <code>Location</code>s. As with all other <code>KarmaExceptions</code>, exceptions can
 * only be thrown with a certain <code>ErrorCode</code>.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public final class LocationException extends Exception {

  private ErrorCode errorCode = null;
  private Object[] messageArguments = null;

  /**
   * This is the prefix that is shown when displaying the error.
   */
  public static final String EXCEPTION_PREFIX = "LOC-";

  /**
   * Location descriptor does not exist. This happens when a module's 'location'-attribute cannot be found by the
   * <code>LocationFactory</code>, which contains references to all <code>Location</code> objects mapped in
   * <code>locations.xml</code>.
   */
  public static final ErrorCode LOCATION_NOT_FOUND = new ErrorCode(EXCEPTION_PREFIX + "00005");
  /**
   * No location files were found. Karma filters all <code>*.xml</code>-files from
   * {@link nl.toolforge.karma.core.boot.WorkingContext#getLocationStore()}.
   */
  public static final ErrorCode NO_LOCATION_DATA_FOUND = new ErrorCode(EXCEPTION_PREFIX + "00006");

  /** Missing location property */
  public static final ErrorCode MISSING_LOCATION_PROPERTY = new ErrorCode(EXCEPTION_PREFIX + "00010");
  /**
   * The manifest-store is configured based on properties in <code>karma.properties</code>. This error code is
   * created when the <code>Location</code> for the manifest-store could not be created succesfully.
   */
  public static final ErrorCode INVALID_MANIFEST_STORE_LOCATION = new ErrorCode(EXCEPTION_PREFIX + "00011");
  /**
   * The location-store is configured based on properties in <code>karma.properties</code>. This error code is
   * created when the <code>Location</code> for the location-store could not be created succesfully.
   */
  public static final ErrorCode INVALID_LOCATION_STORE_LOCATION = new ErrorCode(EXCEPTION_PREFIX + "00012");

  public static final ErrorCode DUPLICATE_LOCATION_KEY = new ErrorCode(EXCEPTION_PREFIX + "00013");

  /**
   * An xml file with locations could not be loaded.
   */
  public static final ErrorCode LOCATION_LOAD_ERROR = new ErrorCode(EXCEPTION_PREFIX + "00017");

  /**
   * A <code>LOCATION_MISMATCH</code> occurs when a module is locally available and an update is requested from
   * another location. A version control system generally gives an error for this.
   */
  public static final ErrorCode LOCATION_MISMATCH = new ErrorCode(EXCEPTION_PREFIX + "00018");

  /**
   * A connection to the location could not be made.
   */
  public static final ErrorCode CONNECTION_EXCEPTION = new ErrorCode(EXCEPTION_PREFIX + "00019");


  /** The location was configured incorrectly. */
//  public static final ErrorCode LOCATION_CONFIGURATION_ERROR = new ErrorCode(EXCEPTION_PREFIX + "00007");

  // Location configuration error codes. Used when the locations xml are misconfigured.
  //

  public static final ErrorCode INVALID_LOCATION_TYPE = new ErrorCode(EXCEPTION_PREFIX + "00020");



  public LocationException(ErrorCode errorCode) {
    this(errorCode, null);
  }

  public LocationException(Throwable t, ErrorCode errorCode) {
    this(t, errorCode, null);
  }

  public LocationException(ErrorCode errorCode, Object[] messageArguments) {
    super();
    this.errorCode = errorCode;
    this.messageArguments = messageArguments;
  }

  public LocationException(Throwable t, ErrorCode errorCode, Object[] messageArguments) {
    super(t);
    this.errorCode = errorCode;
    this.messageArguments = messageArguments;
  }

  /**
   * Helper method to get the localized error message based on the {@link nl.toolforge.karma.core.ErrorCode}.
   */
//  public final String getErrorMessage() {
//    if (messageArguments != null && messageArguments.length > 0) {
//      errorCode.setMessageArguments(messageArguments);
//    }
//    return errorCode.getErrorMessage();
//  }

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
