package nl.toolforge.karma.cli.cmd;

import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.cmd.impl.UpdateAllModulesCommand;

/**
 * Implementation for the <code>UpdateModuleCommand</code> for the command-line interface application.
 *
 * @author D.A. Smedes
 *
 * @version $Id$
 */
public class UpdateAllModulesImpl extends UpdateAllModulesCommand {

  public UpdateAllModulesImpl(CommandDescriptor descriptor) throws CommandException {
    super(descriptor);
  }

  public void execute() throws CommandException {
    super.execute();
  }

}