package nl.toolforge.karma.cli.cmd;

import nl.toolforge.karma.core.boot.WorkingContext;
import nl.toolforge.karma.core.cmd.ActionCommandResponse;
import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.DefaultCommand;
import nl.toolforge.karma.core.cmd.SuccessMessage;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * Sets the default working context identifier as a user preference. Note, this command has no effect
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public class SetWorkingContext extends DefaultCommand {

  CommandResponse response = new ActionCommandResponse();

  public SetWorkingContext(CommandDescriptor descriptor) {
    super(descriptor);
  }

  public void execute() throws CommandException {

    String workingContextName = getCommandLine().getOptionValue("w");

    try {
      Preferences.userRoot().put(WorkingContext.WORKING_CONTEXT_PREFERENCE, workingContextName);
      Preferences.userRoot().flush();

      response.addMessage(new SuccessMessage("Working context set to " + workingContextName));

    } catch (BackingStoreException e) {
      response.addMessage(new SuccessMessage(e.getMessage()));
    }
  }

  public CommandResponse getCommandResponse() {
    return response;
  }
}
