package nl.toolforge.karma.core.vc.cvs;

import nl.toolforge.karma.core.ErrorCode;

/**
 * <p>This exception is thrown by the {@link CVSResponseAdapter} when an error message was received from CVS. The listener
 * mechanism that is used by the Netbeans API sends events to a listener. Succesfull commands to CVS are passed to a
 * {@link nl.toolforge.karma.core.cmd.CommandResponseHandler}, handled by Karma, but we want to be able to throw
 * exceptions when errors have occurred. With a <code>RuntimeException</code>, we have the means to do so.
 *
 * <p>There is a little trick required to get to fetch this exception. The Netbeans API fetches all
 * <code>RuntimeException</code>s and re-throws it as a <code>org.netbeans.lib.cvsclient.command.CommandException</code>.
 * The original (runtime-)exception is kept in its <code>getUnderlyingException()</code>-method, which is thus used
 * to determine if we threw a <code>CVSRuntimeException</code>. Works fine ...
 *
 * @author D.A. Smedes
 * @version $Id$
 */
final class CVSRuntimeException extends RuntimeException {

  private ErrorCode errorCode = null;

  /**
   * Constructs a <code>CVSRuntimeException</code>, with a non-<code>null</code> <code>ErrorCode</code>.
   *
   * @param errorCode An <code>ErrorCode</code> instance. Should not be <code>null</code> (or an
   *                  <code>IllegalArgumentException</code> will be thrown.
   */
  public CVSRuntimeException(ErrorCode errorCode) {

    super();

    if (errorCode == null) {
      throw new IllegalArgumentException("Errorcode cannot be null.");
    }
    this.errorCode = errorCode;
  }

  /**
   * Returns the <code>ErrorCode</code> that was used to construct this exception.
   *
   * @return An <code>ErrorCode</code> instance.
   */
  public ErrorCode getErrorCode() {
    return errorCode;
  }
}
