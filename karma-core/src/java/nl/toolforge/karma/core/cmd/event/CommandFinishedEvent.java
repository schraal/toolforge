package nl.toolforge.karma.core.cmd.event;

import nl.toolforge.karma.core.cmd.Command;

/**
 * Generated when a command was finished (without an exception).
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public final class CommandFinishedEvent extends CommandResponseEvent {

  private long start = 0L;
  private long duration = 0L;

  /**
   * Creates the event for <code>command</code>.
   *
   * @param command The command that has started.
   */
  public CommandFinishedEvent(Command command, long startTime) {

    super(command);

    start = startTime;
    duration = System.currentTimeMillis() - start;
  }

  /**
   * Returns the time this event was generated, effectively, the time the command was finished.
   *
   * @return Finish time of the command.
   */
  public long getTime() {
    return duration;
  }

  /**
   * Returns a SimpleMessage formatted as <code>[ &lt;command-name&gt; ] Started.</code>.
   */
  public Message getEventMessage() {

    if (duration < 1000) {
      return new SimpleMessage(MessageHelper.format(getCommand().getName(), "Finished in " + duration + " milliseconds."));
    } else {
      if (duration/1000 <= 1) {
        return new SimpleMessage(MessageHelper.format(getCommand().getName(), "Finished in " + duration/1000 + " second."));
      } else {
        return new SimpleMessage(MessageHelper.format(getCommand().getName(), "Finished in " + duration/1000 + " seconds."));
      }
    }
  }
}
