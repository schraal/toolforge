package nl.toolforge.karma.cli.cmd;

import nl.toolforge.karma.core.cmd.ActionCommandResponse;
import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.cmd.CommandMessage;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.SimpleCommandMessage;
import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.cmd.impl.PromoteCommand;

public class PromoteModuleImpl extends PromoteCommand {

  private CommandResponse commandResponse = new ActionCommandResponse();

  public PromoteModuleImpl(CommandDescriptor descriptor) {
    super(descriptor);
  }

  public void execute() throws CommandException {

    super.execute();

    CommandMessage message =
        new SimpleCommandMessage(
            getFrontendMessages().getString("message.MODULE_PROMOTED"),
            new Object[]{getCommandLine().getOptionValue("m"), getNewVersion()}
        );
    commandResponse.addMessage(message);
  }

  public CommandResponse getCommandResponse() {
    return commandResponse;
  }
}
