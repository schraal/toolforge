package nl.toolforge.karma.core;

import java.io.File;

/**
 * <p>A manifest is a collection of modules that form a software release (or part thereof). A manifest is represented
 * by a manifest file (used to persist the definition of the manifest).
 *
 * <p>See <a href="http://www.toolforge.nl">toolforge</a> for more descriptive information on the manifest principle.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public interface Manifest {

	/** Element name for an <code>include</code>-element in a manifest XML file */
	public static final String INCLUDE_ELEMENT_NAME = "include";

	public static final String NAME_ATTRIBUTE = "name";
	public static final String VERSION_ATTRIBUTE = "version"; // Currently functionally unused

	public String getName();

	/**
	 * Retrieves all modules from the manifest.
	 *
	 * @return All modules in this manifest as a <code>ModuleMap</code>. When no modules are present, this method will
   * return an empty list.
	 */
	public ModuleMap getModules();

	/**
	 * Retrieves a module instance from this manifest.
	 *
	 * @param moduleName The name of the module contained in this manifest.
	 * @return The correct module instance as found in the manifest.
	 * @throws ManifestException See {@link ManifestException#NO_SUCH_MODULE}.
	 */
	public Module getModule(String moduleName) throws ManifestException;

  /**
   * A manifest is responsible for construction its internal data structure. <code>ModuleDescriptor</code> instances
   * describe one specific module as defined in the manifest xml file. This method creates a module within the manifest'
   * internal data structure to be able to manage the module during the life-cycle of the Karma run.
   *
   * @param module A module descriptor for this manifest.
   */
  public void addModule(ModuleDescriptor module) throws ManifestException;

	public int countSourceModules();

	public int countAllModules();

	public int countJarModules();

	/**
	 * Checks if a module is present locally within the context of the manifest.
	 *
	 * @param module The module that should be checked.
	 * @return <code>true</code> if the module is present locally.
	 */
	public boolean isLocal(Module module);

  /**
   * Checks if the manifest has been checked-out before. This method checks the existence of all the manifests'
   * modules' directories only. When a module directory is not present, a <code>ManifestException</code> is thrown.
   *
   * @return
   */
  public boolean isLocal();

	/**
	 * A manifest on a users' local disk is located in a directory relative to {@link LocalEnvironment#getDevelopmentHome}.
	 * This method returns the full path to this manifests' directory, and results in something like a file reference to
	 * <code>/home/asmedes/karma/projects/test-1.0/</code>
	 *
	 * @return A <code>File</code> reference to the manifests' local directory.
	 * @throws ManifestException When a <code>File</code> reference cannot be obtained. See {@link ManifestException#INVALID_LOCAL_PATH}.
	 */
	public File getDirectory() throws ManifestException;

}
