package nl.toolforge.karma.cli.cmd;

import nl.toolforge.karma.cli.ConsoleConfiguration;
import nl.toolforge.karma.core.LocalEnvironment;
import nl.toolforge.karma.core.cmd.ActionCommandResponse;
import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.cmd.CommandMessage;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.SuccessMessage;
import nl.toolforge.karma.core.cmd.impl.SelectManifest;
import nl.toolforge.karma.core.manifest.ManifestException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * @author W.H. Schraal
 *
 * @version $Id$
 */
public class SelectManifestImpl extends SelectManifest {

	private static Log logger = LogFactory.getLog(SelectManifestImpl.class);

  public SelectManifestImpl(CommandDescriptor descriptor) {
    super(descriptor);
  }

  /**
   * Execute the command in the CLI. When the execution was succesfull, a message is shown on the console.
   *
   */
  public void execute() throws CommandException {

    // Use stuff that's being done in the superclass.
    //
    super.execute(); // Ignore the response from the superclass

		ConsoleConfiguration.setManifest(getContext().getCurrentManifest());

		// Store this manifest as the last used manifest.
		//
		Preferences.userRoot().put(LocalEnvironment.LAST_USED_MANIFEST_PREFERENCE, getContext().getCurrentManifest().getName());
		try {
			Preferences.userRoot().flush();
		} catch (BackingStoreException e) {
			logger.warn("Could not write user preferences due to java.util.prefs.BackingStoreException.");
		}

		CommandMessage message = new SuccessMessage(getFrontendMessages().getString("message.MANIFEST_ACTIVATED"));
    CommandResponse response = new ActionCommandResponse();
    response.addMessage(message);
  }
}
