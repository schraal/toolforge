package nl.toolforge.karma.core.cmd.event;

/**
 * When CommandResponses change, classes that implement this interface (and register themselves)
 * recieve a CommandResponseEvent.
 */
public interface CommandResponseListener {

  public void commandHeartBeat();

  /**
   * Called when a CommandResponse has changed, for example when a new CommandMessage has been added.
   *
   * @param event  The event that described what has changed in the CommandResponse, so that the Listener
   *               can react adequately.
   */
  public void commandResponseChanged(CommandResponseEvent event);

  /**
   * Called when a command is finished. Especially usefull when an interactive system is generating a lot of response
   * and the end of the full 'transaction' must be fetched.
   *
   * @param event An event; can be <code>null</code>.
   */
  public void commandResponseFinished(CommandResponseEvent event);

}
