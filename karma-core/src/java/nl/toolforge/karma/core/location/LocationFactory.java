package nl.toolforge.karma.core.location;

import nl.toolforge.core.util.file.XMLFilenameFilter;
import nl.toolforge.karma.core.KarmaException;
import nl.toolforge.karma.core.prefs.Preferences;
import nl.toolforge.karma.core.vc.cvs.CVSLocationImpl;
import nl.toolforge.karma.core.vc.subversion.SubversionLocationImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Map;

/**
 * Class to gain access to <code>Location</code> instances.
 *
 * @author D.A. Smedes
 * @author W.M. Oosterom
 *
 * @version $Id$
 */
public final class LocationFactory {

	private static Log logger = LogFactory.getLog(LocationFactory.class);

	private static LocationFactory instance = null;

	private static Map locations = null;

	private LocationFactory() {}

	/**
	 * Gets the singleton instance of the location factory. This instance can be used to get access to all location
	 * objects.
	 *
	 * @return A location factory.
	 */
	public static LocationFactory getInstance() {

		if (instance == null) {
			instance = new LocationFactory();
		}
		return instance;
	}

	// Default : assumes that the karma configuration directory is present and loads location xml files from the
	// path denoted by the karma.location-store.directory
	//
	// public synchronized void load()


	// public synchronized void loadOne()



//	/**
//	 * <p>Loads authentication data from the <code>location-authentication.xml</code> in the karma configuration directory.
//	 * The file should contain the following xml:
//	 *
//	 * <pre>
//	 *
//	 * &lt;authenticators&gt;
//	 *   &lt;location type="cvs-repository" id="cvs-local"&gt;
//	 *     &lt;username&gt;asmedes&lt;/username&gt;
//	 *     &lt;password&gt;_9heyjji&lt;/password&gt;
//	 *     &lt;protocol&gt;pserver&lt;/protocol&gt;
//	 * &lt;/location&gt;
//	 * &lt;/authenticators&gt;
//	 *
//	 * </pre>
//	 *
//	 * @throws KarmaException When the authentication data could not be loaded.
//	 */

	/**
	 * Loads all location xml files from the path specified by the {@link Preferences#LOCATION_STORE_DIRECTORY_PROPERTY)}
	 * property. Location objects are matched against authenticator objects, which should be available in the Karma
   * configuration directory and should start with '<code>authentication</code>' and have an <code>xml</code>-extension.
	 *
	 * @throws KarmaException
	 */
	public synchronized void load() throws KarmaException {

		logger.debug("Loading locations directly from disk.");

		Preferences prefs = Preferences.getInstance();

		File base = new File(prefs.get(Preferences.LOCATION_STORE_DIRECTORY_PROPERTY));
		String[] files = base.list(new XMLFilenameFilter());

    // TODO I can check for files == null, but this could be checked when setting up the user environment during startup.
		if (files == null || files.length <= 0) {
			throw new KarmaException(KarmaException.NO_LOCATION_DATA_FOUND);
		}

		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

			// Load the first file.
			//
			Document locationRoot = builder.parse(new File(base.getPath() + File.separator + files[0]));

			// Load the rest of them
			//
			for (int i = 1; i < files.length; i++) {

				Document document = builder.parse(new File(base.getPath() + File.separator + files[i]));

				// Include one in the other
				//
				locationRoot = (Document) locationRoot.importNode(document.getDocumentElement(), true);
			}

      // Repeat this step for authenticator files.
      //

      files = prefs.getConfigurationDirectory().list(new AuthenticationFilenameFilter());

      Document authenticationRoot = null;

      if (files.length > 0) {

        // Load the first file.
        //
        authenticationRoot = builder.parse(new File(prefs.getConfigurationDirectory().getPath() + File.separator + files[0]));

        // Load the rest of them
        //
        for (int i = 1; i < files.length; i++) {

          Document document = builder.parse(new File(base.getPath() + File.separator + files[i]));

          // Include one in the other
          //
          authenticationRoot = (Document) authenticationRoot.importNode(document.getDocumentElement(), true);
        }
      } else {
        logger.info("No authentication files found in " + prefs.getConfigurationDirectory().getPath() + ".");
      }

			load(locationRoot, authenticationRoot);

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (FactoryConfigurationError factoryConfigurationError) {
			factoryConfigurationError.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Loads location objects and authenticator objects from an <code>InputStream</code>
	 */
	public synchronized void load(InputStream locationStream, InputStream authenticatorStream) throws KarmaException {

		logger.debug("Loading locations from inputstream.");

		Document locationRoot = null;
		Document authenticatorRoot = null;
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			locationRoot = builder.parse(locationStream);
      authenticatorRoot = builder.parse(authenticatorStream);
		} catch (Exception e) {
			throw new KarmaException(KarmaException.LAZY_BASTARD, e);
		}

		load(locationRoot, authenticatorRoot);
	}

	/**
	 * Parses the <code>root</code>-element and builds <code>Location</code> objects.
	 *
	 * @param locationRoot The DOM for all location data.
   * @param authenticatorRoot The DOM for all authentication data.
	 *
	 * @throws KarmaException
	 */
	private synchronized void load(Document locationRoot, Document authenticatorRoot) throws KarmaException {

		if (locations == null) {
			locations = new Hashtable();
		}

		NodeList locationElements = locationRoot.getElementsByTagName("location");

		for (int i = 0; i < locationElements.getLength(); i++) {

			// Check all elements for their type and add them to the locationList
			//
			Element locationElement = (Element) locationElements.item(i);

			if (Location.Type.CVS_REPOSITORY.type.equals(locationElement.getAttribute("type").toUpperCase())) {
				CVSLocationImpl cvsLocation = new CVSLocationImpl(locationElement.getAttribute("id"));

        cvsLocation.setProtocol(locationElement.getElementsByTagName("protocol").item(0).getFirstChild().getNodeValue());
				cvsLocation.setHost(locationElement.getElementsByTagName("host").item(0).getFirstChild().getNodeValue());
				cvsLocation.setPort(new Integer(locationElement.getElementsByTagName("port").item(0).getFirstChild().getNodeValue()).intValue());
				cvsLocation.setRepository(locationElement.getElementsByTagName("repository").item(0).getFirstChild().getNodeValue());


				// TODO Read in the authentication stuff and process it ==> XPATH Stuff.
				//
				//cvsLocation.setUsername(authenticationElement.etElementsByTagName("username").item(0).getNodeValue());
				//cvsLocation.setPassword(authenticationElement.etElementsByTagName("password").item(0).getNodeValue());

				locations.put(cvsLocation.getId(), cvsLocation);
			}

			if (Location.Type.SUBVERSION_REPOSITORY.type.equals(locationElement.getAttribute("type").toUpperCase())) {
				SubversionLocationImpl svLocation = new SubversionLocationImpl(locationElement.getAttribute("id"));

				// TODO should be implemented for Subversion

				locations.put(svLocation.getId(), svLocation);
			}

			if (Location.Type.MAVEN_REPOSITORY.equals(locationElement.getAttribute("type").toUpperCase())) {
				MavenRepositoryImpl mavenLocation = new MavenRepositoryImpl(locationElement.getAttribute("id"));

				// TODO should be implemented for Maven

				locations.put(mavenLocation.getId(), mavenLocation);
			}
		}
	}

	public final Map getLocations() {
		return locations;
	}

	/**
	 * Gets a <code>Location</code> instance by its <code>locationAlias</code>.
	 *
	 * @param locationAlias The <code>location</code>-attribute from the <code>module</code>-element in the manifest.
	 * @return A <code>Location</code> instance, representing e.g. a CVS repository or a Maven repository.
	 * @throws KarmaException See {@link KarmaException#LOCATION_NOT_FOUND}.
	 */
	public final Location get(String locationAlias) throws KarmaException {

		if (locations.containsKey(locationAlias)) {
			return (Location) locations.get(locationAlias);
		}
		throw new KarmaException(KarmaException.LOCATION_NOT_FOUND, new Object[]{locationAlias});
	}

	/**
	 * String representation of all locations.
	 *
	 * @return <code>null</code> until implemented.
	 */
	public String toString() {
		return locations.toString();
	}
}
