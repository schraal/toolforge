package nl.toolforge.karma.core.location;

import nl.toolforge.core.util.file.XMLFilenameFilter;
import nl.toolforge.karma.core.prefs.Preferences;
import nl.toolforge.karma.core.vc.cvs.CVSLocationImpl;
import nl.toolforge.karma.core.vc.subversion.SubversionLocationImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.netbeans.lib.cvsclient.CVSRoot;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Map;

/**
 * Class to gain access to <code>Location</code> instances.
 *
 * @author D.A. Smedes
 * @author W.M. Oosterom
 * @version $Id$
 */
public final class LocationFactory {

	private static Log logger = LogFactory.getLog(LocationFactory.class);

	private static LocationFactory instance = null;

	private static Map locations = null;

	private LocationFactory() {
	}

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
	 * Loads all location xml files from the path specified by the {@link Preferences#LOCATION_STORE_DIRECTORY_PROPERTY)}
	 * property. Location objects are matched against authenticator objects, which should be available in the Karma
	 * configuration directory and should start with '<code>authentication</code>' and have an <code>xml</code>-extension.
	 *
	 * @throws LocationException
	 */
	public synchronized void load() throws LocationException {

		logger.debug("Loading locations directly from disk.");

		Preferences prefs = Preferences.getInstance();

		File base = new File(prefs.get(Preferences.LOCATION_STORE_DIRECTORY_PROPERTY));
		String[] files = base.list(new XMLFilenameFilter());

		// TODO I can check for files == null, but this could be checked when setting up the user environment during startup.
		if (files == null || files.length <= 0) {
			throw new LocationException(LocationException.NO_LOCATION_DATA_FOUND);
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

				// Load the first file. todo Later on, more than one file can be supported.
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

		} catch (Exception e) {
			throw new LocationException(LocationException.GENERAL_LOCATION_ERROR);
		}
	}

	/**
	 * Loads location objects and authenticator objects from an <code>InputStream</code>
	 */
	public synchronized void load(InputStream locationStream, InputStream authenticatorStream) throws LocationException {

		logger.debug("Loading locations from inputstream.");

		Document locationRoot = null;
		Document authenticatorRoot = null;

		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			locationRoot = builder.parse(locationStream);
			authenticatorRoot = builder.parse(authenticatorStream);
		} catch (Exception e) {
			throw new LocationException(LocationException.GENERAL_LOCATION_ERROR);
		}

		load(locationRoot, authenticatorRoot);
	}

	/**
	 * Parses the <code>root</code>-element and builds <code>Location</code> objects.
	 *
	 * @param locationRoot      The DOM for all location data.
	 * @param authenticatorRoot The DOM for all authentication data.
	 */
	private synchronized void load(Document locationRoot, Document authenticatorRoot) throws LocationException {

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

				// TODO refactor out, see 'includeAuthentication()'
				//
				cvsLocation = includeAuthentication(cvsLocation, authenticatorRoot);

				checkLocation(cvsLocation);

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

	//
	// TODO Read in the authentication stuff and process it ==> XPATH Stuff.
	//
	private CVSLocationImpl includeAuthentication(CVSLocationImpl location, Document authenticatorRoot) {

		NodeList authenticationElements = authenticatorRoot.getElementsByTagName("authenticator");

		for (int i = 0; i < authenticationElements.getLength(); i++) {
			Element authenticationElement = (Element) authenticationElements.item(i);
			String authenticatorId = authenticationElement.getAttribute("id");

			if (location.getId().equals(authenticatorId)) {
				// The first occurrence that applies is enough.
				//
				location.setUsername(authenticationElement.getAttribute("username"));
				location.setPassword(authenticationElement.getAttribute("password"));

				break;
			}
		}

		return location;
	}

	private void checkLocation(Location location) throws LocationException {

		if (location instanceof CVSLocationImpl) {

			CVSLocationImpl loc = (CVSLocationImpl) location;

			if (loc.getProtocol().equals(CVSRoot.METHOD_EXT)) {
				if (loc.getUsername() == null) {
					logger.debug("Connection protocol (" + CVSRoot.METHOD_EXT + ") requires username.");
					throw new LocationException(LocationException.LOCATION_CONFIGURATION_ERROR);
				}
//				if ((loc.getUsername() == null) || (!loc.passwordSet())) {
//					logger.debug("Connection protocol (" + CVSRoot.METHOD_EXT + ") requires username and password.");
//					throw new KarmaException(KarmaException.LOCATION_CONFIGURATION_ERROR);
//				}
			} else {

				if (loc.getProtocol().equals(CVSRoot.METHOD_PSERVER)) {
					if ((loc.getUsername() == null) || (!loc.passwordSet())) {
						logger.debug("Connection protocol (" + CVSRoot.METHOD_PSERVER + ") requires username and password.");
						throw new LocationException(LocationException.LOCATION_CONFIGURATION_ERROR);
					}
				} else {

					if (loc.getProtocol().equals(CVSRoot.METHOD_SERVER)) {
						if ((loc.getUsername() == null) || (!loc.passwordSet())) {
							logger.debug("Connection protocol (" + CVSRoot.METHOD_SERVER + ") requires username and password.");
							throw new LocationException(LocationException.LOCATION_CONFIGURATION_ERROR);
						}
					}
				}
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
	 * @throws LocationException See {@link LocationException#LOCATION_NOT_FOUND}.
	 */
	public final Location get(String locationAlias) throws LocationException {

		if (locations.containsKey(locationAlias)) {
			return (Location) locations.get(locationAlias);
		}
		throw new LocationException(LocationException.LOCATION_NOT_FOUND, new Object[]{locationAlias});
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
