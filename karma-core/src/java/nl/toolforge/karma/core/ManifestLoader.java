package nl.toolforge.karma.core;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import java.util.Set;
import java.util.HashSet;

import nl.toolforge.karma.core.exception.ErrorCode;

/**
 * <p>The manifest loader is responsible for loading a manifest from disk in memory.
 * Manifests are stored on disk in a directory identified by a property
 * <code>manifest.dir</code>.
 *
 * @author D.A. Smedes
 */
public final class ManifestLoader {

	private static ManifestLoader instance = null;

	public synchronized static ManifestLoader getInstance() {
		if (instance == null) {
			instance = new ManifestLoader();
		}
		return instance;
	}

    /**
     * <p>Loads a manifest with a given <code>id</code>. The id should be provided as
     * the filename part without the extension (a manifest file <code>karma-2.0.xml</code>)
     * is retrieved by providing this method with the <code>id</code> 'karma-2.0'.
     *
     * @param id See method description.
     *
     * @return A <code>Manifest</code> instance.
     *
     * @throws ManifestException When an error occurred while loading the manifest.
     */
    public static Manifest load(String id) throws ManifestException {

		Manifest manifest = null;
		String manifestName = null;

        File manifestFile = null;

		Set duplicates = new HashSet();
        Set recursions = new HashSet();

		try {

		 manifestFile = new File(UserEnvironment.getManifestStore().getPath() + File.separator + id + ".xml");

		} catch (NullPointerException n) {
			throw new ManifestException(ManifestException.MANIFEST_FILE_NOT_FOUND);
		}

		// We're assuming that basic validation has been done by a DTD or XML Schema
		//

		try {

			DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document manifestDocument = documentBuilder.parse(new FileInputStream(manifestFile));

			// The <manifest>-element
			//
			Element manifestElement = manifestDocument.getDocumentElement();
            manifestName = manifestElement.getAttribute(Manifest.NAME_ATTRIBUTE);

            NodeList sourceModules = manifestElement.getElementsByTagName("sourcemodule");

			for (int i = 0; i < sourceModules.getLength(); i++) {

                ModuleList sList = new ModuleList();
                SourceModule sourceModule = null;

				Element sourceModuleElement = (Element) sourceModules.item(i);

				String moduleName = sourceModuleElement.getAttribute(Module.NAME_ATTRIBUTE);
				String version = sourceModuleElement.getAttribute(SourceModule.VERSION_ATTRIBUTE);
                String branch = sourceModuleElement.getAttribute(SourceModule.BRANCH_ATTRIBUTE);

				sourceModule = new SourceModule(moduleName);

				boolean added = duplicates.add(sourceModule);

				if (!added) {
					// Duplicate modules are not allowed
					//
					throw new ManifestException(ManifestException.DUPLICATE_MODULE_IN_MANIFEST);
				}

			}

			NodeList jarModules = manifestElement.getElementsByTagName("jarmodule");

			for (int i = 0; i < jarModules.getLength(); i++) {


			}

		} catch (Exception e) {

			//

		}

		// OK, we have all we need, let's fill up the manifest instance
		//

		try {
			manifest = new ManifestImpl(manifestName);
		} catch (Exception e) {
			throw new ManifestException();
		}

        return null;
    }

}
