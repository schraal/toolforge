package nl.toolforge.karma.core.boot;

/**
 * @author D.A. Smedes
 * @version $Id$
 */
public class WorkingContextException extends Exception {

  public WorkingContextException(String message) {
    super(message);
  }

  public WorkingContextException(Throwable cause) {
    super(cause);
  }

  public WorkingContextException(String message, Throwable cause) {
    super(message, cause);
  }
}
