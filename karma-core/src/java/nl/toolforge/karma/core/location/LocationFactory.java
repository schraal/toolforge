package nl.toolforge.karma.core.location;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import java.io.File;
import java.util.*;

import nl.toolforge.core.util.file.XMLFilenameFilter;
import nl.toolforge.karma.core.vc.cvs.CVSLocationImpl;
import nl.toolforge.karma.core.vc.subversion.SubversionLocationImpl;
import nl.toolforge.karma.core.KarmaException;
import nl.toolforge.karma.core.UserEnvironment;
import nl.toolforge.karma.core.prefs.Preferences;

/**
 * Class to gain access to <code>Location</code> instances.
 *
 * @author D.A. Smedes
 * @author W.M. Oosterom
 */
public final class LocationFactory {

	private static LocationFactory instance = null;

	private static Map locations = null;

	private LocationFactory() {}

	public static LocationFactory getInstance() {

		if (instance == null) {
			instance = new LocationFactory();
		}
		return instance;
	}

	public synchronized void load() throws KarmaException {

		Preferences prefs = Preferences.getInstance();

		load(
			new File(prefs.get(UserEnvironment.LOCATION_STORE_DIRECTORY_PROPERTY)),
			UserEnvironment.getConfigurationDirectory().getPath() + File.separator + "location-authentication.xml"

		);
	}

	/**
	 * Uses all <code>xml</code> files in the directory identified by the
	 * {@link nl.toolforge.karma.core.UserEnvironment#LOCATION_STORE_DIRECTORY_PROPERTY} property.
	 *
	 * @param locationFilesPath
	 *        The path where all location XML files can be found.
	 * @param authenticationFilePath
	 *        The path to the location where <code>location-authentication.xml</code> can be found. The final "/" is not
	 *        required.
	 *
	 * TODO make sure that all xml files are validated before being used.
	 */
	public synchronized void load(File locationFilesPath, String authenticationFilePath) throws KarmaException {

		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();


			String[] files = locationFilesPath.list(new XMLFilenameFilter());

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

			Document authenticators =
				builder.parse(new File(authenticationFilePath + File.separator + "location-authentication.xml"));

			NodeList locationElements = root.getElementsByTagName("location");

            locations = new Hashtable();

			for (int i = 0; i < locationElements.getLength(); i++) {

                // Check all elements for their type and add them to the locationList
				//
                Element locationElement = (Element) locationElements.item(i);
                Location location = null;

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

			// We are going to perform a validation for each repository, to see whether to keep it
			//
//			StringBuffer reposBuffer = new StringBuffer();
//			for (Iterator i = allRepositories.entrySet().iterator(); i.hasNext(); ) {
//				Repository repository = (Repository) ((Map.Entry) i.next()).getValue();
//				if (!repository.isValid()) {
//					log.debug("Repository " + repository.getName() + " is not valid.");
//					i.remove();
//				} else {
//					reposBuffer.append(repository.getName());
//					if (i.hasNext()) reposBuffer.append(", ");
//				}
//			}

//			log.info("Repositories have been initialized : " + reposBuffer.toString());
//
//			// If there is only one repository, we make it the default repository
//			// Otherwise we try to set the original defaultRepository found in the Preferences (if it still exists)
//			//
//			if (allRepositories.size() == 1) {
//				setDefaultRepository( ((Repository)((Map.Entry) allRepositories.entrySet().iterator().next()).getValue()).getName());
//			} else {
//				String previousDefaultRepository = Preferences.getInstance().get("karma.repository.default", null);
//				if (previousDefaultRepository != null) {
//					setDefaultRepository(previousDefaultRepository);
//				}
//			}

		}
		catch (Exception e) {
			// Of course this is serious, we had an error in creating our repositories, we should throw
			// a real high prio exception
			//
			// TODO have a proper exception ErrorCode over here,

			e.printStackTrace();

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
