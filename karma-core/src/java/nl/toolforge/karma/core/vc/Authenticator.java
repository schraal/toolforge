package nl.toolforge.karma.core.vc;

import nl.toolforge.karma.core.boot.WorkingContext;
import nl.toolforge.karma.core.location.LocationException;
import org.apache.commons.digester.Digester;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.io.FileWriter;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collection;
import java.text.MessageFormat;

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
   * <p>Authenticates <code>location</code> against <code>authenticators.xml</code> by updating the location with the
   * corresponding username.
   *
   * @param location A <code>VersionControlSystem</code> instance, that requires authentication.
   */
  public void authenticate(VersionControlSystem location) throws AuthenticationException {

    if (location == null) {
      throw new IllegalArgumentException("Location cannot be null.");
    }

    try {
      location.setUsername(((Authenticator) getAuthenticators().get(location.getId())).getUsername());
    } catch (LocationException e) {
      // todo extend the messaging scheme
      throw new AuthenticationException("Could not authenticate location.");
    }
  }

  private Map getAuthenticators() throws LocationException {

    Map authenticators = new Hashtable();

    File authenticatorsFile = new File(WorkingContext.getConfigurationBaseDir(), "authenticators.xml");

    if (authenticatorsFile == null) {
      throw new LocationException(LocationException.MISSING_AUTHENTICATOR_CONFIGURATION);
    }

    // Create a list of authenticators, anything you can find.
    //
    Digester digester = getDigester();

    List subList = null;
    try {
      subList = (List) digester.parse(authenticatorsFile.getPath());
    } catch (IOException e) {
      throw new LocationException(e, LocationException.MISSING_AUTHENTICATOR_CONFIGURATION);
    } catch (SAXException e) {
      throw new LocationException(e, LocationException.AUTHENTICATOR_LOAD_ERROR);
    }

    for (Iterator j = subList.iterator(); j.hasNext();) {
      Authenticator authDescriptor = (Authenticator) j.next();
      if (authenticators.containsKey(authDescriptor.getId())) {
        throw new LocationException(
            LocationException.DUPLICATE_AUTHENTICATOR_KEY,
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

    } catch (LocationException e) {
      return false;
    }

    if (authenticators.containsKey(authenticator.getId())) {
      return false;
    }

    // Add the authenticator.
    //
    authenticators.put(authenticator.getId(), authenticator);

    StringBuffer buffer = new StringBuffer();

    buffer.append("<?xml version=\"1.0\"?>\n\n");
    buffer.append("<authenticators>\n");

    for (Iterator i = authenticators.values().iterator(); i.hasNext();) {
      MessageFormat formatter = new MessageFormat("  <authenticator id=\"{0}\" username=\"{1}\"/>\n");
      Authenticator a = (Authenticator) i.next();
      buffer.append(formatter.format(new String[]{a.getId(), a.getUsername()}));
    }

    buffer.append("</authenticators>\n");

    FileWriter writer = null;
    try {
      // Write the manifest to the manifest store.
      //
      writer = new FileWriter(new File(WorkingContext.getConfigurationBaseDir(), "authenticators.xml"));

      writer.write(buffer.toString());
      writer.flush();

      return true;

    } catch (IOException e) {
      return false;
    } finally {
      try {
        writer.close();
      } catch (IOException e) {
        return false;
      }
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
