package nl.toolforge.karma.cli.cmd;

import nl.toolforge.karma.cli.ConsoleConfiguration;
import nl.toolforge.karma.core.ManifestException;
import nl.toolforge.karma.core.cmd.*;
import nl.toolforge.karma.core.cmd.impl.SelectManifest;

/**
 * @author W.H. Schraal
 *
 * @version $Id$
 */
public class SelectManifestImpl extends SelectManifest {

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
  public CommandResponse execute() throws ManifestException {

    // Use stuff that's being done in the superclass.
    //
    super.execute(); // Ignore the response from the superclass

		ConsoleConfiguration.setManifest(getContext().getCurrent());

    CommandMessage message = new SimpleCommandMessage(getFrontendMessages().getString("message.MANIFEST_ACTIVATED"));
    CommandResponse response = new SimpleCommandResponse();
    response.addMessage(message);

    return response;
  }
}
