package nl.toolforge.karma.cli.cmd;

import nl.toolforge.karma.core.KarmaException;
import nl.toolforge.karma.core.cmd.*;
import nl.toolforge.karma.core.cmd.impl.UpdateAllModulesCommand;

/**
 * Implementation for the <code>UpdateModuleCommand</code> for the command-line interface application.
 *
 * @author D.A. Smedes 
 * 
 * @version $Id:
 */
public class UpdateAllModulesImpl extends UpdateAllModulesCommand {

  public UpdateAllModulesImpl(CommandDescriptor descriptor) throws CommandException {
    super(descriptor);
  }

  public CommandResponse execute() throws KarmaException {

    super.execute();

		CommandResponse response = new SimpleCommandResponse();
		response.addMessage(new SimpleCommandMessage(getFrontendMessages().getString("message.MODULES_UPDATED")));

    return response;
  }

}