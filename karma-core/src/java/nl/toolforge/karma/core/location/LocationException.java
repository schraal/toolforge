package nl.toolforge.karma.core.location;

import nl.toolforge.karma.core.exception.ErrorCode;
import nl.toolforge.karma.core.KarmaException;

/**
 * <p>Exceptions relating to locations.
 *
 * <p>As with all other <code>KarmaExceptions</code>, exceptions can only be thrown with a certain
 * <code>ErrorCode</code>
 *
 * @author D.A. Smedes
 *
 * @version $Id$
 */
public final class LocationException extends KarmaException {

	/** General error. */
	public static final ErrorCode GENERAL_LOCATION_ERROR = new ErrorCode("LOC-00001");

	/**
	 * Location descriptor does not exist. This happens when a module's 'location'-attribute cannot be found by the
	 * <code>LocationFactory</code>, which contains references to all <code>Location</code> objects mapped in
	 * <code>locations.xml</code>.
	 */
	public static final ErrorCode LOCATION_NOT_FOUND = new ErrorCode("LOC-00005");

	/** No location files were found. A developer should have a directory configured in karma.properties. */
  public static final ErrorCode NO_LOCATION_DATA_FOUND = new ErrorCode("LOC-00006");

	/** The location was configured incorrectly */
	public static final ErrorCode LOCATION_CONFIGURATION_ERROR = new ErrorCode("LOC-00007");

	public LocationException(ErrorCode errorCode) {
		super(errorCode);
	}

	public LocationException(ErrorCode errorCode, Throwable t) {
		super(errorCode, t);
	}

	public LocationException(ErrorCode errorCode, Object[] messageArguments) {
		super(errorCode, messageArguments);
	}

	public LocationException(ErrorCode errorCode, Object[] messageArguments, Throwable t) {
		super(errorCode, messageArguments, t);
	}

}