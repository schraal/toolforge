package nl.toolforge.karma.core.location;

import org.apache.commons.digester.Digester;

import java.util.ArrayList;

/**
 * Helper class to be able to read locations from an xml structure. This class is capable to handle all formats.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public class LocationDescriptor {

  private String id = null;
  private String type = null;

  private String host = null;
  private String port = null;
  private String repository = null;
  private String moduleOffset = null;
  private String protocol = null;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public String getPort() {
    return port;
  }

  public void setPort(String port) {
    this.port = port;
  }

  public String getRepository() {
    return repository;
  }

  public void setRepository(String repository) {
    this.repository = repository;
  }

  public String getModuleOffset() {
    return moduleOffset;
  }

  public void setModuleOffset(String moduleOffset) {
    this.moduleOffset = moduleOffset;
  }

  public String getProtocol() {
    return protocol;
  }

  public void setProtocol(String protocol) {
    this.protocol = protocol;
  }

  /**
   * Creates a <code>Digester</code> to read xml structures for locations. This digester will create an
   * <code>ArrayList</code> of <code>LocationDescriptor</code> instances.
   *
   * @return A digester instance
   */
  public static Digester getDigester() {

    Digester digester = new Digester();

    digester.setNamespaceAware(true);

    digester.addObjectCreate("*/locations", ArrayList.class);
    
    digester.addObjectCreate("*/locations/location", LocationDescriptor.class);
    digester.addSetProperties("*/locations/location");

    digester.addCallMethod("*/locations/location/protocol", "setProtocol", 0);
    digester.addCallMethod("*/locations/location/host", "setHost", 0);
    digester.addCallMethod("*/locations/location/port", "setPort", 0);
    digester.addCallMethod("*/locations/location/repository", "setRepository", 0);
    digester.addCallMethod("*/locations/location/module-offset", "setModuleOffset", 0);
    digester.addSetNext("*/locations/location", "add");

    return digester;
  }

}
