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
package nl.toolforge.karma.core.cmd;

import nl.toolforge.karma.core.ErrorCode;

import java.text.MessageFormat;

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

  /** Not to be used for business logic. Needed for testing. */
  public static final ErrorCode TEST_CODE = new ErrorCode(EXCEPTION_PREFIX + "00000");

  public static final ErrorCode NO_IMPLEMENTING_CLASS = new ErrorCode(EXCEPTION_PREFIX + "00001");

  public static final ErrorCode UNKNOWN_COMMAND = new ErrorCode(EXCEPTION_PREFIX + "00029");

  /**
   * The command that is requested from <code>CommandFactory.getCommand()</code> is invalid. The command could
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

  /**
   * Invalid option.
   */
  public static final ErrorCode INVALID_OPTION = new ErrorCode(EXCEPTION_PREFIX + "00034");


  /** The build of a module failed. */
  public static final ErrorCode BUILD_FAILED = new ErrorCode(EXCEPTION_PREFIX + "00040");

  /** The build of a module failed. */
  public static final ErrorCode BUILD_FAILED_TOO_MANY_MISSING_DEPENDENCIES = new ErrorCode(EXCEPTION_PREFIX + "00046");

  /** The test of a module failed. */
  public static final ErrorCode TEST_FAILED = new ErrorCode(EXCEPTION_PREFIX + "00050");

  /** The test of a module failed. */
  public static final ErrorCode CLEAN_MODULE_FAILED = new ErrorCode(EXCEPTION_PREFIX + "00060");

  /** The test of a module failed. */
  public static final ErrorCode CLEAN_ALL_FAILED = new ErrorCode(EXCEPTION_PREFIX + "00063");

  /** The packaging of a module failed. */
  public static final ErrorCode PACKAGE_FAILED = new ErrorCode(EXCEPTION_PREFIX + "00070");

  /** The packaging of a module failed. */
  public static final ErrorCode PACKAGE_FAILED_NO_APPLICATION_XML = new ErrorCode(EXCEPTION_PREFIX + "00071");

  /** The packaging of a module failed. */
  public static final ErrorCode PACKAGE_FAILED_INVALID_APPLICATION_XML = new ErrorCode(EXCEPTION_PREFIX + "00072");


  /**
   * When the manifest is a development manifest and the module is STATIC. Not allowed to start work on this module.
   */
  public static final ErrorCode START_WORK_NOT_ALLOWED_ON_STATIC_MODULE = new ErrorCode(EXCEPTION_PREFIX + "00041");

  /**
   * When the module is not (a descendant of) <code>BaseModule</code>. Not allowed to start work on this module.
   */
  public static final ErrorCode MODULE_TYPE_MUST_BE_BASEMODULE = new ErrorCode(EXCEPTION_PREFIX + "00042");

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
  public static final ErrorCode DEPENDENCY_FILE_INVALID = new ErrorCode(EXCEPTION_PREFIX + "00047");

  /**
   * <code>src/java</code> (the default directory for a module where java sources are located) is missing.
   */
  public static final ErrorCode NO_SRC_DIR = new ErrorCode(EXCEPTION_PREFIX + "00045");

  /**
   * <code>test/java</code> (the default directory for a module where test java sources are located) is missing.
   */
  public static final ErrorCode NO_TEST_DIR = new ErrorCode(EXCEPTION_PREFIX + "00051");

  public static final ErrorCode UNCOMMITTED_NEW_FILES = new ErrorCode(EXCEPTION_PREFIX + "00055");

  public static final ErrorCode UNCOMMITTED_CHANGED_FILES = new ErrorCode(EXCEPTION_PREFIX + "00056");

  public static final ErrorCode UNCOMMITTED_REMOVED_FILES = new ErrorCode(EXCEPTION_PREFIX + "00057");

  /**
   * <code>build/&lt;module&gt;</code> (the default directory for a module where built files are located) is missing.
   */
  public static final ErrorCode NO_MODULE_BUILD_DIR = new ErrorCode(EXCEPTION_PREFIX + "00061");

  /**
   * <code>build</code> (the default directory where built files are located) is missing.
   */
  public static final ErrorCode NO_BUILD_DIR = new ErrorCode(EXCEPTION_PREFIX + "00062");

  public static final ErrorCode MODULE_VERSION_ERROR = new ErrorCode(EXCEPTION_PREFIX + "00064");

  public static final ErrorCode TEST_WARNING = new ErrorCode(EXCEPTION_PREFIX + "00100");

  public static final ErrorCode BUILD_WARNING = new ErrorCode(EXCEPTION_PREFIX + "00101");

  /**
   * Thrown when a module is not working and the user wants to stop work on it.
   */
  public static final ErrorCode INVALID_STATE_MODULE_NOT_WORKING = new ErrorCode(EXCEPTION_PREFIX + "00102");

  public static final ErrorCode MODULE_NOT_LOCAL = new ErrorCode(EXCEPTION_PREFIX + "00103");

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

//    if (getMessageArguments() != null && getMessageArguments().length != 0) {
//      MessageFormat messageFormat = new MessageFormat(getErrorCode().getErrorMessage());
//      return messageFormat.format(getMessageArguments());
//    } else {
//      return getErrorCode().getErrorMessage();
//    }
    return getErrorCode().getErrorMessage();
  }

  public String getMessage() {
    return getErrorMessage();
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