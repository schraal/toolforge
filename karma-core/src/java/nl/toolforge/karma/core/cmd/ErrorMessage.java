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

  private KarmaException exception;

  public ErrorMessage(KarmaException ke) {
    this(ke, null);
  }

  public ErrorMessage(KarmaException ke, Object[] messageParameters) {
    super(ke.getMessage(), messageParameters);
    this.exception = ke;
  }

//  public String getMessageText() {
//    return exception.getErrorMessage();
//  }

}
