package nl.toolforge.karma.core.cmd;

import java.util.List;
import java.util.ArrayList;

/**
 * Simple implementation of a command message.
 *
 * @author D.A. Smedes 
 * 
 * @version $Id:
 */
public class SimpleCommandMessage implements CommandMessage {

  private String message = null;

  public SimpleCommandMessage(String message) {
    this.message = message;
  }

  /**
   * Must be implemented by the specific implementation.
   *
   * @return
   */
  public String getMessageText() {
    return message;
  }

}