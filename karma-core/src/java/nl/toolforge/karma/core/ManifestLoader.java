package nl.toolforge.karma.core;

/**
 * <p>The manifest loader is responsible for loading a manifest from disk in memory.
 * Manifests are stored on disk in a directory identified by a property
 * <code>manifest.dir</code>.
 *
 * @author D.A. Smedes
 */
public final class ManifestLoader {

    /**
     * <p>Loads a manifest with a given <code>id</code>. The id should be provided as
     * the filename part without the extension (a manifest file <code>karma-2.0.xmlm</code>)
     * is retrieved by providing this method with the <code>id</code> 'karma-2.0'.
     *
     * @param id See method description.
     *
     * @return A <code>Manifest</code> instance.
     *
     * @throws LoaderException When an error occurred while loading the manifest.
     */
    public static Manifest load(String id) throws LoaderException {

        return null;
    }

}
