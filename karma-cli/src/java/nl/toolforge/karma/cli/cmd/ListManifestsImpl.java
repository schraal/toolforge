package nl.toolforge.karma.cli.cmd;

import nl.toolforge.karma.core.ManifestException;
import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.cmd.CommandResponse;
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

	public CommandResponse execute() {

    CommandResponse response = new SimpleCommandResponse();

		return response;
	}
}