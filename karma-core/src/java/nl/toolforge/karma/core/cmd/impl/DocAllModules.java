package nl.toolforge.karma.core.cmd.impl;

import nl.toolforge.karma.core.cmd.DefaultCommand;
import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.cmd.ActionCommandResponse;

/**
 * Generates API documentation for all modules.
 *
 * @author W.H. Schraal
 */
public class DocAllModules extends DefaultCommand {

  private CommandResponse commandResponse = new ActionCommandResponse();

  public DocAllModules(CommandDescriptor descriptor) {
    super(descriptor);
  }

  public void execute() throws CommandException {




  }

  public CommandResponse getCommandResponse() {
    return commandResponse;
  }

}
