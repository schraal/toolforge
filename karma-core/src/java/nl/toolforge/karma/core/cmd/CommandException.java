package nl.toolforge.karma.core.cmd;

import nl.toolforge.karma.core.KarmaException;
import nl.toolforge.karma.core.ErrorCode;
import nl.toolforge.karma.core.vc.VersionControlException;

/**
 * Exceptions thrown during the execution of a command.
 *
 * @author D.A. Smedes
 * @version $Id:
 */
public class CommandException extends Exception {

  private ErrorCode errorCode = null;
  private Object[] messageArguments = null;

  public static final String EXCEPTION_PREFIX = "CMD-";

  // todo make all errorcodes according to EXCEPTION_PREFIX

  /**
   * Some commands apply to modules in the current manifest. When the command is called with a module that is not
   * part of the current manifest, this error code is generated.
   */
  public static final ErrorCode MODULE_NOT_IN_MANIFEST = new ErrorCode(EXCEPTION_PREFIX + "00010");

  /** The module has no version attribute. */
  public static final ErrorCode MODULE_WITHOUT_VERSION = new ErrorCode(EXCEPTION_PREFIX + "00011");

  /**
   * Used when a duplicate status update is added to a command response.
   *
   * @see CommandResponse#addStatusUpdate
   */
  public static final ErrorCode DUPLICATE_COMMAND_STATUS = new ErrorCode(EXCEPTION_PREFIX + "00020");

  /**
   * The command that is requested by <code>CommandFactory</code> is invalid. The command could
   * not be created.
   *
   * @see CommandFactory#getCommand
   */
  public static final ErrorCode INVALID_COMMAND = new ErrorCode(EXCEPTION_PREFIX + "00030");

  /**
   * A required option (see &lt;required&gt;-attributes for options in <code>commands.xml</code>.
   */
  public static ErrorCode MISSING_OPTION = new ErrorCode(EXCEPTION_PREFIX + "00031");

  /**
   * Argument for a command option is missing.
   */
  public static ErrorCode MISSING_ARGUMENT = new ErrorCode(EXCEPTION_PREFIX + "00032");

  /**
   * Argument for a command option is invalid.
   */
  public static ErrorCode INVALID_ARGUMENT = new ErrorCode(EXCEPTION_PREFIX + "00033");


  /** The build of a module failed. */
  public static final ErrorCode BUILD_FAILED = new ErrorCode(EXCEPTION_PREFIX + "00040");

  /**
   * When the module has a version-attribute and is therefor STATIC. Not allowed to start work on this module.
   */
  public static final ErrorCode START_WORK_NOT_ALLOWED_ON_STATIC_MODULE = new ErrorCode(EXCEPTION_PREFIX + "00041");

  /**
   * When the module is not (a descendant of) <code>SourceModule</code>. Not allowed to start work on this module.
   */
  public static final ErrorCode MODULE_TYPE_MUST_BE_SOURCEMODULE = new ErrorCode(EXCEPTION_PREFIX + "00042");

  /**
   * Promote command is not allowed on static and dynamic modules.
   */
  public static final ErrorCode PROMOTE_ONLY_ALLOWED_ON_WORKING_MODULE = new ErrorCode(EXCEPTION_PREFIX + "00043");

  /**
   * <p>The dependency that is referenced does not exists. This could happen when:
   *
   * <ul>
   *   <li/>the dependency is a sourcemodule dependency and the jar it refers to has not been built.
   *   <li/>the dependency is a jar dependency and the artifact is not available in the Maven repository locally.
   * </ul>
   */
  public static final ErrorCode DEPENDENCY_DOES_NOT_EXIST = new ErrorCode(EXCEPTION_PREFIX + "00044");

  /**
   * <code>src/java</code> (the default directory for a module where java sources are located) is missing.
   */
  public static final ErrorCode NO_SRC_DIR = new ErrorCode(EXCEPTION_PREFIX + "00045");

  public CommandException(ErrorCode errorCode) {
    this(errorCode, null);
  }

  public CommandException(Throwable t, ErrorCode errorCode) {
    this(t, errorCode, null);
  }

  public CommandException(ErrorCode errorCode, Object[] messageArguments) {
    super();
    this.errorCode = errorCode;
    this.messageArguments = messageArguments;
  }

  public CommandException(Throwable t, ErrorCode errorCode, Object[] messageArguments) {
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
    return getErrorCode().getErrorMessage();
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