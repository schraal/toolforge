package nl.toolforge.karma.cli.cmd;

import nl.toolforge.karma.core.cmd.CommandResponseHandler;
import nl.toolforge.karma.core.cmd.event.CommandResponseEvent;

/**
 * This class is responsible for handling CommandResponses in an interactive way.
 * Each time a CommandResponse changes, the changes are logged through the writer.
 *
 * @author W.H. Schraal
 */
public class CLICommandResponseHandler implements CommandResponseHandler {

  public void commandResponseChanged(CommandResponseEvent event) {
  }

}
