package nl.toolforge.karma.core.cmd.impl;

import nl.toolforge.karma.core.cmd.DefaultCommand;
import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.CommandDescriptor;

/**
 * Basically calls the test command on all modules in the current manifest.
 * This will result in a test for all modules.
 *
 * @author W.H. Schraal
 */
public class TestAllModules extends DefaultCommand {

  public TestAllModules(CommandDescriptor descriptor) {
    super(descriptor);
  }

  public void execute() throws CommandException {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  public CommandResponse getCommandResponse() {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

}
