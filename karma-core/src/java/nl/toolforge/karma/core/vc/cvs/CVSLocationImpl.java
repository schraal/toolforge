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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.netbeans.lib.cvsclient.connection.Connection;
import org.netbeans.lib.cvsclient.connection.ConnectionFactory;
import org.netbeans.lib.cvsclient.connection.PServerConnection;

import nl.toolforge.core.util.net.Ping;
import nl.toolforge.karma.core.location.BaseLocation;
import nl.toolforge.karma.core.location.Location;
import nl.toolforge.karma.core.location.LocationException;

/**
 * <p>Class representing a location to a CVS repository. This class is not the 'real' connection, as that is handled
 * by other classes in the sequence, yet it is a helper around the <code>org.netbeans.lib.cvsclient.CVSRoot</code>.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public final class CVSLocationImpl extends BaseLocation {

  private static Log logger = LogFactory.getLog(CVSLocationImpl.class);

  /**
   * Default port number : <code>2401</code>
   */
  public static final int DEFAULT_PORT = 2401;

  /**
   * Protocol for local CVS access
   */
  public static final String LOCAL = "local";
  public static final String EXT = "ext";
  public static final String PSERVER = "pserver";

  private String host = null;
  private String username = null;
  private String password = null;
  private String protocol = null;
  private int port = -1;
  private String repository = null;
  private String rootModule = null;

  private String cvsRootString = null;
  private boolean passwordSet = false;


  public CVSLocationImpl(String id) {
    super(id, Location.Type.CVS_REPOSITORY);
  }

  /**
   * The CVS host, where the repository is maintained.
   *
   * @param host The CVS username path (<code>:pserver:asmedes@<b>localhost</b>:2401/home/cvsroot</code>
   */
  public void setHost(String host) {

    if ((host == null) || (host.length() == 0)) {
      throw new IllegalArgumentException("Host cannot be null.");
    }
    this.host = host;
  }

  String getHost() {
    return host;
  }

  /**
   * 'Pings' the cvs repository host on the specified port. Returns <code>false</code> if a timeout occurs after 1 sec.
   *
   * @return
   */
  public boolean ping() {
    if (!LOCAL.equals(protocol) && host != null && port != -1) {
      return Ping.ping(host, port, 2000);
    } else {
      return true; // Local, apparently.
    }
  }

  /**
   * The CVS username.
   *
   * @param username The CVS username path (<code>:pserver:<b>asmedes</b>@localhost:2401/home/cvsroot</code>
   */
  public void setUsername(String username) {

    if ((username == null) || (username.length() == 0)) {
      throw new IllegalArgumentException("Username cannot be null.");
    }
    this.username = username;
  }

  public String getUsername() {
    return username;
  }

  private String getPassword() throws CVSException {

    if (this.password == null) {
      this.password = lookupPassword();
    }
    return password;
  }

  public boolean passwordSet() {

    if (!passwordSet) {
      try {
        passwordSet = (lookupPassword() != null);
      } catch (CVSException c) {
        return false;
      }
    }
    return passwordSet;
  }

  // Copied from the netbeans api.
  //
  private String lookupPassword() throws CVSException {

    String cvsRoot = getCVSRootAsString();
    String passFile = System.getProperty("user.home") + File.separator + ".cvspass";

    BufferedReader reader = null;
    String password = null;

    try {
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
    }  finally {
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
    this.protocol = protocol.toLowerCase();
  }

  public String getProtocol() {
    return protocol;
  }

  /**
   * The CVS repository port number, or zero when the port is not defined.
   *
   * @param port The CVS repository path (<code>:pserver:asmedes@localhost:<b>2401</b>/home/cvsroot</code>
   */
  public void setPort(int port) {
    this.port = port;
  }

  void setPort(String port) {

    try {
      this.port = Integer.parseInt(port);
    } catch (NumberFormatException n) {
      this.port = DEFAULT_PORT;
    }
  }

  int getPort() {
    return port;
  }

  /**
   * The CVS repository string.
   *
   * @param repository The CVS repository path (<code>:pserver:asmedes@localhost:2401<b>/home/cvsroot</b></code>
   */
  public void setRepository(String repository) {

    if ((repository == null) || (repository.length() == 0)) {
      throw new IllegalArgumentException("Repository cannot be null.");
    }
    // due to some 'feature' in the netbeans API, we trim the repository string from slashes ...
    //
    while(repository.endsWith(File.separator)) {
      repository = repository.substring(0, repository.lastIndexOf(File.separator));
    }

    this.repository = repository;
  }

  String getRepository() {
    return repository;
  }

//  /**
//   * The module name that acts as the starting point for a module within a CVS repository. For example, of modules are
//   * stored in a CVS repository under modules, a checkout should be performed from <code>modules/module-A</code> to get
//   * <code>module-A</code>.
//   *
//   * @return The root module or any empty string if none exists (in which case modules are kept directly at the CVSROOT.
//   */
//  public String getRootModule() {
//    return (rootModule == null ? "" : rootModule);
//  }
//
//  public void setRootModule(String rootModule) {
//    this.rootModule = rootModule;
//  }

  /**
   * String representation of the CVSROOT.
   *
   * @return String representation of the CVSROOT, or the message from the {@link CVSException#INVALID_CVSROOT} exception.
   */
  public String toString() {
    try {
      return getCVSRootAsString();
    } catch (CVSException c) {
      return CVSException.INVALID_CVSROOT.getErrorMessage(Locale.ENGLISH);
    }
  }

  /**
   * Returns a connection object to a CVS repository.
   *
   * @return A CVS Connection object.
   * @throws CVSException <code>INVALID_CVSROOT</code>; <code>CONNECTION_EXCEPTION</code> is thrown when
   *   <code>location</code> cannot be reached (remote locations) or is not present (local locations).
   */
  public Connection getConnection() throws CVSException {
    if ( getProtocol().equals(LOCAL) ) {
      if ( !new File(getRepository()).exists() ) {
        //for local protocol check whether the cvsroot is present.
        throw new CVSException(LocationException.CONNECTION_EXCEPTION, new Object[]{getId()});
      }
    } else if ( !ping() ) {
      throw new CVSException(LocationException.CONNECTION_EXCEPTION, new Object[]{getId()});
    }

    if (cvsRootString != null) {
      createCVSRoot();
    }

    Connection connection = ConnectionFactory.getConnection(getCVSRootAsString());
    if (connection instanceof PServerConnection) {
      ((PServerConnection) connection).setEncodedPassword(this.getPassword());
    }

    return connection;
  }

  private void createCVSRoot() throws CVSException {

    StringBuffer buffer = new StringBuffer(":" + protocol + ":");

    if (buffer.toString().equals(":".concat(LOCAL).concat(":"))) {

      // Returns ':local:<repositoru>'
      //
      if (repository == null) {
        throw new CVSException(CVSException.INVALID_CVSROOT);
      }

      buffer.append(repository);

    } else {

      if ((getUsername() == null) || (getHost() == null) || (getPort() == -1)) {
        throw new CVSException(CVSException.INVALID_CVSROOT);
      }

      buffer.append(username).append("@");
      buffer.append(host).append(":");
      buffer.append(port).append(repository.startsWith("/") ? "" : "/");
      buffer.append(repository);
    }
    cvsRootString = buffer.toString();
  }

  public String getCVSRootAsString() throws CVSException {

    if (cvsRootString == null) {
      createCVSRoot();
    }

    return cvsRootString;
  }
}
