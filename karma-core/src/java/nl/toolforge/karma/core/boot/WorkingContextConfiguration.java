package nl.toolforge.karma.core.boot;

import nl.toolforge.karma.core.KarmaRuntimeException;
import nl.toolforge.karma.core.location.Location;
import nl.toolforge.karma.core.location.LocationDescriptor;
import nl.toolforge.karma.core.location.LocationFactory;
import nl.toolforge.karma.core.location.LocationException;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Properties;
import java.util.Enumeration;

/**
 * Configuration class for a working context.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public final class WorkingContextConfiguration {

  private static Log logger = LogFactory.getLog(WorkingContextConfiguration.class);

  private File workingContextConfigurationFile = null;

  private Location manifestStore = null;
  private Location locationStore = null;

  private Properties configuration = null;

  private WorkingContext workingContext = null;

  public WorkingContextConfiguration(WorkingContext workingContext) {
    this.workingContext = workingContext;
    workingContextConfigurationFile = new File(workingContext.getWorkingContextConfigurationBaseDir(), "working-context.xml");
  }

  public Location getManifestStore() {
    return manifestStore;
  }

  public void setManifestStore(Location store) {
    this.manifestStore = store;
  }

  public void setLocationStore(Location store) {
    this.locationStore = store;
  }

  public Location getLocationStore() {
    return locationStore;
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
   * Loads the configuration from <code>working-context.xml</code>. When the file did not exists, <code>false</code>
   * will be returned. Otherwise, this method returns <code>true</code> and the configuration succeeded. Note that
   * this method performs no validation on the configuration itself. This is left to the user.
   *
   * @return <code>true</code> if the configuration was possible with a <code>working-context.xml</code> file, otherwise
   *         <code>false</code>.
   */
  public boolean load() {

    configuration = new Properties();

    // Read properties
    //
    Digester propertyDigester = getPropertyDigester();

    List properties = null;

    try {
      properties = (List) propertyDigester.parse(workingContextConfigurationFile);
    } catch (SAXException e) {
      logger.error(e);
      throw new KarmaRuntimeException(
          "XML error in configuration for working context `" + workingContext + "`. " +
          e.getMessage());
    } catch (IOException e) {
      logger.error(e);
      return false;
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
      locations = (List) locationDigester.parse(workingContextConfigurationFile);
    } catch (SAXException e) {
      logger.error(e);
      throw new KarmaRuntimeException(
          "XML error in configuration for working context `" + workingContext + "`. " +
          e.getMessage());
    } catch (IOException e) {
      logger.error(e);
      return false;
    }

    if (locations == null) {
      // None have been found.
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
        logger.error(e.getMessage());
        return false;
      }

      if ("manifest-store".equals(location.getId())) {
        manifestStore = location;
        manifestStore.setWorkingContext(workingContext);

        try {
          manifestStore.connect();
        } catch (LocationException e) {
          return false;
        }

      } else if ("location-store".equals(location.getId())) {
        locationStore = location;
        locationStore.setWorkingContext(workingContext);

        try {
          locationStore.connect();
        } catch (LocationException e) {
          return false;
        }

      } else {
        logger.error(
            "Invalid location element in `working-context.xml`. " +
            "Expecting a `manifest-store` and `location-store` entry.");
      }
    }

    if (locationStore == null || manifestStore == null) {
      return false; // need both
    }

    return true;
  }

  /**
   * Stores the all configuration items in <code>configuration</code> in the <code>working-context.xml</code> file.
   *
   * @throws IOException When storing failed.
   */
  public void store() throws IOException {

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

    if (manifestStore == null && locationStore == null) {
      //
    } else {

      buffer.append("  <loc:locations>\n");

      if (manifestStore != null) {
        buffer.append(manifestStore.asXML());
      }
      if (locationStore != null) {
        buffer.append(locationStore.asXML());
      }

      buffer.append("  </loc:locations>\n");

    }

    buffer.append("\n");

    buffer.append("</wc:working-context>\n");

    // Write to file `working-context.xml`
    //
    if (!workingContextConfigurationFile.exists()) {
      workingContextConfigurationFile.createNewFile();
    }
    Writer writer = new BufferedWriter(new FileWriter(workingContextConfigurationFile));
    writer.write(buffer.toString());
    writer.flush();
  }

  /**
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
