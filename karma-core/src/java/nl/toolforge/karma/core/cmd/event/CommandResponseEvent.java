package nl.toolforge.karma.core.cmd.event;

import nl.toolforge.karma.core.cmd.CommandMessage;

/**
 * Event thrown when a CommandResponse changes. Classes interested in this event implement
 * the CommandResponseListener interface.
 */
public class CommandResponseEvent {

  CommandMessage eventMessage = null;

  public CommandResponseEvent(CommandMessage eventMessage) {
    this.eventMessage = eventMessage;
  }

  public CommandMessage getEventMessage() {
    return eventMessage;
  }
}
