package nl.toolforge.karma.core;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashSet;
import java.util.Set;

import nl.toolforge.karma.core.prefs.Preferences;

/**
 * <p>The manifest loader is responsible for loading a manifest from disk in memory. Manifests are stored on disk in
 * a directory identified by the property <code>manifest.dir</code>.
 *
 * @author D.A. Smedes
 */
public final class ManifestLoader {

	private static ManifestLoader instance = null;

	private static Preferences prefs = Preferences.getInstance(true);

	public synchronized static ManifestLoader getInstance() {
		if (instance == null) {
			instance = new ManifestLoader();
		}
		return instance;
	}

	/**
	 * When a manifest has been used before, this method will try and load it. A property
	 * <code>manifest.saved.id</code> in the <code>karma.properties</code> is used as the identifier for the manifest
	 * name.
	 *
	 * @return
	 * @throws ManifestException
	 */
	public static Manifest loadFromHistory() throws ManifestException {

		Manifest manifest = null;
		String historyId = null;

		try {
			// Try to resolve the manifest name from the history
			//
		} catch (Exception e) {
			throw new ManifestException(ManifestException.NO_HISTORY_AVAILABLE);
		}

		// Try to load it
		//

		manifest = getInstance().load(historyId);

		return manifest;

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

			if (id.endsWith(".xml")) {
				manifestFile = new File(prefs.getManifestStore().getPath() + File.separator + id);
			} else {
				manifestFile = new File(prefs.getManifestStore().getPath() + File.separator + id + ".xml");
			}
			//System.out.println(" >>>> " + manifestFile.getPath());

		} catch (NullPointerException n) {
			throw new ManifestException(ManifestException.MANIFEST_FILE_NOT_FOUND);
		}

		// We're assuming that basic validation has been done by a DTD or XML Schema
		//

		try {

//			System.out.println(getInstance().getClass().getName() + " >>> 1");

			DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document manifestDocument = documentBuilder.parse(new FileInputStream(manifestFile));

			// The <manifest>-element
			//
			Element manifestElement = manifestDocument.getDocumentElement();

//			System.out.println(getInstance().getClass().getName() + " >>> " + manifestElement.toString());

			manifestName = manifestElement.getAttribute(Manifest.NAME_ATTRIBUTE);

			// Create the actual Manifest instance
			//
			manifest = new ManifestImpl(manifestName);

			NodeList sourceModules = manifestElement.getElementsByTagName("sourcemodule");

//			System.out.println(getInstance().getClass().getName() + " #sourcemodules : " + sourceModules.getLength());

			for (int i = 0; i < sourceModules.getLength(); i++) {

				ModuleList sList = new ModuleList();
				SourceModule sourceModule = null;

				Element sourceModuleElement = (Element) sourceModules.item(i);

				String moduleName = sourceModuleElement.getAttribute(Module.NAME_ATTRIBUTE);
				String version = sourceModuleElement.getAttribute(SourceModule.VERSION_ATTRIBUTE);
				String branch = sourceModuleElement.getAttribute(SourceModule.BRANCH_ATTRIBUTE);

				System.out.println(getInstance().getClass().getName() + " moduleName : " + moduleName);
				System.out.println(getInstance().getClass().getName() + " version : " + version);
				System.out.println(getInstance().getClass().getName() + " branch : " + branch);

				sourceModule = new SourceModule(moduleName);

				boolean added = duplicates.add(sourceModule);

				if (!added) {
					// Duplicate modules are not allowed
					//
					throw new ManifestException(ManifestException.DUPLICATE_MODULE_IN_MANIFEST);
				} else {

					duplicates.add(sourceModule);
				}
				manifest.addModule(sourceModule);
			}

			System.out.println(getInstance().getClass().getName() + " >>> 2");

			NodeList jarModules = manifestElement.getElementsByTagName("jarmodule");

			for (int i = 0; i < jarModules.getLength(); i++) {


			}

		} catch (Exception e) {

			e.printStackTrace();

		}

		return manifest;
	}

}
