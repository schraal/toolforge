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
package nl.toolforge.karma.core.vc;

import nl.toolforge.karma.core.KarmaRuntimeException;
import nl.toolforge.karma.core.boot.WorkingContext;
import nl.toolforge.karma.core.location.PasswordScrambler;
import org.apache.commons.digester.Digester;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

  private static Log logger = LogFactory.getLog(Authenticators.class);

  /**
   * Singleton, helper.
   */
  private Authenticators() {}

  public static synchronized void changePassword(AuthenticatorKey key, String newPassword) throws AuthenticationException {

    if (key == null) {
      throw new IllegalArgumentException("Authenticator key cannot be null.");
    }

    Map authenticators = getAuthenticators();

    Authenticator authenticator = ((Authenticator) authenticators.get(key));

    if (authenticator == null) {
      throw new AuthenticationException(AuthenticationException.AUTHENTICATOR_NOT_FOUND, new Object[]{key});
    }

    authenticator.setPassword(PasswordScrambler.scramble(newPassword));

    try {
      flush(authenticators);
    } catch (IOException e) {
      throw new AuthenticationException(e, AuthenticationException.AUTHENTICATOR_WRITE_ERROR);
    }
  }

  public static Authenticator getAuthenticator(Authenticator authenticator) throws AuthenticationException {
    return getAuthenticator(authenticator.getAuthenticatorKey());
  }

  /**
   * Retrieves an <code>Authenticator</code> entry from the <code>authenticators.xml</code> file.
   *
   * @param key                      The key by which the <code>Authenticator</code> will be located.
   * @return                         The <code>Authenticator</code> with key <code>key</code>.
   * @throws AuthenticationException When no <code>Authenticator</code> can be found with key <code>key</code>.
   */
  public static Authenticator getAuthenticator(AuthenticatorKey key) throws AuthenticationException {

    if (key == null) {
      throw new IllegalArgumentException("Authenticator key cannot be null.");
    }

    Authenticator authenticator = ((Authenticator) getAuthenticators().get(key));

    if (authenticator == null) {
      throw new AuthenticationException(AuthenticationException.AUTHENTICATOR_NOT_FOUND, new Object[]{key});
    }

    return authenticator;
  }

  private static Map authenticatorCache = null;
  private static long lastModified = 0l;

  private synchronized static Map getAuthenticators() throws AuthenticationException {

    File authenticatorsFile = new File(WorkingContext.getConfigurationBaseDir(), "authenticators.xml");

    if (!authenticatorsFile.exists()) {
      createNew();
    }

    if (authenticatorCache == null) {
      authenticatorCache = new Hashtable();
      logger.debug("Creating authenticator cache from `authenticators.xml`.");
    } else {

      // Check if the file has been changed outside the running Karma.
      //
      if (authenticatorsFile.lastModified() == lastModified) {
        logger.debug("Using authenticator cache.");
        return authenticatorCache;
      } else {
        authenticatorCache = new Hashtable();
        logger.debug("Recreating authenticator cache from `authenticators.xml`.");
      }
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
      AuthenticatorKey key = new AuthenticatorKey(authDescriptor.getWorkingContext(), authDescriptor.getId());
      if (authenticatorCache.containsKey(key)) {
        throw new AuthenticationException(
            AuthenticationException.DUPLICATE_AUTHENTICATOR_KEY,
            new Object[] {key, WorkingContext.getConfigurationBaseDir().getPath()}
        );
      }
      authenticatorCache.put(key, authDescriptor);
    }

    // Maintain the lastModified time.
    //
    lastModified = authenticatorsFile.lastModified();

    return authenticatorCache;
  }

  /**
   * Adds an authenticator to <code>authenticators.xml</code> if the authenticator does not yet exist.
   *
   * @param authenticator
   */
  public static synchronized void addAuthenticator(Authenticator authenticator) throws AuthenticationException {

    if (authenticator == null) {
      throw new IllegalArgumentException("Authenticator cannot be null.");
    }

    // Add the authenticator.
    //
    getAuthenticators().put(authenticator.getAuthenticatorKey(), authenticator);

    try {
      flush(getAuthenticators());
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

    // Add the authenticator.
    //
    getAuthenticators().remove(new AuthenticatorKey(authenticator.getWorkingContext(), authenticator.getWorkingContext()));

    try {
      flush(getAuthenticators());
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
        formatter = new MessageFormat("  <authenticator working-context=\"{0}\" id=\"{1}\" username=\"{2}\" password=\"{3}\"/>\n");

        // todo I guess there is some default toolie available for this (XML escaper).

        String password = a.getPassword();
        password = password.replaceAll("&", "&amp;");
        password = password.replaceAll("<", "&lt;");
        password = password.replaceAll(">", "&gt;");
        password = password.replaceAll("\"", "&quot;");
        password = password.replaceAll("'", "&apos;");

        buffer.append(formatter.format(new String[]{a.getWorkingContext(), a.getId(), a.getUsername(), password}));
      } else {
        formatter = new MessageFormat("  <authenticator working-context=\"{0}\" id=\"{1}\" username=\"{2}\"/>\n");
        buffer.append(formatter.format(new String[]{a.getWorkingContext(), a.getId(), a.getUsername()}));
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
    digester.addSetProperties("authenticators/authenticator", new String[]{"working-context"}, new String[]{"workingContext"});
    digester.addSetNext("authenticators/authenticator", "add");
    return digester;
  }
}
