package nl.toolforge.karma.core.cmd.event;

import nl.toolforge.karma.core.cmd.Command;
import nl.toolforge.karma.core.cmd.CommandException;

/**
 * Generated when the command execution failed with a <code>CommandException</code>. This event is - by default -
 * generated when the {@link nl.toolforge.karma.core.cmd.CommandContext#execute(Command)}-method caught a
 * CommandException.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public final class CommandFailedEvent extends ExceptionEvent {

  /**
   * Creates the event for <code>command</code>.
   *
   * @param command The command that has started.
   */
  public CommandFailedEvent(Command command, CommandException exception) {
    super(command, exception);
  }

  /**
   * Returns a SimpleMessage formatted as <code>[ &lt;command-name&gt; ] Started.</code>.
   */
  public Message getEventMessage() {
    return new SimpleMessage(MessageHelper.format(getCommand().getName(), "Command FAILED!"));
  }
}
