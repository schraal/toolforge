package nl.toolforge.karma.core.location;

import nl.toolforge.core.util.file.XMLFilenameFilter;
import nl.toolforge.karma.core.KarmaRuntimeException;
import nl.toolforge.karma.core.LocalEnvironment;
import nl.toolforge.karma.core.KarmaException;
import nl.toolforge.karma.core.vc.cvs.CVSLocationImpl;
import nl.toolforge.karma.core.vc.subversion.SubversionLocationImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.netbeans.lib.cvsclient.CVSRoot;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Map;

/**
 * <p>Factory class to gain access to <code>Location</code> instances. Location instances are created based on XML
 * definitions in the location store. The location store is a directory on the user's local harddisk, which is a
 * mandatory property in <code>karma.properties</code>. See {@link LocalEnvironment} for more information.
 * <p/>
 * <p>A location can be of different types. Some locations require authenticator data, which is mapped from XML files
 * in the Karma configuration directory. The filename pattern used for authenticator files is
 * <code>repository-authenticator*.xml</code>. A location with id <code>cvs-1</code> will be mapped to an authenticator
 * with the same id.
 *
 * @author D.A. Smedes
 * @author W.M. Oosterom
 * @version $Id$
 */
public final class LocationFactory {

  private static Log logger = LogFactory.getLog(LocationFactory.class);

  private static LocalEnvironment env = null; // Reference to the local environment.
  private static LocationFactory instance = null;

  private static Map locations = null;

  private LocationFactory() {
  }

  /**
   * Gets the singleton instance of the location factory. This instance can be used to get access to all location
   * objects. This method expects an initialized {@link LocalEnvironment}.
   *
   * @return A location factory.
   */
  public static LocationFactory getInstance(LocalEnvironment localEnvironment) {

    if (instance == null) {
      instance = new LocationFactory();
      env = localEnvironment;
    }
    return instance;
  }

  /**
   * Gets the singleton instance of the location factory. An instance obtained through this method cannot load locations
   * from the file system. Use {@link #getInstance(nl.toolforge.karma.core.LocalEnvironment)} instead.
   *
   * @return A location factory.
   */
  public static LocationFactory getInstance() {
    return getInstance(null);
  }

  /**
   * Loads all location xml files from the path specified by the {@link LocalEnvironment#LOCATION_STORE_DIRECTORY}
   * property. Location objects are matched against authenticator objects, which should be available in the Karma
   * configuration directory and should start with '<code>authentication</code>' and have an <code>xml</code>-extension.
   *
   * @throws LocationException
   */
  public synchronized void load() throws LocationException {

    /*

    Digester digester = DigesterLoader.parser("location-rules.xml");
    create Location classes via LocationCreationFactory (to enable flexible typing)
    put each Location in the location map

    for each 'authenticator-*.xml' do
    Digester digester = DigesterLoader.parser("authenticator-X.xml");
    create Authenticator classes
    search for a Location instance in the location map (search by id).
    link the Authenticator to the Location instance
    end do

    */

    if (env == null) {
      throw new KarmaRuntimeException("Local environment is not initialized. Use 'getInstance(LocalEnvironment)'.");
    }

    logger.debug("Loading locations directly from disk.");

//		Preferences prefs = Preferences.getInstance();

    File base = null;
    try {
      base = env.getLocationStore();
    } catch (KarmaException e) {
      throw new LocationException(e, LocationException.NO_LOCATION_DATA_FOUND);
    }
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

      files = env.getConfigurationDirectory().list(new AuthenticationFilenameFilter());

      Document authenticationRoot = null;

      if (files.length > 0) {

        // Load the first file. todo Later on, more than one file can be supported.
        //
        authenticationRoot = builder.parse(new File(env.getConfigurationDirectory(), files[0]));

        // Load the rest of them
        //
        for (int i = 1; i < files.length; i++) {

          Document document = builder.parse(new File(base.getPath() + File.separator + files[i]));

          // Include one in the other
          //
          authenticationRoot = (Document) authenticationRoot.importNode(document.getDocumentElement(), true);
        }
      } else {
        logger.info("No authentication files found in " + env.getConfigurationDirectory().getPath() + ".");
        //throw new LocationException(LocationException.MISSING_AUTHENTICATOR_CONFIGURATION);
      }

      load(locationRoot, authenticationRoot);

    } catch (ParserConfigurationException e) {
      throw new LocationException(e, LocationException.MISSING_AUTHENTICATOR_CONFIGURATION);
    } catch (SAXException e) {
      throw new LocationException(e, LocationException.MISSING_AUTHENTICATOR_CONFIGURATION);
    } catch (IOException e) {
      throw new LocationException(e, LocationException.MISSING_AUTHENTICATOR_CONFIGURATION);
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
    } catch (ParserConfigurationException e) {
      throw new LocationException(LocationException.LOCATION_CONFIGURATION_ERROR);
    } catch (SAXException e) {
      throw new LocationException(LocationException.LOCATION_CONFIGURATION_ERROR);
    } catch (IOException e) {
      throw new LocationException(LocationException.LOCATION_CONFIGURATION_ERROR);
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

    // todo replace with Digester code and use a factory create the correct implementation
    //

    NodeList locationElements = locationRoot.getElementsByTagName("location");

    for (int i = 0; i < locationElements.getLength(); i++) {

      // Check all elements for their type and add them to the locationList
      //
      Element locationElement = (Element) locationElements.item(i);

      if (Location.Type.CVS_REPOSITORY.type.equals(locationElement.getAttribute("type").toUpperCase())) {
        CVSLocationImpl cvsLocation = new CVSLocationImpl(locationElement.getAttribute("id"));

        try {
          cvsLocation.setProtocol(locationElement.getElementsByTagName("protocol").item(0).getFirstChild().getNodeValue());
        } catch (NullPointerException ne) {
          throw new LocationException(LocationException.MISSING_LOCATION_PROPERTY, new Object[]{"protocol"});
        }
        try {
          cvsLocation.setHost(locationElement.getElementsByTagName("host").item(0).getFirstChild().getNodeValue());
        } catch (NullPointerException ne) {
          throw new LocationException(LocationException.MISSING_LOCATION_PROPERTY, new Object[]{"host"});
        }
        try {
          cvsLocation.setPort(new Integer(locationElement.getElementsByTagName("port").item(0).getFirstChild().getNodeValue()).intValue());
        } catch (NullPointerException ne) {
          throw new LocationException(LocationException.MISSING_LOCATION_PROPERTY, new Object[]{"port"});
        }
        try {
          cvsLocation.setRepository(locationElement.getElementsByTagName("repository").item(0).getFirstChild().getNodeValue());
        } catch (NullPointerException ne) {
          throw new LocationException(LocationException.MISSING_LOCATION_PROPERTY, new Object[]{"repository"});
        }

        // TODO refactor out, see 'includeAuthentication()'
        //
        if (authenticatorRoot != null) {
          int a = 1;
          cvsLocation = includeAuthentication(cvsLocation, authenticatorRoot);

          //checkLocation(cvsLocation);
        }
        locations.put(cvsLocation.getId(), cvsLocation);
//        }
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
          String error = "Connection protocol (" + CVSRoot.METHOD_EXT + ") requires username in repository-authenticators.xml";
          logger.error(error);
          throw new LocationException(LocationException.INVALID_AUTHENTICATOR_CONFIGURATION, new Object[]{loc.getId()});
        }
      } else {

        if (loc.getProtocol().equals(CVSRoot.METHOD_PSERVER)) {
          if ((loc.getUsername() == null) || (!loc.passwordSet())) {
            String error = "Connection protocol (" + CVSRoot.METHOD_PSERVER + ") requires username and password in repository-authenticators.xml";
            logger.error(error);
            throw new LocationException(LocationException.INVALID_AUTHENTICATOR_CONFIGURATION, new Object[]{loc.getId()});
          }
        } else {

          if (loc.getProtocol().equals(CVSRoot.METHOD_SERVER)) {
            if ((loc.getUsername() == null) || (!loc.passwordSet())) {
              String error = "Connection protocol (" + CVSRoot.METHOD_SERVER + ") requires username and password in repository-authenticators.xml";
              logger.error(error);
              throw new LocationException(LocationException.INVALID_AUTHENTICATOR_CONFIGURATION, new Object[]{loc.getId()});
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

//    logger.debug(locations.toString());

    if (locations.containsKey(locationAlias)) {

      Location location = (Location) locations.get(locationAlias);
      checkLocation(location); // checks if authentication data is available if need be.

      return location;
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
