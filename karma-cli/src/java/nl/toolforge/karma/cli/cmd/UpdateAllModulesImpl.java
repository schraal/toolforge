package nl.toolforge.karma.cli.cmd;

import nl.toolforge.karma.core.KarmaException;
import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.SimpleCommandMessage;
import nl.toolforge.karma.core.cmd.impl.UpdateModuleCommand;

/**
 * Implementation for the <code>UpdateModuleCommand</code> for the command-line interface application.
 *
 * @author D.A. Smedes 
 * 
 * @version $Id:
 */
public class UpdateAllModulesImpl extends UpdateModuleCommand {

  public UpdateAllModulesImpl(CommandDescriptor descriptor) throws CommandException {
    super(descriptor);
  }

  public CommandResponse execute() throws KarmaException {

    CommandResponse response = super.execute();

		response.addMessage(new SimpleCommandMessage(getFrontendMessages().getString("message.MODULES_UPDATED")));

    return response;
  }

}