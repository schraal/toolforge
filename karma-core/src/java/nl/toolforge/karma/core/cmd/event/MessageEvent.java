package nl.toolforge.karma.core.cmd.event;

import nl.toolforge.karma.core.cmd.Command;
import nl.toolforge.karma.core.cmd.CommandException;

/**
 * An event generated as a result of something ordinary. Commands can generate these events, but
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public final class MessageEvent extends CommandResponseEvent {

  private SimpleMessage message = null;

  /**
   * Creates a <code>MessageEvent</code> not linked to any command with a priority of <code>priority</code>.
   *
   * @param command  The command generated this event.
   * @param priority The priority of the event.
   */
  public MessageEvent(Command command, int priority) {
    super(command, priority);
  }

  /**
   * Creates the event for <code>command</code>.
   *
   * @param command  The command generated this event.
   * @param priority The priority of the event.
   * @param message  The message for the event.
   */
  public MessageEvent(Command command, int priority, SimpleMessage message) {
    super(command, priority);
    this.message = message;
  }

  /**
   * Creates the event for <code>command</code>.
   *
   * @param command The command generated this event.
   */
  public MessageEvent(Command command, SimpleMessage message) {
    super(command);
    this.message = message;
  }

  /**
   * Creates a <code>MessageEvent</code> not linked to any command.
   *
   * @param message Some message.
   */
  public MessageEvent(SimpleMessage message) {
    super(null);
    this.message = message;
  }

  /**
   * Returns a <code>SimpleMessage</code> formatted as <code>[ &lt;command-name&gt; ] &lt;message-text&gt;</code> if
   * this message was constructed with a <code>Command</code> object, otherwise it returns the
   * <code>SimpleMessgae</code> as-is.
   *
   * @return A <code>SimpleMessage</code> object optionally prefixed with the <code>Command</code> name.
   */
  public Message getEventMessage() {
    if (getCommand() == null) {
      return message;
    } else {
      return new SimpleMessage(MessageHelper.format(getCommand().getName(), message.getMessageText()));
    }
  }
}
