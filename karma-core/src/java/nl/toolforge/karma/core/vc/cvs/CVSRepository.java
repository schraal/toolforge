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
package nl.toolforge.karma.core.vc.cvs;

import nl.toolforge.core.util.net.Ping;
import nl.toolforge.karma.core.location.BaseLocation;
import nl.toolforge.karma.core.location.LocationException;
import nl.toolforge.karma.core.location.LocationType;
import nl.toolforge.karma.core.vc.VersionControlSystem;
import nl.toolforge.karma.core.vc.AuthenticationException;
import nl.toolforge.karma.core.vc.Authenticator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.netbeans.lib.cvsclient.connection.Connection;
import org.netbeans.lib.cvsclient.connection.ConnectionFactory;
import org.netbeans.lib.cvsclient.connection.PServerConnection;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Locale;

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

  public void authenticate() throws AuthenticationException {
    if (PSERVER.equals(getProtocol())) {
      Authenticator authenticator = new Authenticator();
      authenticator.authenticate(this);
    }
  }

  /**
   * Tries to locate the (scrambled) password in <code>$HOME/.cvspass</code>.
   *
   * @return The password for this CVS repository.
   */
  public String getPassword() {

    if (password == null) {
      password = lookupPassword();
    }
    return password;
  }

  // Copied from the netbeans api.
  //
  private String lookupPassword() {

    BufferedReader reader = null;
    String password = null;
    String cvsRoot = null;

    try {

      cvsRoot = getCVSRoot();
      String passFile = System.getProperty("user.home") + File.separator + ".cvspass";

      reader = null;

      reader = new BufferedReader(new FileReader(passFile));
      String line;
      while ((line = reader.readLine()) != null) {
        if (line.startsWith("/1 ")) line = line.substring("/1 ".length());
        if (line.startsWith(cvsRoot)) {
          password = line.substring(cvsRoot.length() + 1);
          break;
        }
      }
    }
    catch (IOException e) {
      logger.error("Could not read password for CVS host: " + e);
      return null;
    } catch (CVSException e) {
      return null;
    } finally {
      if (reader != null) {
        try {
          reader.close();
        }
        catch (IOException e) {
          logger.error("Warning: could not close password file.");
        }
      }
    }
    if (password == null) {
      logger.error("Didn't find password for CVSROOT '" + cvsRoot + "'.");
    }
    return password;
  }

  /**
   * The CVS repository protocol (<code>ext</code>, <code>pserver</code>, etc).
   *
   * @param protocol The CVS protocol (<code>:<b>pserver</b>:asmedes@localhost:2401/home/cvsroot</code>. Protocol
   *                 strings are converted to lowercase.
   */
  public void setProtocol(String protocol) {
    String p = LOCAL + "|" + EXT + "|" + PSERVER;
    if (protocol == null || !protocol.toLowerCase().matches(p)) {
      throw new IllegalArgumentException("Protocol is invalid; should be " + p);
    }
    super.setProtocol(protocol.toLowerCase());
  }

  public void setPort(String port) {

    try {
      super.setPort(Integer.parseInt(port));
    } catch (NumberFormatException n) {
      super.setPort(DEFAULT_PORT);
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

//  /**
//   * Returns a connection object to a CVS repository.
//   *
//   * @return A CVS Connection object.
//   * @throws CVSException <code>INVALID_CVSROOT</code>; <code>CONNECTION_EXCEPTION</code> is thrown when
//   *   <code>location</code> cannot be reached (remote locations) or is not present (local locations).
//   */
//  public Connection getConnection() throws CVSException {
//    if ( getProtocol().equals(LOCAL) ) {
//      if ( !new File(getRepository()).exists() ) {
//        //for local protocol check whether the cvsroot is present.
//        throw new CVSException(LocationException.CONNECTION_EXCEPTION, new Object[]{getId()});
//      }
//    } else if ( !ping() ) {
//      throw new CVSException(LocationException.CONNECTION_EXCEPTION, new Object[]{getId()});
//    }
//
//    if (cvsRootString != null) {
//      createCVSRoot();
//    }
//
//    Connection connection = ConnectionFactory.getConnection(getConnectionString());
//    if (connection instanceof PServerConnection) {
//      ((PServerConnection) connection).setEncodedPassword(this.getPassword());
//    }
//
//    return connection;
//  }

  public final String getCVSRoot() throws CVSException {

    StringBuffer buffer = new StringBuffer(":" + getProtocol() + ":");

    if (buffer.toString().equals(":".concat(LOCAL).concat(":"))) {

      // Returns ':local:<repositoru>'
      //
      if (getRepository() == null) {
        throw new CVSException(CVSException.INVALID_CVSROOT);
      }

      buffer.append(getRepository());

    } else {

      if ((getUsername() == null) || (getHost() == null) || (getPort() == -1)) {
        throw new CVSException(CVSException.INVALID_CVSROOT);
      }

      buffer.append(getUsername()).append("@");
      buffer.append(getHost()).append(":");
      buffer.append(getPort()).append(getRepository().startsWith("/") ? "" : "/");
      buffer.append(getRepository());
    }
    return buffer.toString();
  }

//  public String getConnectionString() throws CVSException {
//
//    if (cvsRootString == null) {
//      createCVSRoot();
//    }
//
//    return cvsRootString;
//  }



}
