package nl.toolforge.karma.cli.cmd;

import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.QueryCommandResponse;
import nl.toolforge.karma.core.cmd.SimpleCommandMessage;
import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.cmd.SuccessMessage;
import nl.toolforge.karma.core.cmd.impl.ListManifests;
import nl.toolforge.karma.core.manifest.ManifestException;

import java.util.Iterator;
import java.util.Set;
import java.util.Collection;

/**
 * Command line interface implementation of the {@link ListManifests} command.
 *
 * @author D.A. Smedes
 * @author W.H. Schraal
 * @version $Id$
 */
public class ListManifestsImpl extends ListManifests {

  CommandResponse commandResponse = new QueryCommandResponse();

  public ListManifestsImpl(CommandDescriptor descriptor) throws ManifestException {
    super(descriptor);
  }

  public void execute() throws CommandException {
    CommandResponse response = getCommandResponse();

    Collection manifests = null;
    try {
      manifests = getContext().getAllManifests();
    } catch (ManifestException e) {
      throw new CommandException(e.getErrorCode());
    }

    if (manifests.size() == 0) {
      response.addMessage(new SuccessMessage("No manifests found."));
    } else {

      Iterator manifestsIterator = manifests.iterator();
      while (manifestsIterator.hasNext()) {
        Object manifest = manifestsIterator.next();
        response.addMessage(new SimpleCommandMessage(manifest.toString()));
      }
    }
  }

  public CommandResponse getCommandResponse() {
    return this.commandResponse;
  }

}