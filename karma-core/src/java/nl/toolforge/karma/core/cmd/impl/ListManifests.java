package nl.toolforge.karma.core.cmd.impl;

import nl.toolforge.karma.core.ManifestException;
import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.cmd.DefaultCommand;

import java.util.Set;

/**
 * Base implementation for the ListManifests command.
 *
 * @author D.A. Smedes
 * @version @version $Id$
 */
public abstract class ListManifests extends DefaultCommand {

	/**
	 * This constructor performs all generic (enduser-independent functionality).
	 *
	 * @throws ManifestException See {@link nl.toolforge.karma.core.ManifestException#NO_MANIFEST_STORE_DIRECTORY}.
	 */
	public ListManifests(CommandDescriptor descriptor) throws ManifestException {
		super(descriptor);
	}

}