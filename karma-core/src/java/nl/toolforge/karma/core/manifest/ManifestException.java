package nl.toolforge.karma.core.manifest;

import nl.toolforge.karma.core.ErrorCode;


/**
 * Exception thrown by the AbstractManifest Domain.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public class ManifestException extends Exception {

  private ErrorCode errorCode = null;
  private Object[] messageArguments = null;

  public static final String EXCEPTION_PREFIX = "MAN-";
  /**
   * When a manifest is included with the same name as an already loaded manifest.
   */
  public static final ErrorCode MANIFEST_NAME_RECURSION = new ErrorCode(EXCEPTION_PREFIX + "00001");
  /**
   * When a duplicate module-name is encountered in a manifest.
   */
  public static final ErrorCode DUPLICATE_MODULE = new ErrorCode(EXCEPTION_PREFIX + "00002");
  /**
   * When the manifest file cannot be found on the users' local harddisk.
   */
  public static final ErrorCode MANIFEST_FILE_NOT_FOUND = new ErrorCode(EXCEPTION_PREFIX + "00003");
  /**
   * When a module does not exist in the manifest.
   */
  public static final ErrorCode MODULE_NOT_FOUND = new ErrorCode(EXCEPTION_PREFIX + "00004");
  /**
   * When the manifest could not be loaded from disk.
   */
  public static final ErrorCode MANIFEST_LOAD_ERROR = new ErrorCode(EXCEPTION_PREFIX + "00006");
  /**
   * When the manifest could not be loaded from disk.
   */
  public static final ErrorCode NO_ACTIVE_MANIFEST = new ErrorCode(EXCEPTION_PREFIX + "00007");
  /**
   * When the local path to the manifest on disk is invalid.
   */
  public static final ErrorCode INVALID_LOCAL_PATH = new ErrorCode(EXCEPTION_PREFIX + "00010");;
  /**
   * When the state update for a module failed.
   */
  public static final ErrorCode STATE_UPDATE_FAILURE = new ErrorCode(EXCEPTION_PREFIX + "00011");
  /**
   * When a modules' <code>project.xml</code> cannot be found.
   */
  public static final ErrorCode DEPENDENCY_FILE_NOT_FOUND = new ErrorCode(EXCEPTION_PREFIX + "00013");
	/**
	 * When a module has not yet been checked out for this manifest
	 */
	public static final ErrorCode MODULE_NOT_LOCAL = new ErrorCode(EXCEPTION_PREFIX + "00014");
  /**
   * When the <code>dependencies.xml</code> file could not be loaded correctly.
   */
  public static final ErrorCode DEPENDENCY_FILE_LOAD_ERROR = new ErrorCode(EXCEPTION_PREFIX + "00015");

  public ManifestException(ErrorCode errorCode) {
    this(errorCode, null);
  }

  public ManifestException(Throwable t, ErrorCode errorCode) {
    this(t, errorCode, null);
  }

  public ManifestException(ErrorCode errorCode, Object[] messageArguments) {
    super();
    this.errorCode = errorCode;
    this.messageArguments = messageArguments;
  }

  public ManifestException(Throwable t, ErrorCode errorCode, Object[] messageArguments) {
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
