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
package nl.toolforge.karma.core.vc.cvsimpl;

import nl.toolforge.core.util.net.Ping;
import nl.toolforge.karma.core.location.LocationException;
import nl.toolforge.karma.core.location.LocationType;
import nl.toolforge.karma.core.vc.AuthenticationException;
import nl.toolforge.karma.core.vc.Authenticator;
import nl.toolforge.karma.core.vc.Authenticators;
import nl.toolforge.karma.core.vc.VersionControlSystem;
import nl.toolforge.karma.core.vc.AuthenticatorKey;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.netbeans.lib.cvsclient.command.CommandAbortedException;
import org.netbeans.lib.cvsclient.connection.Connection;
import org.netbeans.lib.cvsclient.connection.ConnectionFactory;
import org.netbeans.lib.cvsclient.connection.PServerConnection;

import java.io.File;
import java.text.MessageFormat;

/**
 * <p>Class representing a location to a CVS repository. This class is not the 'real' connection, as that is handled
 * by other classes in the sequence, yet it is a helper around the <code>org.netbeans.lib.cvsclient.CVSRoot</code>.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public class CVSRepository extends VersionControlSystem {

  private static Log logger = LogFactory.getLog(CVSRepository.class);

  /**
   * Default port number : <code>2401</code>
   */
  public static final int DEFAULT_PORT = 2401;

  public static final String LOCAL = "local";
  public static final String EXT = "ext";
  public static final String PSERVER = "pserver";
  private String password = null;

//  private String password = null;

  public CVSRepository(String id) {
    super(id, LocationType.CVS);
  }

  /**
   * 'Pings' the cvs repository host on the specified port. Returns <code>false</code> if a timeout occurs after 1 sec.
   *
   * @return
   */
  public boolean isAvailable() {
    if (!LOCAL.equals(getProtocol()) && getHost() != null && getPort() != -1) {
      return Ping.ping(getHost(), getPort(), 3000);
    } else {
      return true; // Local, apparently.
    }
  }

  public void connect() throws LocationException {

    Authenticator authenticator = null;
    try {
      authenticator = Authenticators.getAuthenticator(this.getAuthenticatorKey());
    } catch (AuthenticationException e) {
      throw new LocationException(e.getErrorCode());
    }

    Connection connection = null;
    try {
      connection = ConnectionFactory.getConnection(getCVSRoot(authenticator.getUsername()));
      if (connection instanceof PServerConnection) {
        ((PServerConnection) connection).setEncodedPassword(authenticator.getPassword());
      }
    } catch (CVSException c) {
      logger.error(c.getMessage());
      throw new LocationException(c.getErrorCode());
    }

    try {
      connection.open();
    } catch (CommandAbortedException e) {
      throw new LocationException(CVSException.INTERNAL_ERROR);
    } catch (org.netbeans.lib.cvsclient.connection.AuthenticationException e) {
      throw new LocationException(CVSException.AUTHENTICATION_ERROR, new Object[]{getId()});
    }
  }

  public void setPassword(String password) {
    this.password = password;
  }

  /**
   * Returns the encrypted password.
   *
   * @return
   */
  public String getPassword() {
    return password;
  }

  /**
   * The CVS repository protocol (<code>ext</code>, <code>pserver</code>, etc). <code>protocol</code> cannot be
   * <code>null</code>.
   *
   * @param protocol The CVS protocol (<code>:<b>pserver</b>:asmedes@localhost:2401/home/cvsroot</code>. Protocol
   *                 strings are converted to lowercase.
   */
  public void setProtocol(String protocol) {
    String p = LOCAL + "|" + PSERVER;
    if (protocol == null || !protocol.toLowerCase().matches(p)) {
      throw new IllegalArgumentException("Protocol is invalid; should be " + p);
    }
    super.setProtocol(protocol.toLowerCase());
  }

  public void setPort(int port) {

    int correctPort = -1;

    if (port < 0) {
      correctPort = DEFAULT_PORT;
    } else {
      correctPort = port;
    }
    super.setPort(correctPort);
  }

  /**
   * Sets the server port. Overrides <code>super.setPort(String)</code> entirely. When <code>port</code> is not a
   * number, {@link DEFAULT_PORT} is set.
   *
   * @param port
   */
  public void setPort(String port) {

    try {
      setPort(Integer.parseInt(port));
    } catch (NumberFormatException n) {
      setPort(CVSRepository.DEFAULT_PORT);
    }
  }

  /**
   * A CVS repository should have a "/" at the end. This method overrides <code>super.setRepository</code> and fixes
   * this.
   *
   * @param repository The CVS repository path (<code>:pserver:asmedes@localhost:2401<b>/home/cvsroot</b></code>
   */
  public final void setRepository(String repository) {

    if ((repository == null) || (repository.length() == 0)) {
      throw new IllegalArgumentException("Repository cannot be null.");
    }
    // due to some 'feature' in the netbeans API, we trim the repository string from slashes ...
    //
    while(repository.endsWith(File.separator)) {
      repository = repository.substring(0, repository.lastIndexOf(File.separator));
    }
    super.setRepository(repository);
  }

  /**
   * Constructs a CVSROOT string. This method with fail when the CVSROOT string cannot be constructed. This can happen
   * under the following circumstances:
   *
   * <lu>
   * <li/>For the <code>local</code> protocol : when {@link #getRepository()} returns <code>null</code>.
   * <li/>For the <code>pserver</code> protocol : when {@link #getUsername()} or {@link #getHost()} return
   *      <code>null</code>
   * </ul>
   *
   * @return
   * @throws CVSException
   */
  public final String getCVSRoot() throws CVSException {

//    StringBuffer buffer = new StringBuffer(":" + getProtocol() + ":");
//
//    if (buffer.toString().equals(":".concat(LOCAL).concat(":"))) {
//
//      // Returns ':local:<repositoru>'
//      //
//      if (getRepository() == null) {
//        throw new CVSException(CVSException.INVALID_CVSROOT);
//      }
//
//      buffer.append(getRepository());
//
//    } else {
//
//      if ((getUsername() == null) || (getHost() == null) || (getPort() == -1)) {
//        throw new CVSException(CVSException.INVALID_CVSROOT);
//      }
//
//      buffer.append(getUsername()).append("@");
//      buffer.append(getHost()).append(":");
//      buffer.append(getPort()).append(getRepository().startsWith("/") ? "" : "/");
//      buffer.append(getRepository());
//    }
    return getCVSRoot(null);
  }


  private String getCVSRoot(String userName) throws CVSException {

    StringBuffer buffer = new StringBuffer(":" + getProtocol() + ":");

    if (buffer.toString().equals(":".concat(LOCAL).concat(":"))) {

      // Returns ':local:<repositoru>'
      //
      if (getRepository() == null) {
        throw new CVSException(CVSException.INVALID_CVSROOT);
      }

      buffer.append(getRepository());

    } else {

      String user = (userName == null ? getUsername() : userName);

      if ((user == null) || (getHost() == null) || (getPort() == -1)) {
        throw new CVSException(CVSException.INVALID_CVSROOT);
      }

      buffer.append(user).append("@");

      buffer.append(getHost()).append(":");
      buffer.append(getPort()).append(getRepository().startsWith("/") ? "" : "/");
      buffer.append(getRepository());
    }
    return buffer.toString();
  }

  /**
   * Returns the CVSROOT as a string.
   *
   * @return The CVSROOT as a string.
   */
  public String toString() {

    try {
      return getCVSRoot(Authenticators.getAuthenticator(getAuthenticatorKey()).getUsername());
    } catch (CVSException e) {
      return "<invalid-cvsroot>";
    } catch (AuthenticationException e) {
      return "<invalid-cvsroot>";
    }
  }

  /**
   * Returns a StringBuffer with an XML string view of the location.
   *
   * @return A StringBuffer with an XML string view of the location.
   */
  public StringBuffer asXML() {
    return asXML(2);
  }

  public StringBuffer asXML(int leftIndent) {

    StringBuffer buffer = new StringBuffer();

    MessageFormat formatter = new MessageFormat(
        "<loc:location type=\"{0}\" id=\"{1}\">\n" +
        "   <loc:protocol>{2}</loc:protocol>\n" +
        "   <loc:host>{3}</loc:host>\n" +
        "   <loc:port>{4}</loc:port>\n" +
        "   <loc:repository>{5}</loc:repository>\n" +
        "   <loc:module-offset>{6}</loc:module-offset>\n" +
        "</loc:location>\n");

    buffer.append(formatter.format(
        new String[]{
          getType().toString(),
          getId(),
          getProtocol(),
          (getHost() == null ? "" : getHost()),
          "" + (getPort() == -1 ? "" : "" + getPort()),
          getRepository(),
          (getModuleOffset() == null ? "" : getModuleOffset())
        }));

    return buffer;
  }
}
