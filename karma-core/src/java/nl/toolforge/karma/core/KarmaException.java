package nl.toolforge.karma.core;

/**
 * Root exception for <code>nl.toolforge.karma</code> classes. A <code>KarmaException</code> can be initialized with a
 * structured error code {@link ErrorCode} or the good old way (a <code>String</code>). The first method supports
 * localized error messages, which is usefull when building a localized Karma client.
 *
 * @author D.A. Smedes
 */
public class KarmaException extends Exception {

	private ErrorCode errorCode = null;

	public KarmaException(String message) {
		super(message);
	}

	public KarmaException(ErrorCode errorCode) {
		this.errorCode = errorCode;
	}

	/**
	 * Gets this instance' {@link ErrorCode}.
	 *
	 * @return This instance' {@link ErrorCode} or <code>null</code> if this exception was not initialized with an
	 *         <code>ErrorCode</code>.
	 */
	public ErrorCode getErrorCode() {
		return errorCode;
	}

	/**
	 * A <code>KarmaException</code> can be constructed with a structured error code {@link ErrorCode}. When this is
	 * done, the error message will return {@link ErrorCode#getErrorMessage} for this exception. If no
	 * <code>ErrorCode</code> was used for initialization, the exceptions' {@link #getMessage} is returned, so there is
	 * always something to tell the developer or user.
	 *
	 * @return Return's the <code>ErrorCode</code>s' error message, if the <code>ErrorCode</code> was set, otherwise it
	 *         will return <code>Throwable.getMessage()</code>.
	 */
	public String getErrorMessage() {

		if (errorCode == null) {
			return getMessage();
		} else {
			return errorCode.getErrorMessage();
		}
	}
}
