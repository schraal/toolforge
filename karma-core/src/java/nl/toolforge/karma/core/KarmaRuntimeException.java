package nl.toolforge.karma.core;

import nl.toolforge.karma.core.exception.ErrorCode;

/**
 * Root runtime exception for <code>nl.toolforge.karma</code> classes.
 *
 * @author D.A. Smedes
 *
 * @version
 */
public class KarmaRuntimeException extends RuntimeException {

	/** When some function is not yet implemented and likely to be forgotten, this one can be thrown to be reminded */
	public static final ErrorCode LAZY_BASTARD = new ErrorCode("RT-00000");

	/** Vital configuration is missing */
	public static final ErrorCode MISSING_CONFIGURATION = new ErrorCode("RT-00001");

	/** The development home directory cannot be created */
	public static final ErrorCode DEVELOPMENT_HOME_CANNOT_BE_CREATED = new ErrorCode("RT-00010");

	/** The configuration home directory cannot be created */
	public static final ErrorCode CONFIG_HOME_CANNOT_BE_CREATED = new ErrorCode("RT-00011");

	/** Manifest cannot only contain certain types of <code>Module</code>s. */
	public static final ErrorCode INVALID_MANIFEST_NAME = new ErrorCode("RT-00004");

	private ErrorCode errorCode = null;

	public KarmaRuntimeException(String message) {
		super(message);
	}

	public KarmaRuntimeException(ErrorCode errorCode) {
		this.errorCode = errorCode;
	}

	public KarmaRuntimeException(ErrorCode errorCode, Throwable t) {
		super(t);
		this.errorCode = errorCode;
	}

	public ErrorCode getErrorCode() {
		return errorCode;
	}
}
