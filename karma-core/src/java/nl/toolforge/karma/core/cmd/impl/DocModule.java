package nl.toolforge.karma.core.cmd.impl;

import nl.toolforge.karma.core.cmd.DefaultCommand;
import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.CommandDescriptor;

/**
 * Generates API documentation for the given module.
 *
 * @author W.H. Schraal
 */
public class DocModule extends DefaultCommand {

  public DocModule(CommandDescriptor descriptor) {
    super(descriptor);
  }

  public void execute() throws CommandException {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  public CommandResponse getCommandResponse() {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

}
