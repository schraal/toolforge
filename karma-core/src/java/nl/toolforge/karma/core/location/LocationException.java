package nl.toolforge.karma.core.location;

import nl.toolforge.karma.core.ErrorCode;

/**
 * <p>Exceptions relating to locations. As with all other <code>KarmaExceptions</code>, exceptions can only be thrown
 * with a certain <code>ErrorCode</code>
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public final class LocationException extends Exception {

  private ErrorCode errorCode = null;
  private Object[] messageArguments = null;

  public static final String EXCEPTION_PREFIX = "LOC-";

  /**
   * When the authentication configuration is invalid.
   */
  public static final ErrorCode INVALID_AUTHENTICATOR_CONFIGURATION = new ErrorCode(EXCEPTION_PREFIX + "00001");
  /**
   * When the location-type requires authentication configuration to be present.
   */
  public static final ErrorCode MISSING_AUTHENTICATOR_CONFIGURATION = new ErrorCode(EXCEPTION_PREFIX + "00002");
  /**
   * Location descriptor does not exist. This happens when a module's 'location'-attribute cannot be found by the
   * <code>LocationFactory</code>, which contains references to all <code>Location</code> objects mapped in
   * <code>locations.xml</code>.
   */
  public static final ErrorCode LOCATION_NOT_FOUND = new ErrorCode(EXCEPTION_PREFIX + "00005");
  /**
   * No location files were found. A developer should have a directory configured in karma.properties, and location
   * data should be available.
   */
  public static final ErrorCode NO_LOCATION_DATA_FOUND = new ErrorCode(EXCEPTION_PREFIX + "00006");
  /** The location was configured incorrectly. */
  public static final ErrorCode LOCATION_CONFIGURATION_ERROR = new ErrorCode(EXCEPTION_PREFIX + "00007");
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

  public static final ErrorCode DUPLICATE_AUTHENTICATOR_KEY = new ErrorCode(EXCEPTION_PREFIX + "00014");

  public static final ErrorCode AUTHENTICATOR_LOAD_ERROR = new ErrorCode(EXCEPTION_PREFIX + "00015");;

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
   *
   * @return
   */
  public final String getErrorMessage() {
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
