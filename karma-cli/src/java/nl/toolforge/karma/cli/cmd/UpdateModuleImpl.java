package nl.toolforge.karma.cli.cmd;

import nl.toolforge.karma.core.KarmaException;
import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.CommandResponseHandler;
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

  public void execute() {
    super.execute();
  }

}