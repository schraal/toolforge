package nl.toolforge.karma.core.location;

import nl.toolforge.core.util.file.XMLFilenameFilter;
import nl.toolforge.karma.core.KarmaException;
import nl.toolforge.karma.core.KarmaRuntimeException;
import nl.toolforge.karma.core.LocalEnvironment;
import nl.toolforge.karma.core.vc.cvs.CVSLocationImpl;
import nl.toolforge.karma.core.vc.subversion.SubversionLocationImpl;
import org.apache.commons.digester.Digester;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.netbeans.lib.cvsclient.CVSRoot;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * <p>Loader class to load all gain access to <code>Location</code> instances. Location instances are created based on XML
 * definitions in the location store. The location store is a directory on the user's local harddisk, which is a
 * mandatory property in <code>karma.properties</code>. See {@link nl.toolforge.karma.core.LocalEnvironment} for more information.
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
public final class LocationLoader {

  private static Log logger = LogFactory.getLog(LocationLoader.class);

  private static LocalEnvironment env = null; // Reference to the local environment.
  private static LocationLoader instance = null;

  private static Map locations = null;

  private static File locationBase = null;
  private static File authenticationBase = null;

  private LocationLoader() {
  }

  /**
   * Gets the singleton instance of the location factory. This instance can be used to get access to all location
   * objects. This method expects an initialized {@link nl.toolforge.karma.core.LocalEnvironment}.
   *
   * @return A location factory.
   */
  public static LocationLoader getInstance(LocalEnvironment localEnvironment) throws LocationException {

    if (instance == null) {
      instance = new LocationLoader();

      if (localEnvironment != null) {

        env = localEnvironment;

        try {
          locationBase = env.getLocationStore();
        } catch (KarmaException e) {
          throw new LocationException(e, LocationException.NO_LOCATION_DATA_FOUND);
        }
        authenticationBase = env.getConfigurationDirectory();
      }
    }
    return instance;
  }

  /**
   * Gets the singleton instance of the location factory. An instance obtained through this method cannot load locations
   * from the file system. Use {@link #getInstance(nl.toolforge.karma.core.LocalEnvironment)} instead.
   *
   * @return A location factory.
   */
  public static LocationLoader getInstance() throws LocationException {
    return getInstance(null);
  }

  /**
   * Loads all location xml files from the path specified by the {@link nl.toolforge.karma.core.LocalEnvironment#LOCATION_STORE_DIRECTORY}
   * property. Location objects are matched against authenticator objects, which should be available in the Karma
   * configuration directory and should start with '<code>authentication</code>' and have an <code>xml</code>-extension.
   *
   * @throws LocationException
   */
  public synchronized void load() throws LocationException {

    if (locationBase == null) {
      throw new KarmaRuntimeException("Local environment is not initialized. Use 'getInstance(LocalEnvironment)'.");
    }

    locations = new Hashtable();

    Map authenticators = new Hashtable();

    File authenticatorsFile = new File(authenticationBase, "authenticators.xml");

    if (authenticatorsFile == null) {
      throw new LocationException(LocationException.MISSING_AUTHENTICATOR_CONFIGURATION);
    }

    // Create a list of authenticators, anything you can find.
    //
    Digester digester = new Digester();
    digester.addObjectCreate("authenticators", "java.util.ArrayList");
    digester.addObjectCreate("authenticators/authenticator", "nl.toolforge.karma.core.location.AuthenticatorDescriptor");
    digester.addSetProperties("authenticators/authenticator");
    digester.addSetNext("authenticators/authenticator", "add");

    List subList = null;
    try {
      subList = (List) digester.parse(authenticatorsFile.getPath());
    } catch (IOException e) {
      throw new LocationException(e, LocationException.MISSING_AUTHENTICATOR_CONFIGURATION);
    } catch (SAXException e) {
      throw new LocationException(e, LocationException.AUTHENTICATOR_LOAD_ERROR);
    }

    for (Iterator j = subList.iterator(); j.hasNext();) {
      AuthenticatorDescriptor authDescriptor = (AuthenticatorDescriptor) j.next();
      if (authenticators.containsKey(authDescriptor.getId())) {
        throw new LocationException(LocationException.DUPLICATE_AUTHENTICATOR_KEY, new Object[] {authDescriptor.getId()});
      }
      authenticators.put(authDescriptor.getId(), authDescriptor);
    }

    // Recurse over all xml files in the locations directory.
    //

    String[] files = locationBase.list(new XMLFilenameFilter());

    if (files == null || files.length <= 0) {
      throw new LocationException(LocationException.NO_LOCATION_DATA_FOUND);
    }

    for (int i = 0; i < files.length; i++) {

      digester = new Digester();
      digester.addObjectCreate("locations", "java.util.ArrayList");
      digester.addObjectCreate("locations/location", "nl.toolforge.karma.core.location.LocationDescriptor");
      digester.addSetProperties("locations/location");
      digester.addCallMethod("locations/location/protocol", "setProtocol", 0);
      digester.addCallMethod("locations/location/host", "setHost", 0);
      digester.addCallMethod("locations/location/port", "setPort", 0);
      digester.addCallMethod("locations/location/repository", "setRepository", 0);
      digester.addSetNext("locations/location", "add");

//      subList = null;
      try {
        subList = (List) digester.parse(new File(locationBase, files[i]).getPath());
      } catch (IOException e) {
        e.printStackTrace();
      } catch (SAXException e) {
        e.printStackTrace();
      }

      if (subList != null) {
        for (Iterator j = subList.iterator(); j.hasNext();) {

          LocationDescriptor d = (LocationDescriptor) j.next();
          if (locations.containsKey(d.getId())) {
            throw new LocationException(LocationException.DUPLICATE_LOCATION_KEY, new Object[] {d.getId()});
          }

          // Get the authenticator
          //
          AuthenticatorDescriptor authDescriptor = (AuthenticatorDescriptor) authenticators.get(d.getId());

          if (authDescriptor == null) {
            throw new LocationException(LocationException.MISSING_AUTHENTICATOR_CONFIGURATION, new Object[]{d.getId()});
          }
          locations.put(d.getId(), getLocation(d, authDescriptor));
        }
      }
    }
  }

  /**
   * Loads location objects and authenticator objects from a specified <code>File</code> location. Can be used for unit
   * tests as well. Directories for locations and authentication files are the same.
   */
  public synchronized void load(File dir) throws LocationException {

    locationBase = dir;
    authenticationBase = dir;

    load();
  }

  private Location getLocation(LocationDescriptor locDescriptor, AuthenticatorDescriptor authDescriptor) {

    if (Location.Type.CVS_REPOSITORY.type.equals(locDescriptor.getType().toUpperCase())) {

      CVSLocationImpl cvsLocation = new CVSLocationImpl(locDescriptor.getType());

      cvsLocation.setProtocol(locDescriptor.getProtocol());
      cvsLocation.setHost(locDescriptor.getHost());
      cvsLocation.setPort(new Integer(locDescriptor.getPort()).intValue());
      cvsLocation.setRepository(locDescriptor.getRepository());

      cvsLocation.setUsername(authDescriptor.getUsername());
      cvsLocation.setPassword(authDescriptor.getPassword());

      return cvsLocation;

    } else if (Location.Type.SUBVERSION_REPOSITORY.type.equals(locDescriptor.getType().toUpperCase())) {
      return new SubversionLocationImpl(locDescriptor.getType());
    } else {

// todo tricky
//
      return null;
    }
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
