package nl.toolforge.karma.core.cmd.impl;

import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.cmd.DefaultCommand;
import nl.toolforge.karma.core.manifest.ManifestException;

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
	 * @throws ManifestException See {@link ManifestException#MANIFEST_STORE_NOT_FOUND}.
	 */
	public ListManifests(CommandDescriptor descriptor) throws ManifestException {
		super(descriptor);
	}

}