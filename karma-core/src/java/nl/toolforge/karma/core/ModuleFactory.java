package nl.toolforge.karma.core;

import nl.toolforge.karma.core.vc.VersionControlSystem;
import nl.toolforge.karma.core.location.Location;
import nl.toolforge.karma.core.location.LocationFactory;

/**
 * Factory class to create modules. We're talking about <b>new</b> modules, <b>not</b> existing modules in a
 * manifest; these are read by the {@link ManifestLoader} when required.
 *
 * @author D.A. Smedes
 *
 * @version $Id$
 */
public class ModuleFactory {

	private static ModuleFactory instance;

	/**
	 * Get the singleton instance of this factory.
	 *
	 * @return The factory to create modules.
	 */
	public synchronized static ModuleFactory getInstance() {
		if (instance == null) {
			instance = new ModuleFactory();
		}
		return instance;
	}

	private ModuleFactory() {}

	/**
	 * <p>Creates a module in a version control system. If the module does not yet
	 * exist in the version control system provided, this method will try and create the
	 * module in the version control system.
	 *
	 * <p>This method is to be used when a module should be created irrespective of
	 * a manifest.
	 *
	 * @param type See {@link Module}. A specific type identifier has to be provided.
	 * @param moduleName The (unique) name of the module within a <code>vcs</code>.
	 * @param locationAlias The version control system in which this module should be created.
	 *
	 * @return A <code>SourceModule</code> instance. Note that <code>JarModule</code> instances cannot be created in
	 *         a version control system (well, they shouldn't).
	 * @throws KarmaException KarmaException#LOCATION_NOT_FOUND when not location could be found for
	 *         <code>locationAlias</code>.
	 */
	public Module createModule(int type, String moduleName, String locationAlias) throws KarmaException {

    Location location = LocationFactory.getInstance().get(locationAlias);





		return new SourceModule(moduleName, location);
	}
}

