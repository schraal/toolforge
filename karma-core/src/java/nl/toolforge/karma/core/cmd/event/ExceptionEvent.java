package nl.toolforge.karma.core.cmd.event;

import nl.toolforge.karma.core.cmd.Command;
import nl.toolforge.karma.core.cmd.CommandException;

/**
 * Generated when an exception is thrown during command execution that should be sent to listeners.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public class ExceptionEvent extends CommandResponseEvent {

  private Throwable exception = null;

  /**
   * Creates the event for <code>command</code>.
   *
   * @param command The command that has started.
   */
  public ExceptionEvent(Command command, Throwable exception) {
    super(command);
    this.exception = exception;
  }

  /**
   * Returns a SimpleMessage formatted as <code>[ &lt;command-name&gt; ] Started.</code>.
   */
  public Message getEventMessage() {
    return new SimpleMessage(MessageHelper.format(getCommand().getName(), exception.getMessage()));
  }

  /**
   * The exception that generated this event.
   *
   * @return The exception (as a <code>Throwable</code>) that caused this event to occur.
   */
  public Throwable getCause() {
    return exception;
  }
}
