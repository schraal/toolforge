package nl.toolforge.karma.core;

/**
 * <p>Class representing a Karma errorcode. Errorcodes are defined in ranges. Please refer to the online
 * documentation for all error code ranges.
 *
 * @author D.A. Smedes
 */
public final class ErrorCode {

	public static ErrorCode CORE_NOT_IMPLEMENTED = new ErrorCode("CORE-00000");

	//
	//	 CORE error codes
	//

	// Range : 00001 - 00100 --> Manifest loading related errors

	public static ErrorCode CORE_MANIFEST_COULD_NOT_BE_LOADED = new ErrorCode("CORE-00001");
	public static ErrorCode CORE_COMMAND_IMPLEMENTATION_CLASS_NOT_FOUND = new ErrorCode("CORE-00002");

	//
	// CMD error codes
	//

	public static ErrorCode COMMAND_CONTEXT_NOT_INITIALIZED = new ErrorCode("CMD-00001");

	private String errorCode = null;

	private ErrorCode(String errorCode) {
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

