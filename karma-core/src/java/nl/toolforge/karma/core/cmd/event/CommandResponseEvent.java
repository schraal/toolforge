package nl.toolforge.karma.core.cmd.event;

/**
 * Event thrown when a CommandResponse changes. Classes interested in this event implement
 * the CommandResponseListener interface.
 */
public class CommandResponseEvent {

  String eventMessage = null;

  public CommandResponseEvent(String eventMessage) {
    this.eventMessage = eventMessage;
  }

  public String getEventMessage() {
    return eventMessage;
  }
}
