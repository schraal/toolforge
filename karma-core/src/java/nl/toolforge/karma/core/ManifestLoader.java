package nl.toolforge.karma.core;

import nl.toolforge.core.util.file.XMLFilenameFilter;
import nl.toolforge.karma.core.location.Location;
import nl.toolforge.karma.core.location.LocationFactory;
import nl.toolforge.karma.core.prefs.Preferences;
import nl.toolforge.karma.core.prefs.UnavailableValueException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.HashSet;
import java.util.Set;

/**
 * <p>The manifest loader is responsible for loading a manifest from disk in memory. Manifests are stored on disk in
 * a directory identified by the property <code>manifest.dir</code>.
 *
 * @author D.A. Smedes
 *
 * @version $Id$
 */
public final class ManifestLoader {

	private static Log logger = LogFactory.getLog(ManifestLoader.class);

	private static ManifestLoader instance = null;

	//private Manifest currentManifest = null;

	private static Preferences prefs = Preferences.getInstance();
	private static ClassLoader classLoader = null;
	private static String resourceDir = null;

	public synchronized static ManifestLoader getInstance() {
		if (instance == null) {
			instance = new ManifestLoader();
		}
		return instance;
	}

	/**
	 * Returns a set with all manifest names, as they could be located in the manifest store directory.
	 *
	 * @return All manifests in the manifest store directory.
	 *
	 * @throws ManifestException
	 */
	public final Set getAll() throws ManifestException {

		Set all = new HashSet();

		String[] names = prefs.getManifestStore().list(new XMLFilenameFilter());

		for (int i = 0; i < names.length; i++) {
			all.add(names[i]);
		}

		return all;
	}

	/**
	 * When a manifest has been used before, this method will try and load it. A property
	 * <code>manifest.saved.id</code> in the <code>karma.properties</code> is used as the identifier for the manifest
	 * name.
	 *
	 * @return The <code>Manifest</code> that was restored based on the {@link Preferences#MANIFEST_HISTORY_PROPERTY} or
	 *         <code>null</code> when no history was defined.
	 *
	 * @throws ManifestException See {@link ManifestException#MANIFEST_LOAD_ERROR}
	 */
	public final Manifest loadFromHistory() throws ManifestException {

		String historyId = null;

		try {
			historyId = prefs.get(Preferences.MANIFEST_HISTORY_PROPERTY);
		} catch (UnavailableValueException u) {
			// TODO : logger.debug("No history available for manifest. Returning null.);
			// No history property available. Fine, we'll just return nothing.
			//
			return null;
		}

		return load(historyId);
	}

	/**
	 * Loads a manifest and all included manifests using the <code>loader</code> classloader.
	 *
	 * @param id The id of the manifest, represented as a a filename with or without the <code>xml</code> extension.
	 * @param loader A classloader where manifest-files are available as resources.
	 * @param dir The location path (directory, relative to the <code>resources</code> directory in the classpath, e.g.
	 *            <code>/test</code>) where resources can be found. This property is used by <code>loader</code> to
	 *            retrieve manifest files.
	 *
	 * @return A <code>Manifest</code> implementation. See {@link ManifestImpl}.
	 *
	 * @throws ManifestException See {@link ManifestException#MANIFEST_LOAD_ERROR}
	 */
	public final Manifest load(String id, ClassLoader loader, String dir) throws ManifestException {

		classLoader = loader;
		resourceDir = dir;

		return load(id);
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
	 * @throws ManifestException See {@link ManifestException#MANIFEST_LOAD_ERROR}.
	 */
	public final Manifest load(String id) throws ManifestException {

		Manifest manifest = null;

		// Keeps track of all manifests that have been loaded. Prevents recursive calls
		//
		Set duplicates = new HashSet();

		// TODO We're assuming that basic validation has been done by a DTD or XML Schema
		//
		try {

			// TODO the following two statements can disappear when the <name>-attribute for a Manifest disappears
			//
			DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document manifestDocument = documentBuilder.parse(getManifestFile(id));

			logger.debug("Retrieved manifest file to obtain its <name>-attribute.");

			// Create the actual Manifest instance
			//
			String manifestName = manifestDocument.getDocumentElement().getAttribute(Manifest.NAME_ATTRIBUTE);
			manifest = new ManifestImpl(manifestName);

			// (Recursively add manifests and included manifests
			//
			getInstance().add(duplicates, manifest, manifestName);

		} catch (ParserConfigurationException p) {
			throw new ManifestException(ManifestException.MANIFEST_LOAD_ERROR, new Object[]{id}, p);
		} catch (SAXException s) {
			throw new ManifestException(ManifestException.MANIFEST_LOAD_ERROR, new Object[]{id}, s);
		} catch (IOException i) {
			throw new ManifestException(ManifestException.MANIFEST_LOAD_ERROR, new Object[]{id}, i);
		}

		return manifest;
	}

	/**
	 * Recursively adds modules from manifests to a manifest.
	 */
	private synchronized void add(Set duplicates, Manifest manifest, String id) throws ManifestException {

		// Check if included manifest has already been loaded, to prevent looping
		//
		if (duplicates.contains(id)) {
			throw new ManifestException(ManifestException.MANIFEST_LOAD_RECURSION);
		}

		duplicates.add(id);

		logger.debug("Loading modules from manifest file " + id);

		try {

			DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document manifestDocument = documentBuilder.parse(getManifestFile(id));

			// Get all Element nodes, which are either <module> or <include-manifest> tags
			//
			NodeList moduleElements = manifestDocument.getDocumentElement().getElementsByTagName("*");

			for (int i = 0; i < moduleElements.getLength(); i++) {

				Element node = (Element) moduleElements.item(i);

				if (node.getNodeName().equals(Module.DESCRIPTION_ATTRIBUTE)) {
					// Ignore
					//

					// TODO Maybe later .... assign to description attribute.
					//

				} else {

					// Mandatory fields
					//

          String nodeName = node.getNodeName();

					if (nodeName.equals(SourceModule.ELEMENT_NAME)) {

						String moduleName = node.getAttribute(Module.NAME_ATTRIBUTE);
						Location location = LocationFactory.getInstance().get(node.getAttribute(Module.LOCATION_ATTRIBUTE));

						// SourceModule specific fields
						//
						String version = node.getAttribute(SourceModule.VERSION_ATTRIBUTE);

						SourceModule sourceModule;
						if (version == null) {
							sourceModule = new SourceModule(moduleName, location);
						} else {
							sourceModule = new SourceModule(moduleName, location, version);
						}
						//sourceModule.setBranch(node.getAttribute(SourceModule.BRANCH_ATTRIBUTE));

						manifest.addModule(sourceModule);

					}

					if (nodeName.equals(JarModule.ELEMENT_NAME)) {

						String moduleName = node.getAttribute(Module.NAME_ATTRIBUTE);
						Location location = LocationFactory.getInstance().get(node.getAttribute(Module.LOCATION_ATTRIBUTE));

						String version = node.getAttribute(JarModule.VERSION_ATTRIBUTE);

						JarModule jarModule;
						if (version == null) {
							jarModule = new JarModule(moduleName, location);
						} else {
							jarModule = new JarModule(moduleName, location, version);
						}

						manifest.addModule(jarModule);

					}

					if (nodeName.equals(Module.INCLUDE_ELEMENT_NAME)) {
						// Recursive call
						//
						add(duplicates, manifest, node.getAttribute(Module.INCLUDE_NAME_ATTRIBUTE));
					}
				}
			}
        } catch (ManifestException me) {
            me.printStackTrace();
            throw me;
        } catch (KarmaException ke) {
            ke.printStackTrace();
            throw new ManifestException(ManifestException.MANIFEST_LOAD_ERROR, new Object[]{id}, ke);
		} catch (ParserConfigurationException p) {
			throw new ManifestException(ManifestException.MANIFEST_LOAD_ERROR, new Object[]{id}, p);
		} catch (SAXException s) {
			throw new ManifestException(ManifestException.MANIFEST_LOAD_ERROR, new Object[]{id}, s);
		} catch (IOException i) {
			throw new ManifestException(ManifestException.MANIFEST_LOAD_ERROR, new Object[]{id}, i);
		}
	}

	// Strips a path and uses it for future reference.
	//
	private String path = "";

	/**
	 * Local helper method to get the manifest file from the correct resource path.
	 */
	private InputStream getManifestFile(String id) throws ManifestException {

		try {
			String fileName = (id.endsWith(".xml") ? id : id.concat(".xml"));

			if (fileName.endsWith(File.separator)) {
				fileName = fileName.substring(0, fileName.length() - 1);
			}
			if (fileName.lastIndexOf(File.separator) > 0) {
				path = fileName.substring(0, fileName.lastIndexOf(File.separator));
			}
			fileName = fileName.substring(fileName.lastIndexOf(File.separator) + 1);

			if (classLoader == null) {
				logger.debug("Loading manifest " + fileName + " from file-system ...");
				return new FileInputStream(prefs.getManifestStore().getPath() + File.separator + fileName);
			} else {
				logger.debug("Loading manifest " + fileName + " from classpath ...");

				InputStream inputStream = classLoader.getResourceAsStream(path + File.separator + fileName);

				if (inputStream == null) {
					throw new ManifestException(ManifestException.MANIFEST_FILE_NOT_FOUND, new Object[]{id});
				}

				return inputStream;
			}
		} catch (FileNotFoundException f) {
			throw new ManifestException(ManifestException.MANIFEST_FILE_NOT_FOUND, new Object[]{id}, f);
		} catch (NullPointerException n) {
			throw new ManifestException(ManifestException.MANIFEST_FILE_NOT_FOUND, new Object[]{id}, n);
		}
	}

}
