package nl.toolforge.karma.core.location;

/**
 * @author D.A. Smedes
 * @version $Id$
 */
public final class LocationDescriptor {

  // todo validate each setter !!!!

  private String id = null;
  private String type = null;
  private String protocol = null;
  private String host = null;
  private String port = null;
  private String repository = null;

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

  public String getProtocol() {
    return protocol;
  }

  public void setProtocol(String protocol) {
    this.protocol = protocol;
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

  public boolean equals(Object obj) {

    if (obj instanceof LocationDescriptor) {
      return ((LocationDescriptor) obj).getId().equals(getId());
    }
    return false;
  }

  public int hashCode() {
    return getId().hashCode();
  }
}
