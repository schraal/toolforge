package nl.toolforge.karma.core.cmd;

import nl.toolforge.karma.core.cmd.event.CommandResponseListener;
import nl.toolforge.karma.core.cmd.event.CommandResponseEvent;

/**
 * <p>A <code>CompositeCommand</code> is suited for executing multiple commands and at the same time, act as a
 * <code>CommandResponseListener</code>. A good example is a command traversing all modules in a manifest and calling
 * some command on each of them.
 *
 * <p>When a <code>CompositeCommand</code> is registered as a listener to other commands, the composite command is
 * responsible . An
 * example implementation is where the Com
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public abstract class CompositeCommand extends DefaultCommand implements CommandResponseListener {

  //public abstract void execute() throws KarmaException;

  public CompositeCommand(CommandDescriptor commandDescriptor) {
    super(commandDescriptor);
  }

  public CommandResponse getCommandResponse() {
    return null;
  }

  /**
   *
   */
  public abstract void commandHeartBeat();

  /**
   * Called when the CompositeCommand ...
   *
   * @param event
   */
  public abstract void commandResponseChanged(CommandResponseEvent event);

  /**
   *
   * @param event
   */
  public abstract void commandResponseFinished(CommandResponseEvent event);
}
