package nl.toolforge.karma.cli.cmd;

import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.cmd.SuccessMessage;
import nl.toolforge.karma.core.cmd.impl.UpdateModuleCommand;

/**
 * Implementation for the <code>UpdateModuleCommand</code> for the command-line interface application.
 *
 * @author D.A. Smedes 
 * 
 * @version $Id:
 */
public class UpdateModuleImpl extends UpdateModuleCommand {

  public UpdateModuleImpl(CommandDescriptor descriptor) throws CommandException {
    super(descriptor);
  }

  public void execute() throws CommandException {
    SuccessMessage message = new SuccessMessage("Updating module, please wait ...");
    response.addMessage(message);

    super.execute();
  }

}