package nl.toolforge.karma.core.vc;

import nl.toolforge.karma.core.boot.WorkingContext;
import nl.toolforge.karma.core.location.PasswordScrambler;
import nl.toolforge.karma.core.KarmaRuntimeException;
import org.apache.commons.digester.Digester;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;

/**
 * @author D.A. Smedes
 * @version $Id$
 */
public final class Authenticator {

  private String workingContext = null;
  private String id = null;
  private String username = null;
  private String password = null;

  public Authenticator(String id) {
    setId(id);
  }

  /**
   * Empty constructor (generally only used by Digesters).
   */
  public Authenticator() {
    // Empty
  }

  public String getWorkingContext() {
    return workingContext;
  }

  public void setWorkingContext(String workingContext) {

    if ("".equals(workingContext) || workingContext == null) {
      throw new IllegalArgumentException(
          "The `working-context`-attribute for an authenticator cannot be null or an empty string.");
    }
    this.workingContext = workingContext;
  }

  public String getId() {

    return id;
  }

  public void setId(String id) {

    if ("".equals(id) || id == null) {
      throw new IllegalArgumentException(
          "The `id`-attribute for an authenticator cannot be null or an empty string.");
    }
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  /**
   * Returns the password (encrypted) or an empty String if the password is null.
   *
   * @return
   */
  public String getPassword() {
    return (password == null ? "" : password);
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public AuthenticatorKey getAuthenticatorKey() {
    return new AuthenticatorKey(workingContext, id);
  }
}
