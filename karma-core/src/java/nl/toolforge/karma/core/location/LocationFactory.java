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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
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

	/**
	 * <p>Loads authentication data from the <code>location-authentication.xml</code> in the karma configuration directory.
	 * The file should contain the following xml:
	 *
	 * <pre>
	 *
	 * &lt;authenticators&gt;
	 *   &lt;location type="cvs-repository" id="cvs-local"&gt;
	 *     &lt;username&gt;asmedes&lt;/username&gt;
	 *     &lt;password&gt;_9heyjji&lt;/password&gt;
	 *     &lt;protocol&gt;pserver&lt;/protocol&gt;
	 * &lt;/location&gt;
	 * &lt;/authenticators&gt;
	 *
	 * </pre>
	 *
	 * @throws KarmaException When the authentication data could not be loaded.
	 */
	public synchronized void load() throws KarmaException {

		Preferences prefs = Preferences.getInstance();

		try {
			load(
				new File(prefs.get(Preferences.LOCATION_STORE_DIRECTORY_PROPERTY)),
				prefs.getConfigurationDirectory().getPath() + File.separator + "location-authentication.xml"
			);
		} catch (KarmaException k) {
			logger.error(
				"Check if a location-authentication.xml is available in the directory " +
				"identified by the property 'karma.location-store.directory'");
			throw k;
		}
	}

	/**
	 * See {@link #load()}
	 *
	 * @param locationFilesPath
	 *        The path where all location XML files can be found.
	 * @param authenticationFilePath
	 *        The path to the location where <code>location-authentication.xml</code> can be found. The final "/" is not
	 *        required.
	 *
	 * @throws KarmaException See {@link #load()}
	 *
	 * TODO make sure that all xml files are validated before being used.
	 */
	public synchronized void load(File locationFilesPath, String authenticationFilePath) throws KarmaException {

		String[] files = locationFilesPath.list(new XMLFilenameFilter());

		if (files.length <= 0) {
           throw new KarmaException(KarmaException.NO_LOCATION_DATA_FOUND);
		}

		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

			// Parse all XML files into one Document instance
			//
			Document root =
				builder.parse(new File(locationFilesPath.getPath() + File.separator + files[0]));

			for (int i = 1; i < files.length; i++) {

				Document document = builder.parse(new File(locationFilesPath.getPath() + File.separator + files[i]));

				// Include one in the other
				//
				root = (Document) root.importNode(document.getDocumentElement(), true);
			}

			NodeList locationElements = root.getElementsByTagName("location");

			locations = new Hashtable();

			for (int i = 0; i < locationElements.getLength(); i++) {

				// Check all elements for their type and add them to the locationList
				//
				Element locationElement = (Element) locationElements.item(i);

				if (Location.Type.CVS_REPOSITORY.equals(locationElement.getAttribute("type").toUpperCase())) {
					CVSLocationImpl cvsLocation = new CVSLocationImpl(locationElement.getAttribute("id"));

					cvsLocation.setHost(locationElement.getElementsByTagName("host").item(0).getNodeValue());
					cvsLocation.setPort(new Integer(locationElement.getElementsByTagName("host").item(0).getNodeValue()).intValue());
					cvsLocation.setRepository(locationElement.getElementsByTagName("repository").item(0).getNodeValue());

					// TODO Read in the authentication stuff and process it
					//
					//cvsLocation.setUsername(authenticationElement.etElementsByTagName("username").item(0).getNodeValue());
					//cvsLocation.setPassword(authenticationElement.etElementsByTagName("password").item(0).getNodeValue());
					//cvsLocation.setProtocol(authenticationElement.etElementsByTagName("protocol").item(0).getNodeValue());

					locations.put(cvsLocation.getId(), cvsLocation);
				}

				if (Location.Type.SUBVERSION_REPOSITORY.equals(locationElement.getAttribute("type").toUpperCase())) {
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

//			Document authenticators = builder.parse(new File(authenticationFilePath));

			// TODO : match with authenticators

		}
		catch (Exception e) {
			throw new KarmaException(KarmaException.LAZY_BASTARD, e);
		}
	}

	public final Map getLocations() {
		return locations;
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
