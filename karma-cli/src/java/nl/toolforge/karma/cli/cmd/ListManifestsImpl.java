package nl.toolforge.karma.cli.cmd;

import java.util.Iterator;
import java.util.Set;

import nl.toolforge.karma.core.ManifestException;
import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.SimpleCommandMessage;
import nl.toolforge.karma.core.cmd.QueryCommandResponse;
import nl.toolforge.karma.core.cmd.CommandResponseHandler;
import nl.toolforge.karma.core.cmd.impl.ListManifests;

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

	public void execute() {
    try {
      CommandResponse response = getCommandResponse();

      Set manifests = getContext().getAllManifests();

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