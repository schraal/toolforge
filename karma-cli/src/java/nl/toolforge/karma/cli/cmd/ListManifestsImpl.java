package nl.toolforge.karma.cli.cmd;

import nl.toolforge.karma.core.cmd.impl.ListManifests;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.SimpleCommandResponse;
import nl.toolforge.karma.core.ManifestException;

/**
 * Command line interface implementation of the {@link ListManifests} command.
 *
 * @author D.A. Smedes  
 * @version $Id$
 */
public class ListManifestsImpl extends ListManifests {

	public ListManifestsImpl() throws ManifestException {
		super();
	}

	public CommandResponse execute() {

    CommandResponse response = new SimpleCommandResponse();


		return response;
	}
}