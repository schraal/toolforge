package nl.toolforge.karma.core.vc;

import nl.toolforge.karma.core.boot.WorkingContext;
import nl.toolforge.karma.core.location.PasswordScrambler;
import org.apache.commons.digester.Digester;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * <p>When a {@link nl.toolforge.karma.core.location.Location} - more specifically, {@link VersionControlSystem} -
 * requires authentication, Karma provides for a mechanism whereby an d<code>authenticators.xml</code>, located in the
 * Karma configuration directory stores a username. Depending on the specific implementation of
 * <code>VersionControlSystem</code>, password info is then retrieved.
 *
 * <p>The <code>Authenticator</code> class reads the <code>authenticators.xml</code> from the Karma configuration
 * directory and can then map <code>VersionControlSystem</code> instances by checking if an <code>authenticator</code>
 * element is present for the location. This mapping is done by checking if the <code>id</code> attributes for the
 * <code>location</code> and the <code>authenticator</code> are the same.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public final class Authenticator {

  private String id = null;
  private String username = null;
  private String password = null;

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

  public synchronized void changePassword(VersionControlSystem location, String newPassword) throws AuthenticationException {

    if (location == null) {
      throw new IllegalArgumentException("Location cannot be null.");
    }

    Map authenticators = getAuthenticators();

    Authenticator authenticator = ((Authenticator) authenticators.get(location.getId()));

    if (authenticator == null) {
      throw new AuthenticationException(AuthenticationException.AUTHENTICATOR_NOT_FOUND, new Object[]{location.getId()});
    }

    authenticator.setPassword(PasswordScrambler.scramble(newPassword));

    try {
      flush(authenticators);
    } catch (IOException e) {
      throw new AuthenticationException(AuthenticationException.AUTHENTICATOR_WRITE_ERROR);
    }
  }

  /**
   * <p>Authenticates <code>location</code> against <code>authenticators.xml</code> by updating the location with the
   * corresponding username.
   *
   * @param location A <code>VersionControlSystem</code> instance, that requires authentication.
   */
  public Authenticator authenticate(VersionControlSystem location) throws AuthenticationException {

    if (location == null) {
      throw new IllegalArgumentException("Location cannot be null.");
    }

    Authenticator authenticator = ((Authenticator) getAuthenticators().get(location.getId()));

    if (authenticator == null) {
      throw new AuthenticationException(AuthenticationException.AUTHENTICATOR_NOT_FOUND, new Object[]{location.getId()});
    }
    location.setUsername(authenticator.getUsername());

    return authenticator;
  }

  private Map getAuthenticators() throws AuthenticationException {

    Map authenticators = new Hashtable();

    File authenticatorsFile = new File(WorkingContext.getConfigurationBaseDir(), "authenticators.xml");

    if (authenticatorsFile == null) {
      throw new AuthenticationException(AuthenticationException.MISSING_AUTHENTICATOR_CONFIGURATION);
    }

    // Create a list of authenticators, anything you can find.
    //
    Digester digester = getDigester();

    List subList = null;
    try {
      subList = (List) digester.parse(authenticatorsFile.getPath());
    } catch (IOException e) {
      throw new AuthenticationException(e, AuthenticationException.MISSING_AUTHENTICATOR_CONFIGURATION);
    } catch (SAXException e) {
      throw new AuthenticationException(e, AuthenticationException.AUTHENTICATOR_LOAD_ERROR);
    }

    for (Iterator j = subList.iterator(); j.hasNext();) {
      Authenticator authDescriptor = (Authenticator) j.next();
      if (authenticators.containsKey(authDescriptor.getId())) {
        throw new AuthenticationException(
            AuthenticationException.DUPLICATE_AUTHENTICATOR_KEY,
            new Object[] {authDescriptor.getId(), WorkingContext.getConfigurationBaseDir().getPath()}
        );
      }
      authenticators.put(authDescriptor.getId(), authDescriptor);
    }
    return authenticators;
  }

  /**
   * Adds an authenticator to <code>authenticators.xml</code> if the authenticator does not yet exist.
   *
   * @param authenticator
   *
   * @return <code>true</code> if <code>authenticator</code> was added, <code>false</code> otherwise.
   */
  public synchronized boolean addAuthenticator(Authenticator authenticator) {

    Map authenticators = null;
    try {

      // todo hmm, this bit of code COULD be used to detect duplicates as well .

      authenticators = getAuthenticators();

    } catch (AuthenticationException e) {
      return false;
    }

//    if (authenticators.containsKey(authenticator.getId())) {
//      authenticators.remove(authenticator);
//    }

    // Add the authenticator.
    //
    authenticators.put(authenticator.getId(), authenticator);

    try {
      flush(authenticators);
    } catch (IOException e) {
      return false;
    }
    return true;
  }

  private synchronized void flush(Map authenticators) throws IOException {

    StringBuffer buffer = new StringBuffer();

    buffer.append("<?xml version=\"1.0\"?>\n\n");
    buffer.append("<authenticators>\n");

    for (Iterator i = authenticators.values().iterator(); i.hasNext();) {
      MessageFormat formatter = null;

      Authenticator a = (Authenticator) i.next();
      if (a.getPassword() != null) {
        formatter = new MessageFormat("  <authenticator id=\"{0}\" username=\"{1}\" password=\"{2}\"/>\n");
        buffer.append(formatter.format(new String[]{a.getId(), a.getUsername(), a.getPassword()}));
      } else {
        formatter = new MessageFormat("  <authenticator id=\"{0}\" username=\"{1}\"/>\n");
        buffer.append(formatter.format(new String[]{a.getId(), a.getUsername()}));
      }
    }

    buffer.append("</authenticators>\n");

    FileWriter writer = null;
    try {
      // Write the manifest to the manifest store.
      //
      writer = new FileWriter(new File(WorkingContext.getConfigurationBaseDir(), "authenticators.xml"));

      writer.write(buffer.toString());
      writer.flush();

    } finally {
      writer.close();
    }
  }

  private Digester getDigester() {

    Digester digester = new Digester();
    digester.addObjectCreate("authenticators", ArrayList.class);
    digester.addObjectCreate("authenticators/authenticator", Authenticator.class);
    digester.addSetProperties("authenticators/authenticator");
    digester.addSetNext("authenticators/authenticator", "add");
    return digester;
  }
}
