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
package nl.toolforge.karma.core.boot;

import nl.toolforge.karma.core.ErrorCode;
import nl.toolforge.karma.core.location.Location;
import nl.toolforge.karma.core.location.LocationDescriptor;
import nl.toolforge.karma.core.location.LocationException;
import nl.toolforge.karma.core.location.LocationFactory;
import org.apache.commons.digester.Digester;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * <p>Configuration class for a <code>WorkingContext</code>. A <code>WorkingContextConfiguration</code> consists of the
 * following configuration:
 *
 * <ul>
 *   <li>Properties that can be used by Karma. Three properties are mandatory :
 *     <ul>
 *       <li>
 *         <code>project.baseDir</code>, indicating the directory where projects for a working context are stored.
 *       </li>
 *       <li>
 *         <code>manifest-store.module</code>, which is the module name for the manifest store in a version control
 *         system. Note that the format of the module name might be version control system dependent.
 *       </li>
 *       <li>
 *         <code>location-store.module</code>, which is the module name for the location store in a version control
 *         system. Note that the format of the module name might be version control system dependent.
 *       </li>
 *     </ul>
 *   <li/>
 *   <li>A manifest store <code>Location</code>, describing the version control system where the
 *       <code>manifest-store.module</code> module can be found.
 *   <li/>
 *   <li>A location store <code>Location</code>, describing the version control system where the
 *       <code>location-store.module</code> module can be found.
 *   <li/>
 * </ul>
 *
 * <p>The configuration is not loaded automatically. By calling the {@link #load()}-method the configuration will be
 * loaded.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public final class WorkingContextConfiguration {

  public static final ErrorCode CONFIGURATION_LOAD_ERROR = new ErrorCode("WCC-00001");

  private static Log logger = LogFactory.getLog(WorkingContextConfiguration.class);

  private File workingContextConfigurationFile = null;

  private Location manifestStoreLocation = null;
  private Location locationStoreLocation = null;

  private Properties configuration = null;

  private WorkingContext workingContext = null;



  /**
   * Creates a configuration object using <code>configFile</code> as the configuration file. The configuration is
   * loaded by calling {@link #load()}.
   *
   * @param configFile A configuration file.
   */
  public WorkingContextConfiguration(File configFile) {

    if (configFile == null) {
      throw new IllegalArgumentException("Configuration file cannot be null.");
    }
    workingContextConfigurationFile = configFile;
    configuration = new Properties();
  }

  private File getConfigFile() {
    return workingContextConfigurationFile;
  }

  public Location getManifestStoreLocation() {
    return manifestStoreLocation;
  }

  public void setManifestStoreLocation(Location store) {
    this.manifestStoreLocation = store;
  }

  public void setLocationStoreLocation(Location store) {
    this.locationStoreLocation = store;
  }

  public Location getLocationStoreLocation() {
    return locationStoreLocation;
  }



  /**
   *
   * @param key
   * @return <code>null</code> if the property is not found.
   */
  public String getProperty(String key) {

    if (configuration == null) {
      return null;
    }

    return configuration.getProperty(key);
  }



  /**
   * Adds or changes a property in the current configuration. When a property with key <code>name</code> exists, its
   * value is overwritten with <code>value</code>.
   */
  public void setProperty(String name, String value) {
    configuration.setProperty(name, value);
  }



  /**
   * Thorough checks of this configuration is valid, and if it is not, returns the <code>ErrorCode</code> to indicate
   * what went wrong or <code>null</code> if nothing went wrong, and this configuration is ready to use.
   *
   * @return An <code>ErrorCode</code> indicating the exact failure or <code>null</code> of nothing it wrong.
   */
  public ErrorCode check() {

    try {
      if (load()) {
        // ok
      }
    } catch (WorkingContextException e) {
      return CONFIGURATION_LOAD_ERROR;
    }

    return null;
  }





  /**
   * <p>Loads the configuration from <code>working-context.xml</code>. When the file did not exists, <code>false</code>
   * will be returned. Otherwise, this method returns <code>true</code> and the configuration succeeded. Note that
   * this method performs no validation on the configuration itself. This is left to the user.
   *
   * <p>When the configuration could be loaded, this method returns <code>true</code>. This is not to say that the
   * configuration is correct. The configuration should still be checked for correctness by the client.
   *
   * <p>Calling this method overwrites any properties already set for this configuration.
   *
   * @return                         <code>true</code> when the configuration could be loaded, <code>false</code> if it
   *                                 couldn't.
   *
   * @throws WorkingContextException When an <code>IOException</code> or <code>SAXException</code> occurs, indicating
   *                                 an error when reading a configuration file, which could result by the client in
   *                                 specific actions (thus the exception and not the <code>true</code> or
   *                                 <code>false</code>.
   */
  public boolean load() throws WorkingContextException {

    configuration = new Properties();

    // Read properties
    //
    Digester propertyDigester = getPropertyDigester();

    List properties = null;

    try {
      properties = (List) propertyDigester.parse(getConfigFile());
    } catch (SAXException e) {
      logger.error(e);
      throw new WorkingContextException("XML error in configuration for working context `" + workingContext + "`. ", e);
    } catch (IOException e) {
      logger.error(e);
      throw new WorkingContextException(e);
    }

    for (Iterator i = properties.iterator(); i.hasNext();) {
      Property property = (Property) i.next();
      configuration.put(property.getName(), property.getValue());
    }

    // Read manifests-store and location-store data
    //
    Digester locationDigester = LocationDescriptor.getDigester();

    List locations = null;

    try {
      locations = (List) locationDigester.parse(getConfigFile());
    } catch (SAXException e) {
      logger.error(e);
      throw new WorkingContextException("XML error in configuration for working context `" + workingContext + "`. ", e);
    } catch (IOException e) {
      logger.error(e);
      throw new WorkingContextException(e);
    }

    if (locations == null) {
      return false;
    }

    // Expecting exactly two items !!! Only 'manifest-store' and 'location-store' are accepted.
    //
    for (Iterator i = locations.iterator(); i.hasNext();) {

      LocationDescriptor descriptor = (LocationDescriptor) i.next();

      Location location = null;
      try {
        location = LocationFactory.getInstance().createLocation(descriptor);
      } catch (LocationException e) {
        return false;
      }

      if ("manifest-store".equals(location.getId())) {
        manifestStoreLocation = location;
//        manifestStoreLocation.setWorkingContext(workingContext);
      } else if ("location-store".equals(location.getId())) {
        locationStoreLocation = location;
//        locationStoreLocation.setWorkingContext(workingContext);
      } else {
        logger.error(
            "Invalid location element in `working-context.xml`. " +
            "Expecting a `manifest-store` and `location-store` entry.");
        return false;
      }
    }

    if (locationStoreLocation == null || manifestStoreLocation == null) {
      return false;
    }

    return true;
  }

  /**
   * Stores the all configuration items in <code>configuration</code> in the <code>working-context.xml</code> file.
   *
   * @throws WorkingContextException When storing failed.
   */
  public void store() throws WorkingContextException {

    // todo this implementation can be made more efficient, but it's ok for now.

    if (configuration == null) {
      throw new NullPointerException("Configuration cannot be null.");
    }

    // Check the (mandatory) configuration
    //

    StringBuffer buffer = new StringBuffer();
    buffer.append("<?xml version=\"1.0\"?>\n");

    buffer.append(
        "<wc:working-context\n" +
        "  xmlns:wc=\"http://www.toolforge.org/specifications/working-context\"\n" +
        "  xmlns:loc=\"http://www.toolforge.org/specifications/location\">\n");

    // <properties>-element
    //
    buffer.append("  <wc:properties>\n");

    MessageFormat formatter = new MessageFormat("    <wc:property name=\"{0}\" value=\"{1}\"/>\n");

    Enumeration e = configuration.propertyNames();

    while (e.hasMoreElements()) {

      String key = (String) e.nextElement();
      String value = configuration.getProperty(key);

      buffer.append(formatter.format(new String[]{key, value}));
    }

    buffer.append("  </wc:properties>\n\n");
    //
    // </properties>-element

    if (manifestStoreLocation == null && locationStoreLocation == null) {
      //
    } else {

      buffer.append("  <loc:locations>\n");

      if (manifestStoreLocation != null) {
        buffer.append(manifestStoreLocation.asXML());
      }
      if (locationStoreLocation != null) {
        buffer.append(locationStoreLocation.asXML());
      }

      buffer.append("  </loc:locations>\n");

    }

    buffer.append("\n");

    buffer.append("</wc:working-context>\n");

    // Write to file `working-context.xml`
    //
    try {
      if (!getConfigFile().exists()) {
        getConfigFile().createNewFile();
      }
      Writer writer = new BufferedWriter(new FileWriter(getConfigFile()));
      writer.write(buffer.toString());
      writer.flush();
    } catch (IOException ioe) {
      logger.error(ioe);
      throw new WorkingContextException(ioe);
    }
  }



  /*
  * The working-context configuration file is populated with elements from two different namespaces. One is the
  * working context stuff itself (all sorts of properties), the other is from the <code>location</code> namespace,
  * configuration for the manifest store and the location store.
  *
  * <p>This method gets a <code>Digester</code> for the general properties for the working context.
  */
  private Digester getPropertyDigester() {

    Digester digester = new Digester();

    digester.setNamespaceAware(true);

    digester.addObjectCreate("working-context/properties", ArrayList.class);

    digester.addObjectCreate("working-context/properties/property", Property.class);
    digester.addSetProperties("working-context/properties/property");
    digester.addSetNext("working-context/properties/property", "add");

    return digester;
  }
}
