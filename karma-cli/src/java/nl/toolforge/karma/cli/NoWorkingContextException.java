package nl.toolforge.karma.cli;

/**
 * Thrown when <code>CLI</code> users have not yet set the working context for their environment.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public class NoWorkingContextException extends Exception {

  public NoWorkingContextException(String message) {
    super(message);
  }
}
