package nl.toolforge.karma.cli.cmd;

import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.cmd.CommandMessage;
import nl.toolforge.karma.core.cmd.SuccessMessage;
import nl.toolforge.karma.core.cmd.impl.StartWorkCommand;

/**
 * @author W.H. Schraal
 */
public class StartWorkImpl extends StartWorkCommand {

  public StartWorkImpl(CommandDescriptor descriptor) {
    super(descriptor);
  }

  public void execute() throws CommandException {

    CommandMessage message = new SuccessMessage(getFrontendMessages().getString("message.START_WORK_STARTED"), new Object[]{getCommandLine().getOptionValue("m")});
    response.addMessage(message);

    super.execute();
  }

}
