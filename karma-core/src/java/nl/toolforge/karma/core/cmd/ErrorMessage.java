package nl.toolforge.karma.core.cmd;

import nl.toolforge.karma.core.ErrorCode;

/**
 * A <code>CommandMessage</code> that implements an error.
 *
 * @author W.H. Schraal
 * @author D.A. Smedes
 *
 * @version $Id$
 */
public class ErrorMessage extends AbstractCommandMessage {

  public ErrorMessage(ErrorCode code) {
    this(code, null);
  }

  public ErrorMessage(ErrorCode code, Object[] messageParameters) {
    super(code.getErrorMessage(), messageParameters);
  }

//  public String getMessageText() {
//    return exception.getErrorMessage();
//  }

}
