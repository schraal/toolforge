package nl.toolforge.karma.core.cmd.event;

/**
 * When CommandResponses change, classes that implement this interface (and register themselves)
 * recieve a CommandResponseEvent.
 */
public interface CommandResponseListener {

  /**
   * Called when a CommandResponse has changed, e.g. when a new CommandMessage has been added.
   *
   * @param event  The event that described what has changed in the CommandResponse, so that the Listener
   *               can react adequately.
   */
  public void commandResponseChanged(CommandResponseEvent event);

}
