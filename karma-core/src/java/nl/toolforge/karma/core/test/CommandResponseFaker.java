package nl.toolforge.karma.core.test;

import nl.toolforge.karma.core.cmd.CommandMessage;
import nl.toolforge.karma.core.cmd.CommandResponse;

/**
 * Pretty empty implementation for a CommandResponse. Usefull for unit tests.
 *
 * @author D.A. Smedes
 * @version $Id:
 */
public class CommandResponseFaker extends CommandResponse {

  /**
   * Overrides {@link CommandResponse#addMessage(CommandMessage)}, and does nothing.
   *
   * @param message
   */
  public void addMessage(CommandMessage message) {}
  
}
