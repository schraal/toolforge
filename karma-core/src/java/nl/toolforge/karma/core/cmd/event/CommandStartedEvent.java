package nl.toolforge.karma.core.cmd.event;

import nl.toolforge.karma.core.cmd.Command;

/**
 * Generated before a command is executed. This event has the default priority {@link CommandResponseEvent#LEVEL_DEBUG}.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public final class CommandStartedEvent extends CommandResponseEvent {

  private long start = 0L;

  /**
   * Creates the event for <code>command</code>.
   *
   * @param command The command that has started.
   */
  public CommandStartedEvent(Command command) {
    super(command, LEVEL_DEBUG);
    start = System.currentTimeMillis();;
  }

  /**
   * Returns the time this event was generated, effectively, the time the command was started.
   *
   * @return Start time of the command.
   */
  public long getTime() {
    return start;
  }

  /**
   * Returns a SimpleMessage formatted as <code>[ &lt;command-name&gt; ] Started.</code>.
   */
  public Message getEventMessage() {
    return new SimpleMessage(MessageHelper.format(getCommand().getName(), "Started."));
  }
}
