package nl.toolforge.karma.core.vc;

import nl.toolforge.karma.core.KarmaRuntimeException;
import nl.toolforge.karma.core.boot.WorkingContext;
import nl.toolforge.karma.core.location.PasswordScrambler;
import org.apache.commons.digester.Digester;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
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
public final class Authenticators {

  /**
   * Singleton, helper.
   */
  private Authenticators() {}

  public static synchronized void changePassword(VersionControlSystem location, String newPassword) throws AuthenticationException {

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
      throw new AuthenticationException(e, AuthenticationException.AUTHENTICATOR_WRITE_ERROR);
    }
  }

  /**
   * <p>Authenticates <code>location</code> against <code>authenticators.xml</code> by updating the location with the
   * corresponding username.
   *
   * @param location A <code>VersionControlSystem</code> instance, that requires authentication.
   */
  public static Authenticator getAuthenticator(VersionControlSystem location) throws AuthenticationException {

    if (location == null) {
      throw new IllegalArgumentException("Location cannot be null.");
    }
    return getAuthenticator(location.getId());
  }

  public static Authenticator getAuthenticator(String locationId) throws AuthenticationException {

    if (locationId == null) {
      throw new IllegalArgumentException("Location id cannot be null.");
    }

    Authenticator authenticator = ((Authenticator) getAuthenticators().get(locationId));

    if (authenticator == null) {
      throw new AuthenticationException(AuthenticationException.AUTHENTICATOR_NOT_FOUND, new Object[]{locationId});
    }

    return authenticator;
  }

  private static Map getAuthenticators() throws AuthenticationException {

    Map authenticators = new Hashtable();

    File authenticatorsFile = new File(WorkingContext.getConfigurationBaseDir(), "authenticators.xml");

    if (!authenticatorsFile.exists()) {
      createNew();
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
   */
  public static synchronized void addAuthenticator(Authenticator authenticator) {

    if (authenticator == null) {
      throw new IllegalArgumentException("Authenticator cannot be null.");
    }

    Map authenticators = null;
    try {

      // todo hmm, this bit of code COULD be used to detect duplicates as well .

      authenticators = getAuthenticators();

    } catch (AuthenticationException e) {
      throw new KarmaRuntimeException(e);
    }

    // Add the authenticator.
    //
    authenticators.put(authenticator.getId(), authenticator);

    try {
      flush(authenticators);
    } catch (IOException e) {
      throw new KarmaRuntimeException(e);
    }
  }

  /**
   * Deletes an authenticator entry from <code>authenticators.xml</code>.
   *
   * @param authenticator
   */
  public static synchronized void deleteAuthenticator(Authenticator authenticator) throws AuthenticationException {

    Map authenticators = null;
    try {

      // todo hmm, this bit of code COULD be used to detect duplicates as well .

      authenticators = getAuthenticators();

    } catch (AuthenticationException e) {
      throw new KarmaRuntimeException(e);
    }

    // Add the authenticator.
    //
    authenticators.remove(authenticator.getId());

    try {
      flush(authenticators);
    } catch (IOException e) {
      throw new KarmaRuntimeException(e);
    }
  }

  private static void createNew() throws AuthenticationException {
    try {
      flush(new HashMap());
    } catch (IOException e) {
      throw new AuthenticationException(e, AuthenticationException.MISSING_AUTHENTICATOR_CONFIGURATION);
    }
  }

  private static synchronized void flush(Map authenticators) throws IOException {

    StringBuffer buffer = new StringBuffer();

    buffer.append("<?xml version=\"1.0\"?>\n\n");
    buffer.append("<authenticators>\n");

    for (Iterator i = authenticators.values().iterator(); i.hasNext();) {
      MessageFormat formatter = null;

      Authenticator a = (Authenticator) i.next();
      if (a.getPassword() != null) {
        formatter = new MessageFormat("  <authenticator id=\"{0}\" username=\"{1}\" password=\"{2}\"/>\n");

        // todo I guess there is some default toolie available for this (XML escaper).

        String password = a.getPassword();
        password = password.replaceAll("&", "&amp;");
        password = password.replaceAll("<", "&lt;");
        password = password.replaceAll(">", "&gt;");
        password = password.replaceAll("\"", "&quot;");
        password = password.replaceAll("'", "&apos;");

        buffer.append(formatter.format(new String[]{a.getId(), a.getUsername(), password}));
      } else {
        formatter = new MessageFormat("  <authenticator id=\"{0}\" username=\"{1}\"/>\n");
        buffer.append(formatter.format(new String[]{a.getId(), a.getUsername()}));
      }
    }

    buffer.append("</authenticators>\n");

    FileWriter writer = null;
    // Write the manifest to the manifest store.
    //
    writer = new FileWriter(new File(WorkingContext.getConfigurationBaseDir(), "authenticators.xml"));
    try {

      writer.write(buffer.toString());
      writer.flush();

    } finally {
      writer.close();
    }
  }

  private static Digester getDigester() {

    Digester digester = new Digester();
    digester.addObjectCreate("authenticators", ArrayList.class);
    digester.addObjectCreate("authenticators/authenticator", Authenticator.class);
    digester.addSetProperties("authenticators/authenticator");
    digester.addSetNext("authenticators/authenticator", "add");
    return digester;
  }
}
