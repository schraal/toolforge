package nl.toolforge.karma.core;

import nl.toolforge.karma.core.vc.VersionControlSystem;

import java.io.File;

/**
 * <p>A manifest is a collection of modules that form a
 * software release (or part thereof). KARMA uses a <code>Manifest</code> instance
 * to build an application server instance.
 *
 * <p>See http://www.sourceforge,net/projects/toolforge/karma or
 * http://toolforge.sourceforge.net/karma for extensive
 * coverage of the conceptual manifest that Karma supports.
 *
 * @author D.A. Smedes
 */
public interface Manifest {

	public static final String NAME_ATTRIBUTE = "name";
	public static final String VERSION_ATTRIBUTE = "version"; // Currently functionally unused

    public String getName();

    /**
     * Retrieves all modules from the manifest.
     *
     * @return All modules in this manifest as a <code>ModuleList</code>. When no modules
     *         are present, this method will return an empty list.
     */
    public ModuleList getModules();

	public void addModule(Module module);

    /**
     * <p>Creates a module and includes the module in the manifest instance. If the module
     * does not yet exist in the version control system provided, this method will try and
     * create the module in the version control system. If the module already exists in the
     * version control system, a <code>KarmaException</code> will be thrown. When the module
     * should be added to the manifest anyway, the
     * {@link #createModule(nl.toolforge.karma.core.vc.VersionControlSystem, java.lang.String, boolean)} should be used.
     *
     * <p>This method is not intended to load modules from a manifest file into the Manifest
     * instance. See {@link nl.toolforge.karma.core.ManifestLoader}.
     *
     * @param vcs The version control system in which this module should be created.
     * @param name The (unique) name of the module within a <code>vcs</code>.
     *
     * @return A <code>Module</code> instance.
     *
     * @throws nl.toolforge.karma.core.KarmaException When creation failed.
     */
    public Module createModule(VersionControlSystem vcs, String name) throws KarmaException;

    /**
     *
     * @param vcs The version control system in which this module should be created.
     * @param name The (unique) name of the module within a <code>vcs</code>.
     * @param addToFile <code>true</code> when the module should be added to the manifest file
     *                  irrespective of succesfull creation in the version control system. Defaults
     *                  to <code>false</code>.
     *
     * @return A <code>Module</code> instance.
     *
     * @throws nl.toolforge.karma.core.KarmaException When creation failed.
     */
    public Module createModule(VersionControlSystem vcs, String name, boolean addToFile) throws KarmaException;

    /**
     * Returns the physical location of this manifest's file representation on a user's
     * local HD.
     *
     * @return A <code>File</code> reference, such as <code>/home/asmedes/dev/manifests/karma-1.0.xml</code>
     */
    public File getPath();

	public int countSourceModules();

	public int countAllModules();

	public int countJarModules();
}
