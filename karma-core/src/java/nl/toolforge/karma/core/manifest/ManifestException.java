package nl.toolforge.karma.core.manifest;

import nl.toolforge.karma.core.ErrorCode;

import java.util.Locale;


/**
 * Exception thrown by the Manifest Domain.
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
   * When the manifest could not be written to disk (mainly IO).
   */
  public static final ErrorCode MANIFEST_FLUSH_ERROR = new ErrorCode(EXCEPTION_PREFIX + "00005");
  /**
   * When the manifest could not be loaded from disk.
   */
  public static final ErrorCode MANIFEST_LOAD_ERROR = new ErrorCode(EXCEPTION_PREFIX + "00006");
  /**
   * When the manifest could not be loaded from disk.
   */
  public static final ErrorCode NO_ACTIVE_MANIFEST = new ErrorCode(EXCEPTION_PREFIX + "00007");
  /**
   * When the manifest store directory could not be found.
   */
  public static final ErrorCode MANIFEST_STORE_NOT_FOUND = new ErrorCode(EXCEPTION_PREFIX + "00008");
  /**
   * When the manifest has not yet been checked out to the local environment
   */
  public static final ErrorCode MANIFEST_NOT_UPDATED = new ErrorCode(EXCEPTION_PREFIX + "00009");
  /**
   * When the local path to the manifest on disk is invalid.
   */
  public static final ErrorCode INVALID_LOCAL_PATH = new ErrorCode(EXCEPTION_PREFIX + "00010");;
  /**
   * When the state update for a module failed.
   */
  public static final ErrorCode STATE_UPDATE_FAILURE = new ErrorCode(EXCEPTION_PREFIX + "00011");
  /**
   * When the state of module within a manifest contains > 1 state files.
   */
  public static final ErrorCode TOO_MANY_STATE_FILES = new ErrorCode(EXCEPTION_PREFIX + "00012");
  /**
   * When the state of module could not be determined correctly.
   */
  public static final ErrorCode INVALID_STATE = new ErrorCode(EXCEPTION_PREFIX + "00012");
  /**
   * When a modules' <code>project.xml</code> cannot be found.
   */
  public static final ErrorCode DEPENDENCY_FILE_NOT_FOUND = new ErrorCode(EXCEPTION_PREFIX + "00013");

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
