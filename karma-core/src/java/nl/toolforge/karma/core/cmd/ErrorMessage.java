package nl.toolforge.karma.core.cmd;

import nl.toolforge.karma.core.KarmaException;

/**
 * A <code>CommandMessage</code> that implements an error.
 *
 * @author W.H. Schraal
 * @author D.A. Smedes
 *
 * @version $Id$
 */
public class ErrorMessage extends AbstractCommandMessage {

  private Throwable exception;

  public ErrorMessage(Throwable ke) {
    this(ke, null);
  }

  public ErrorMessage(Throwable ke, Object[] messageParameters) {
    super(ke.getMessage(), messageParameters);
    this.exception = ke;
  }

//  public String getMessageText() {
//    return exception.getErrorMessage();
//  }

}
