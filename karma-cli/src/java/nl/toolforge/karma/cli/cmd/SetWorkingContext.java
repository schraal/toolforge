package nl.toolforge.karma.cli.cmd;

import nl.toolforge.karma.core.boot.WorkingContext;
import nl.toolforge.karma.core.boot.WorkingContextConfiguration;
import nl.toolforge.karma.core.boot.WorkingContextException;
import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.DefaultCommand;
import nl.toolforge.karma.core.cmd.event.ExceptionEvent;
import nl.toolforge.karma.core.cmd.event.MessageEvent;
import nl.toolforge.karma.core.cmd.event.SimpleMessage;
import nl.toolforge.karma.core.location.LocationException;
import nl.toolforge.karma.core.manifest.Manifest;
import nl.toolforge.karma.core.manifest.ManifestException;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * Sets the default working context identifier as a user preference. Note, this command has no effect
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public class SetWorkingContext extends DefaultCommand {

  private CommandResponse response = new CommandResponse();

  public SetWorkingContext(CommandDescriptor descriptor) {
    super(descriptor);
  }

  public void execute() throws CommandException {

    String workingContextName = getCommandLine().getOptionValue("w");

    try {
      Preferences.userRoot().put(WorkingContext.WORKING_CONTEXT_PREFERENCE, workingContextName);
      Preferences.userRoot().flush();

      response.addEvent(new MessageEvent(this, new SimpleMessage("Loading new working context `" + workingContextName + "` ...")));

      WorkingContext w = new WorkingContext(workingContextName);

      WorkingContextConfiguration configuration = new WorkingContextConfiguration(w);
      try {
        configuration.load();
      } catch (WorkingContextException e) {
        throw new CommandException(CommandException.INVALID_WORKING_CONTEXT_CONFIGURATION, new Object[]{w.getName()});
      }
      w.configure(configuration);

      getContext().setWorkingContext(w);

      response.addEvent(new MessageEvent(this, new SimpleMessage("Working context set to `" + workingContextName + "`")));

    } catch (BackingStoreException e) {
      response.addEvent(new ExceptionEvent(this, e));
    }

    // Reload the last used manifest for the new working context.
    //
    try {
      response.addEvent(new MessageEvent(this, new SimpleMessage(getFrontendMessages().getString("message.LOADING_MANIFEST_FROM_HISTORY"))));
      Manifest manifest = getContext().getWorkingContext().getManifestCollector().loadManifestFromHistory();
      if (manifest != null) {

        getContext().changeCurrentManifest(getContext().getWorkingContext().getManifestCollector().loadManifestFromHistory());

        SimpleMessage message =
            new SimpleMessage(getFrontendMessages().getString("message.MANIFEST_ACTIVATED"), new Object[]{getContext().getCurrentManifest()});
        response.addEvent(new MessageEvent(this, message));

      } else {
        response.addEvent(new MessageEvent(this, new SimpleMessage(getFrontendMessages().getString("message.NO_MANIFEST_IN_HISTORY"))));
      }
    } catch (ManifestException e) {
      throw new CommandException(e.getErrorCode(), e.getMessageArguments());
    } catch (LocationException e) {
      throw new CommandException(e.getErrorCode(), e.getMessageArguments());
    }
  }

  public CommandResponse getCommandResponse() {
    return response;
  }
}
