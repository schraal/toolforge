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
}
