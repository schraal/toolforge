package nl.toolforge.karma.core;

import nl.toolforge.karma.core.ErrorCode;


/**
 * Root runtime exception for <code>nl.toolforge.karma</code> classes. Runtime exceptions support localized messages
 * as well as <code>KarmaException</code>s.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public class KarmaRuntimeException extends RuntimeException {

  protected ErrorCode errorCode = null;

  protected Object[] messageArguments = new Object[]{};

  public KarmaRuntimeException(String message) {
    super(message);
  }

  /**
   *
   * @param message A message for this exception. <code>message</code> is not localized, but stored as-is.
   * @param t Some other exception, preferrably the exception causing this one.
   */
  public KarmaRuntimeException(String message, Throwable t) {
    super(message, t);
  }

  public KarmaRuntimeException(ErrorCode errorCode) {
    this.errorCode = errorCode;
  }

  public KarmaRuntimeException(ErrorCode errorCode, Throwable t) {
    super(t);
    this.errorCode = errorCode;
  }

  public KarmaRuntimeException(ErrorCode errorCode, Object[] messageArguments) {
    this.errorCode = errorCode;
    this.messageArguments = messageArguments;
  }

  public KarmaRuntimeException(ErrorCode errorCode, Object[] messageArguments, Throwable t) {
    super(t);
    this.errorCode = errorCode;
    this.messageArguments = messageArguments;
  }

}
