package nl.toolforge.karma.cli.cmd;

import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.cmd.CommandMessage;
import nl.toolforge.karma.core.cmd.SuccessMessage;
import nl.toolforge.karma.core.cmd.impl.StopWorkCommand;

/**
 * @author W.H. Schraal
 */
public class StopWorkImpl extends StopWorkCommand {

  public StopWorkImpl(CommandDescriptor descriptor) {
    super(descriptor);
  }

  public void execute() throws CommandException {

    CommandMessage message = new SuccessMessage(getFrontendMessages().getString("message.STOP_WORK_STARTED"), new Object[]{getCommandLine().getOptionValue("m")});
    response.addMessage(message);

    super.execute();
  }

}
