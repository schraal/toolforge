package nl.toolforge.karma.cli.cmd;

import nl.toolforge.karma.core.KarmaException;
import nl.toolforge.karma.core.cmd.ActionCommandResponse;
import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.SimpleCommandMessage;
import nl.toolforge.karma.core.cmd.CommandResponseHandler;
import nl.toolforge.karma.core.cmd.event.CommandResponseEvent;
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

  public void execute() {

    super.execute();

		CommandResponse response = new ActionCommandResponse();
		response.addMessage(new SimpleCommandMessage(getFrontendMessages().getString("message.MODULES_UPDATED")));
  }

}