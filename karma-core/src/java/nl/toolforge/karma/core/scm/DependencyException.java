package nl.toolforge.karma.core.scm;

import nl.toolforge.karma.core.KarmaException;
import nl.toolforge.karma.core.exception.ErrorCode;

/**
 * Exception class used when determining the dependency-graphs for the build process.
 */
public class DependencyException extends KarmaException {

  /**
   * When two dependencies are encountered within the module or manifest context from the same family (for example:
   * <code>log4j</code>), with two different version numbers (for example <code>log4j-1.2.7</code> and
   * <code>log4j-1.2.8</code>).
   */
  public static final ErrorCode DEPENDENCY_VERSION_CONFLICT = new ErrorCode("DEP-00001");

  public DependencyException(ErrorCode errorCode) {
    super(errorCode);
  }

  public DependencyException(ErrorCode errorCode, Object[] messageArguments) {
    super(errorCode, messageArguments);
  }

  public DependencyException(ErrorCode errorCode, Throwable t) {
    super(errorCode, t);
  }

  public DependencyException(ErrorCode errorCode, Object[] messageArguments, Throwable t) {
    super(errorCode, messageArguments, t);
  }

}
