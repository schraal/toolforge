package nl.toolforge.karma.core.cmd;

import java.util.List;
import java.util.ArrayList;

/**
 *
 *
 * @author D.A. Smedes 
 * 
 * @version $Id:
 */
public abstract class DefaultCommandMessage implements CommandMessage {

  private String message = null;

  public DefaultCommandMessage(String message) {
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