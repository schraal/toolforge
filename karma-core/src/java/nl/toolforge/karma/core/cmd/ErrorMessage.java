package nl.toolforge.karma.core.cmd;

import nl.toolforge.karma.core.KarmaException;

/**
 * A CommandMessage that implements an error.
 */
public class ErrorMessage implements CommandMessage {
  private KarmaException exception;

  public ErrorMessage(KarmaException ke) {
    this.exception = ke;
  }

  public String getMessageText() {
    return exception.getErrorMessage();
  }

}
