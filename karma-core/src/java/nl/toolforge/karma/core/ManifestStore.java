package nl.toolforge.karma.core;

import nl.toolforge.karma.core.vc.VersionControlSystem;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Container for the directory on the user's harddisk (well, why not : a network
 * disk if need be) where manifest's are stored.
 *
 * @author D.A. Smedes
 */
public final class ManifestStore {

    private File directory = null;

    /**
     * Initializes the <code>ManifestStore</code>.
     *
     * @param directory A <code>File</code>
     * @throws FileNotFoundException When the directory does not exist.     *
     */
    public ManifestStore(File directory) throws FileNotFoundException {

    }

    /**
     * Updates the manifest store on disk.
     *
     * @param vcs A version control system instance.
     * @throws KarmaException
     */
    public static void update(VersionControlSystem vcs) throws KarmaException {

    }
}
