package nl.toolforge.karma.core.cmd.impl;

import nl.toolforge.karma.core.KarmaRuntimeException;
import nl.toolforge.karma.core.ManifestException;
import nl.toolforge.karma.core.cmd.DefaultCommand;

import java.util.Set;

/**
 * Base implementation for the ListManifests command.
 *
 * @author D.A. Smedes  
 * @version $revision $date $author
 */
public abstract class ListManifests extends DefaultCommand {

	protected Set manifests = null;

	/**
	 * This constructor performs all generic (enduser-independent functionality). Implementation are
	 *
	 * @throws ManifestException See {@link ManifestException#NO_MANIFEST_STORE_DIRECTORY}.
	 */
	public ListManifests() throws ManifestException {
     manifests = getContext().getAll();
	}

	/**
	 * Gets the list of manifests.
	 *
	 * @return The list of manifests.
	 */
	protected Set getManifests() {

		if (manifests == null) {
			 throw new KarmaRuntimeException("The constructor of this class was not called to initialize local properties.");
		}
		return manifests;
	}
}