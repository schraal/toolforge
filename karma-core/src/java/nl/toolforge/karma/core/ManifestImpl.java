package nl.toolforge.karma.core;

import nl.toolforge.karma.core.vc.VersionControlSystem;
import nl.toolforge.karma.core.exception.ErrorCode;

import java.io.File;

/**
 * Implementation for a manifest.
 *
 * @see nl.toolforge.karma.core.Manifest
 *
 * @author D.A. Smedes
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
			throw new KarmaRuntimeException(KarmaRuntimeException.INVALID_MANIFEST_NAME);
		}
		this.manifestName = manifestName;
    }

    public ModuleList getModules() {
        return modules;
    }

	/**
	 * Adds a <code>Module</code> instance to the
	 */
	public void addModule(Module module) {
		modules.add(module);
	}

    public Module createModule(VersionControlSystem vcs, String name) throws KarmaException {
        return null;
    }

    public Module createModule(VersionControlSystem vcs, String name, boolean b) throws KarmaException {
        return null;
    }

    public File getPath() {

        return new File("");
    }

	public int countSourceModules() {
		return 0;
	}

	public int countAllModules() {
		return 0;
	}

	public int countJarModules() {
		return 0;
	}

	public String getName() {
        return manifestName;
    }
}
