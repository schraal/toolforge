package nl.toolforge.karma.cli.cmd;

import nl.toolforge.karma.core.cmd.CommandResponseHandler;
import nl.toolforge.karma.core.cmd.event.CommandResponseEvent;
import nl.toolforge.karma.cli.ConsoleWriter;

/**
 * This class is responsible for handling CommandResponses in an interactive way.
 * Each time a CommandResponse changes, the changes are logged through the writer.
 *
 * @author W.H. Schraal
 */
public class CLICommandResponseHandler implements CommandResponseHandler {

  private ConsoleWriter writer = null;

  public CLICommandResponseHandler(ConsoleWriter writer) {
    this.writer = writer;
  }

  public void commandHeartBeat() {
    writer.showProgress();
  }

  public void commandResponseChanged(CommandResponseEvent event) {
    writer.writeln(event.getEventMessage().getMessageText());
  }

  public void commandResponseFinished(CommandResponseEvent event) {
    writer.newLine();
  }

}
