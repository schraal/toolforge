package nl.toolforge.karma.core.cmd;

import nl.toolforge.karma.core.KarmaException;

/**
 * A CommandMessage that implements a success.
 */
public class SuccessMessage implements CommandMessage {
  //todo make this a localized message
  private String message;

  public SuccessMessage(String message) {
    this.message = message;
  }

  public String getMessageText() {
    return message;
  }

}
