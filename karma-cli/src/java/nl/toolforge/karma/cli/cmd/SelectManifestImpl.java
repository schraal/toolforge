package nl.toolforge.karma.cli.cmd;

import nl.toolforge.karma.cli.ConsoleConfiguration;
import nl.toolforge.karma.cli.CLI;
import nl.toolforge.karma.core.ManifestException;
import nl.toolforge.karma.core.LocalEnvironment;
import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.cmd.CommandMessage;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.SimpleCommandMessage;
import nl.toolforge.karma.core.cmd.SimpleCommandResponse;
import nl.toolforge.karma.core.cmd.impl.SelectManifest;
import nl.toolforge.karma.core.location.LocationException;

import java.util.prefs.Preferences;
import java.util.prefs.BackingStoreException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author W.H. Schraal
 *
 * @version $Id$
 */
public class SelectManifestImpl extends SelectManifest {

	private static Log logger = LogFactory.getLog(SelectManifestImpl.class);

  public SelectManifestImpl(CommandDescriptor descriptor) throws ManifestException {
    super(descriptor);
  }

  /**
   * Execute the command in the CLI. When the execution was succesfull, a message is shown on the console.
   *
   * @return The response from the command execution.
   *
   * @throws ManifestException When the selection of the manifest failed.
   *   See {@link ManifestException#MANIFEST_LOAD_ERROR}.
   */
  public CommandResponse execute() throws ManifestException, LocationException {

    // Use stuff that's being done in the superclass.
    //
    super.execute(); // Ignore the response from the superclass

		ConsoleConfiguration.setManifest(getContext().getCurrent());

		// Store this manifest as the last used manifest.
		//
		Preferences.userRoot().put(LocalEnvironment.LAST_USED_MANIFEST_PREFERENCE, getContext().getCurrent().getName());
		try {
			Preferences.userRoot().flush();
		} catch (BackingStoreException e) {
			logger.warn("Could not write user preferences due to java.util.prefs.BackingStoreException.");
		}

		CommandMessage message = new SimpleCommandMessage(getFrontendMessages().getString("message.MANIFEST_ACTIVATED"));
    CommandResponse response = new SimpleCommandResponse();
    response.addMessage(message);

    return response;
  }
}
