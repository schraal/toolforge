package nl.toolforge.karma.core;

import java.io.File;

/**
 * <p>A manifest is a collection of modules that form a
 * software release (or part thereof). KARMA uses a <code>Manifest</code> instance
 * to build an application server instance.
 * <p/>
 * <p>See http://www.sourceforge,net/projects/toolforge/karma or
 * http://toolforge.sourceforge.net/karma for extensive
 * coverage of the conceptual manifest that Karma supports.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public interface Manifest {

	public static final String NAME_ATTRIBUTE = "name";
	public static final String VERSION_ATTRIBUTE = "version"; // Currently functionally unused

	public String getName();

	/**
	 * Retrieves all modules from the manifest.
	 *
	 * @return All modules in this manifest as a <code>ModuleMap</code>. When no modules
	 *         are present, this method will return an empty list.
	 */
	public ModuleMap getModules();

	public void addModule(Module module);

	/**
	 * Retrieves a module instance from this manifest.
	 *
	 * @param moduleName The name of the module contained in this manifest.
	 * @return The correct module instance as found in the manifest.
	 * @throws ManifestException See {@link ManifestException#NO_SUCH_MODULE}.
	 */
	public Module getModule(String moduleName) throws ManifestException;

	/**
	 * Creates a <code>SourceModule</code>.
	 *
	 * @param name          The (unique) name of the module within a <code>vcs</code>.
	 * @param locationAlias The version control system in which this module should be created.
	 */
	public Module createModule(String name, String locationAlias) throws KarmaException;

	/**
	 * <p>Creates a module and includes the module in the manifest instance. If the module
	 * does not yet exist in the version control system provided, this method will try and
	 * create the module in the version control system. If the module already exists in the
	 * version control system, a <code>KarmaException</code> will be thrown. When the module
	 * should be added to the manifest anyway, the {@link #createModule(int, String, String, boolean)} should be used.
	 * <p/>
	 * <p>This method is not intended to load modules from a manifest file into the Manifest
	 * instance. See {@link nl.toolforge.karma.core.ManifestLoader}.
	 *
	 * @param name          See {@link #createModule(java.lang.String, java.lang.String)}.
	 * @param locationAlias See {@link #createModule(java.lang.String, java.lang.String)}
	 * @return A <code>Module</code> instance.
	 * @throws KarmaException TODO to be documented; should be manifest exception ??
	 */
	public Module createModule(String name, String locationAlias, boolean include) throws KarmaException;

	/**
	 * <p>Creates a module and includes the module in the manifest instance. If the module
	 * does not yet exist in the version control system provided, this method will try and
	 * create the module in the version control system. If the module already exists in the
	 * version control system, a <code>KarmaException</code> will be thrown. When the module
	 * should be added to the manifest anyway, the {@link #createModule(int, String, String, boolean)} should be used.
	 * <p/>
	 * <p>This method is not intended to load modules from a manifest file into the Manifest
	 * instance. See {@link nl.toolforge.karma.core.ManifestLoader}.
	 *
	 * @param typeIdentifier See {@link Module}. A type identifier should be provided.
	 * @param name           See {@link #createModule(java.lang.String, java.lang.String)}.
	 * @param locationAlias  See {@link #createModule(java.lang.String, java.lang.String)}
	 * @return A <code>Module</code> instance.
	 * @throws KarmaException TODO to be documented; should be manifest exception ??
	 */
	public Module createModule(int typeIdentifier, String name, String locationAlias) throws KarmaException;

	/**
	 * @param typeIdentifier See {@link Module}. A type identifier should be provided. <b>NOTE:</b> unused at this moment.
	 * @param locationAlias  {@link #createModule(java.lang.String, java.lang.String)}.
	 * @param name           {@link #createModule(java.lang.String, java.lang.String)}.
	 * @param include        <code>true</code> when the module should be added to the manifest file
	 *                       irrespective of succesfull creation in the version control system. Defaults to <code>false</code>.
	 * @return A <code>Module</code> instance.
	 * @throws KarmaException When the new module manifest file could not be written
	 *                        ({@link nl.toolforge.karma.core.ManifestException#MANIFEST_FLUSH_ERROR}
	 */
	public Module createModule(int typeIdentifier, String name, String locationAlias, boolean include) throws KarmaException;

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

	/**
	 * Checks if a module is present locally within the context of the manifest. Implementations of user interfaces that
	 * wish to have multiple instances of a manifest within one JVM should build in support for the specific
	 * thread as well. The latter is not performed by this implementation.
	 *
	 * @param module The module that should be checked.
	 * @return <code>true</code> if the module is present locally.
	 */
	public boolean isLocal(Module module) throws KarmaException;

	/**
	 * A manifest on a users' local disk is located in a directory relative to
	 * {@link nl.toolforge.karma.core.prefs.Preferences#getDevelopmentHome}. This method returns the full path to this
	 * manifests' directory.
	 *
	 * @return A <code>File</code> reference to the manifests' local directory.
	 * @throws ManifestException When a <code>File</code> reference cannot be obtained. See {@link ManifestException#INVALID_LOCAL_PATH}.
	 */
	public File getLocalPath() throws ManifestException;

}
