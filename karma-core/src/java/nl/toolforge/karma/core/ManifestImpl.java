package nl.toolforge.karma.core;

import nl.toolforge.karma.core.vc.VersionControlSystem;

import java.io.File;

/**
 * Implementation for a manifest.
 *
 * @see nl.toolforge.karma.core.Manifest
 *
 * @author D.A. Smedes
 *
 * @version $Id$
 */
public class ManifestImpl implements Manifest {

	private String manifestName = null;

	private ModuleList modules = null;

	/**
	 * Constructs a manifest instance with name <code>manifestName</code>.
	 *
	 * @param manifestName The name of the manifest, which is the <code>name</code>-attribute from the
	 *                     <code>manifest</code>-element, when parsed from a manifest XML file.
	 */
	public ManifestImpl(String manifestName) {

		if ((manifestName == null) || (manifestName.length() == 0)) {
			// Most probably a programmers' mistake, so a runtime exception
			//
			throw new KarmaRuntimeException("Manifest name should not be null or an empty string.");
		}
		this.manifestName = manifestName;
		this.modules = new ModuleList();
	}

	public final ModuleList getModules() {
		return modules;
	}

	/**
	 * Adds a <code>Module</code> instance to this manifest. Not the same as {@link #createModule} !
	 */
	public final void addModule(Module module) {
		modules.add(module);
	}

	public final Module createModule(VersionControlSystem vcs, String name) throws KarmaException {
		return null;
	}

	public final Module createModule(VersionControlSystem vcs, String name, boolean b) throws KarmaException {
		return null;
	}

	public final File getPath() {

		return new File("");
	}

	public int countSourceModules() {
		return getModules().getSourceModules().size();
	}

	public int countAllModules() {
		return getModules().size();
	}

	public int countJarModules() {
		return getModules().getJarModules().size();
	}

	public String getName() {
		return manifestName;
	}
}
