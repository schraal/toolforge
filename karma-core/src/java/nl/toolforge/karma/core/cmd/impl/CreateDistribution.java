package nl.toolforge.karma.core.cmd.impl;

import nl.toolforge.karma.core.cmd.ActionCommandResponse;
import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.DefaultCommand;
import nl.toolforge.karma.core.manifest.ManifestException;

/**
 * @author D.A. Smedes
 * @version $Id$
 */
public class CreateDistribution extends DefaultCommand {

  private CommandResponse commandResponse = new ActionCommandResponse();

  public CreateDistribution(CommandDescriptor descriptor) {
    super(descriptor);
  }

  public void execute() throws CommandException {

    // todo move to aspect; this type of checking can be done by one aspect.
    //
    if (!getContext().isManifestLoaded()) {
      throw new CommandException(ManifestException.NO_ACTIVE_MANIFEST);
    }

  }

  public CommandResponse getCommandResponse() {
    return commandResponse;
  }
}
