package nl.toolforge.karma.core.location;

/**
 * @author D.A. Smedes
 * @version $Id$
 */
public final class AuthenticatorDescriptor {

  // todo validate each setter !!!!

  private String id = null;
  private String username = null;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

}
