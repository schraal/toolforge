package nl.toolforge.karma.core.cmd.impl;

import nl.toolforge.karma.core.ManifestException;
import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.DefaultCommand;
import nl.toolforge.karma.core.cmd.SimpleCommandResponse;

/**
 * This command activates a manifest, which is a general requirement for most other commands. The newly activated
 * manifest is stored for the Karma session in the {@link nl.toolforge.karma.core.cmd.CommandContext} that is associated
 * with the Karma session.
 *
 * @author W.H. Schraal
 * @author D.A. Smedes
 *
 * @version $Id$
 */
public class SelectManifest extends DefaultCommand {

  public SelectManifest(CommandDescriptor descriptor) {
    super(descriptor);
  }

  /**
   * Executes the real stuff, i.e. activates the manifest.
   *
   * @return A command response with the results of the activation.
   *
   * @throws ManifestException When problems occurred while loading the new manifest.
   */
  public CommandResponse execute() throws ManifestException {

    // Select a manifest and store it in the command context
    //
    getContext().changeCurrent(getCommandLine().getOptionValue("m"));

    return new SimpleCommandResponse();
  }

}
