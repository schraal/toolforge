package nl.toolforge.karma.core.exception;

/**
 * <p>Class representing a Karma errorcode. These error codes are localized to support different languages. Errorcodes
 * are defined in ranges. Please refer to the online documentation for all error code ranges.
 *
 * <p>This class should be extended by modules that must implement an
 * errorcoding scheme.
 *
 * @author D.A. Smedes
 *
 * @version
 */
public class ErrorCode {



	private String errorCode = null;

	public ErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	/**
	 * Gets a localized error message for the <code>ErrorCode</code> instance. Error messages are defined in a
	 * <code>error-messages-&lt;locale&gt;.properties</code> (e.g. <code>error-messages-NL.properties</code>).
	 *
	 * @return A localized error message.
	 */
	public String getErrorMessage() {

		// Over here, some resource bundle should be refered to
		//

		return "";
	}

	/**
	 * Gets this instance' error code.
	 *
	 * @return This instance' error code.
	 */
	public String getErrorCode() {
		return errorCode;
	}
}

