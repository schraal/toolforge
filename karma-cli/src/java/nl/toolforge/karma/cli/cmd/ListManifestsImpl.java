package nl.toolforge.karma.cli.cmd;

import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.QueryCommandResponse;
import nl.toolforge.karma.core.cmd.SimpleCommandMessage;
import nl.toolforge.karma.core.cmd.CommandException;
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
    try {
      CommandResponse response = getCommandResponse();

      Collection manifests = getContext().getAllManifests();

      Iterator manifestsIterator = manifests.iterator();
      while (manifestsIterator.hasNext()) {
        Object manifest = manifestsIterator.next();
        response.addMessage(new SimpleCommandMessage(manifest.toString()));
      }
    } catch (Exception e) {
      //todo proper exception handling
      e.printStackTrace();
    }
	}

  public CommandResponse getCommandResponse() {
    return this.commandResponse;
  }

}