package nl.toolforge.karma.core.cmd;

import nl.toolforge.karma.core.cmd.event.CommandResponseListener;
import nl.toolforge.karma.core.cmd.event.CommandResponseEvent;
import nl.toolforge.karma.core.KarmaException;

/**
 * <p>A <code>CompositeCommand</code> is suited for executing multiple commands and at the same time, act as a
 * <code>CommandResponseListener</code>. A good example is a command traversing all modules in a manifest and calling
 * some command on each of them.
 *
 * @author D.A. Smedes
 * @version $Id:
 */
public abstract class CompositeCommand extends DefaultCommand implements CommandResponseListener {

  public abstract void execute() throws KarmaException;

  public CompositeCommand(CommandDescriptor commandDescriptor) {
    super(commandDescriptor);
  }

  public CommandResponse getCommandResponse() {
    return null;
  }

  public void commandHeartBeat() {
  }

  public void commandResponseChanged(CommandResponseEvent event) {
  }

  public void commandResponseFinished(CommandResponseEvent event) {
  }
}
