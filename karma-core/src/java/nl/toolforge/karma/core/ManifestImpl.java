package nl.toolforge.karma.core;

import nl.toolforge.karma.vc.VersionControlSystem;

import java.io.File;

/**
 * Implementation for a manifest.
 *
 * @see nl.toolforge.karma.core.Manifest
 *
 * @author D.A. Smedes
 */
public class ManifestImpl implements Manifest {

    public String manifestName = null;

    public ManifestImpl() {
        //
    }

    public ModuleList getModules() {
        return null;
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

    public String getName() {
        return manifestName;
    }
}
