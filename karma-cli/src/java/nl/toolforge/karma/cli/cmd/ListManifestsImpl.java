package nl.toolforge.karma.cli.cmd;

import java.util.Iterator;
import java.util.Set;

import nl.toolforge.karma.core.ManifestException;
import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.SimpleCommandMessage;
import nl.toolforge.karma.core.cmd.SimpleCommandResponse;
import nl.toolforge.karma.core.cmd.impl.ListManifests;

/**
 * Command line interface implementation of the {@link ListManifests} command.
 *
 * @author D.A. Smedes  
 * @version $Id$
 */
public class ListManifestsImpl extends ListManifests {

	public ListManifestsImpl(CommandDescriptor descriptor) throws ManifestException {
		super(descriptor);
	}

	public CommandResponse execute() throws ManifestException {
    CommandResponse response = new SimpleCommandResponse();

    Set manifests = getContext().getAllManifests();

    Iterator manifestsIterator = manifests.iterator();
    while (manifestsIterator.hasNext()) {
      Object manifest = manifestsIterator.next();
      response.addMessage(new SimpleCommandMessage(manifest.toString()));
    }

    return response;
	}
}