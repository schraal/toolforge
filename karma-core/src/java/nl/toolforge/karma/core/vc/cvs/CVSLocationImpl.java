package nl.toolforge.karma.core.vc.cvs;

import nl.toolforge.karma.core.location.BaseLocation;
import nl.toolforge.karma.core.location.Location;
import org.netbeans.lib.cvsclient.connection.Connection;
import org.netbeans.lib.cvsclient.connection.ConnectionFactory;
import org.netbeans.lib.cvsclient.connection.PServerConnection;

import java.util.Locale;

/**
 * <p>Class representing a location to a CVS repository. This class is not the 'real' connection, as that is handled
 * by other classes in the sequence, yet it is a helper around the <code>org.netbeans.lib.cvsclient.CVSRoot</code>.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public final class CVSLocationImpl extends BaseLocation {

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

  private String cvsRootString = null;

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

  /**
   * The CVS password in the normal (<b>* insecure *</b>) format. This password can generally be found in
   * <code>${user.home}/.cvspass</code>.
   *
   * @param encodedPassword The CVS password in the normal (<b>* insecure *</b>) password.
   */
  public void setPassword(String encodedPassword) {

    if ((encodedPassword == null) || (encodedPassword.length() == 0)) {
      throw new IllegalArgumentException("Password cannot be null.");
    }
    // TODO some encoding scheme should be applied.
    //
    this.password = encodedPassword;
  }

  String getPassword() {
    return password;
  }

  /**
   * Checks if a password has been set.
   */
  public boolean passwordSet() {
    return password != null;
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
    this.repository = repository;
  }

  String getRepository() {
    return repository;
  }

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
   * @throws CVSException See {@link CVSException#INVALID_CVSROOT}
   */
  public Connection getConnection() throws CVSException {

    if (cvsRootString != null) {
      createCVSRoot();
    }

    Connection connection = ConnectionFactory.getConnection(getCVSRootAsString());
    if (connection instanceof PServerConnection) {
      ((PServerConnection) connection).setEncodedPassword(this.getPassword());
    }

    return connection;
  }

  private synchronized void createCVSRoot() throws CVSException {

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

//			buffer.append(username).append(":").append(password).append("@");
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
