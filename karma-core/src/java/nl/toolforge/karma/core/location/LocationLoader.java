/*
Karma core - Core of the Karma application
Copyright (C) 2004  Toolforge <www.toolforge.nl>

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/
package nl.toolforge.karma.core.location;

import nl.toolforge.karma.core.boot.WorkingContext;
import org.apache.commons.digester.Digester;
import org.apache.tools.ant.DirectoryScanner;
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
 * mandatory property in <code>karma.properties</code>.
 *
 * <p>A location can be of different types. Some locations require authenticator data, which is mapped from an XML file
 * in the Karma configuration directory (<code>uthenticator.xml</code>). A location with id <code>cvs-1</code> will be
 *  mapped to an authenticator with the same id.
 *
 * @author D.A. Smedes
 * @author W.M. Oosterom
 * @version $Id$
 */
public final class LocationLoader {

  private Map locations = null;
  private WorkingContext workingContext = null;

  /**
   * Constructs a LocationLoader for the current <code>workingContext</code>.
   * @param workingContext
   */
  public LocationLoader(WorkingContext workingContext) {
    this.workingContext = workingContext;
  }



  /**
   * Loads all location xml files from the path specified by the {@link WorkingContext#getLocationStore()}
   * property. Location objects are matched against authenticator objects, which should be available in the Karma
   * configuration directory {@link nl.toolforge.karma.core.boot.Karma#getConfigurationBaseDir()} and should be named
   * '<code>authenticators.xml</code>'.
   *
   * @throws LocationException
   */
  public synchronized void load() throws LocationException {

    locations = new Hashtable();

    // todo replace by LocationModule stuff.

    // Recurse over all xml files in the locations directory.
    //
    DirectoryScanner scanner = new DirectoryScanner();
    scanner.setBasedir(workingContext.getLocationStoreBasedir());
    scanner.setIncludes(new String[]{"**/*.xml"});
    scanner.scan();

    String[] files = scanner.getIncludedFiles();

    if (files == null || files.length <= 0) {
      throw new LocationException(LocationException.NO_LOCATION_DATA_FOUND);
    }

    for (int i = 0; i < files.length; i++) {

      Digester digester = LocationDescriptor.getDigester();

      List subList = null;
      try {
        subList = (List) digester.parse(new File(workingContext.getLocationStoreBasedir(), files[i]).getPath());
      } catch (IOException e) {
        throw new LocationException(e, LocationException.LOCATION_LOAD_ERROR);
      } catch (SAXException e) {
        e.printStackTrace();
        throw new LocationException(e, LocationException.LOCATION_LOAD_ERROR);
      }

      if (subList != null) {
        for (Iterator j = subList.iterator(); j.hasNext();) {

          LocationDescriptor d = (LocationDescriptor) j.next();

          if (locations.containsKey(d.getId())) {
            locations.remove(d.getId());
            throw new LocationException(
                LocationException.DUPLICATE_LOCATION_KEY,
                new Object[] {d.getId(), workingContext.getLocationStoreBasedir().getPath()}
            );
          }

          Location location = LocationFactory.getInstance().createLocation(d);
          location.setWorkingContext(workingContext);
          
          locations.put(location.getId(), location);
        }
      }
    }
  }

  /**
   * Returns all locations that have been loaded by the loader.
   *
   * @return A map containing <code>Location</code>-objects, accessible by their <code>id</code> as a key.
   */
  public final Map getLocations() {
    return locations;
  }

  /**
   * Gets a <code>Location</code> instance by its <code>locationAlias</code>. This method checks the availability of the
   * location as well throwing the corresponding errors when this did not succeed.
   *
   * @param  locationAlias      The <code>location</code>-attribute from the <code>module</code>-element
   *                            in the manifest.
   * @return A <code>Location</code> instance, representing e.g. a CVS repository or a Maven repository.
   * @throws LocationException See {@link LocationException#LOCATION_NOT_FOUND}.
   * @throws LocationException See {@link LocationException#CONNECTION_EXCEPTION}.
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
