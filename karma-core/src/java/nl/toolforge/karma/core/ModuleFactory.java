package nl.toolforge.karma.core;

import nl.toolforge.karma.vc.VersionControlSystem;

/**
 * Factory class to create modules. Note that we're talking about <b>new</b> modules, <b>not</b> existing modules in a
 * manifest.
 *
 * @author D.A. Smedes
 */
public class ModuleFactory {

    private static ModuleFactory instance;

    public synchronized static ModuleFactory getInstance() {
        if (instance == null) {
            instance = new ModuleFactory();
        }
        return instance;
    }

    private ModuleFactory() {
    }

    /**
     * <p>Creates a module in a version control system. If the module does not yet
     * exist in the version control system provided, this method will try and create the
     * module in the version control system.
     *
     * <p>This method is to be used when a module should be created irrespective of
     * a manifest,
     *
     * @param vcs The version control system in which this module should be created.
     * @param moduleName The (unique) name of the module within a <code>vcs</code>.
     *
     * @return A <code>SourceModule</code> instance. Note that <code>JarModule</code> instances cannot be created in
	 *         a version control system (well, they shouldn't).
     */
    public Module createModule(VersionControlSystem vcs, String moduleName) throws KarmaException {

        return new SourceModule(moduleName);
    }
}

