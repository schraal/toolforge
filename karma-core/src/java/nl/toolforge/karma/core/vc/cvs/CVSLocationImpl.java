package nl.toolforge.karma.core.vc.cvs;

import nl.toolforge.karma.core.KarmaException;
import nl.toolforge.karma.core.location.BaseLocation;
import nl.toolforge.karma.core.location.Location;

import java.util.Locale;

/**
 *
 * @author D.A. Smedes
 *
 * @version $Id$
 */
public final class CVSLocationImpl extends BaseLocation {

	/** Default port number : <code>2401</code> */
	public static final int DEFAULT_PORT = 2401;

	/** Default protocol : <code>pserver</code> */
	public static final String DEFAULT_PROTOCOL = "pserver";

	private String host = null;
	private String username = null;
	private String password = null;
	private String protocol = null;
	private int port = -1;
	private String repository = null;

	public CVSLocationImpl(String id) throws KarmaException {
		super(id, Location.Type.CVS_REPOSITORY);
	}

	/**
	 * The CVS host, where the repository is maintained.
	 *
	 * @param host The CVS username path (<code>:pserver:asmedes@<b>localhost</b>:2401/home/cvsroot</code>
	 */
	public void setHost(String host) {
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
		this.username = username;
	}
	String getUsername() {
		return username;
	}

	/**
	 * The CVS password in the normal (<b>* insecure *</b>) format. This password can generally be found in
	 * <code>${user.home}/.cvspass</code>.
	 *
	 * @param encodedPassword The CVS password in the normal (<b>* insecure *</b>) password.
	 */
	public void setPassword(String encodedPassword) {

		// TODO some encoding scheme should be applied.
		//
		this.password = encodedPassword;
	}
	String getPassword() {
		return password;
	}

	/**
	 * The CVS repository protocol (<code>ext</code>, <code>pserver</code>, etc).
	 *
	 * @param protocol The CVS protocol (<code>:<b>pserver</b>:asmedes@localhost:2401/home/cvsroot</code>
	 */
	public void setProtocol(String protocol) {
		if (protocol == null) {
			protocol = DEFAULT_PROTOCOL;
		}
		this.protocol = protocol;
	}
	String getProtocol() {
		return protocol;
	}

	/**
	 * The CVS repository port, should be an valid port number.
	 *
	 * @param port The CVS repository path (<code>:pserver:asmedes@localhost:<b>2401</b>/home/cvsroot</code>
	 */
	public void setPort(int port) {
		this.port = port;
	}
	void setPort(String port) {

		int p = -1;
		try {
			p = Integer.parseInt(port);
		} catch (NumberFormatException n) {
			this.port = DEFAULT_PORT;
		}
		this.port = p;
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
		this.repository = repository;
	}
	String getRepository() {
		return repository;
	}

	/**
	 * See {@link #getCVSROOT}.
	 *
	 * @return See {@link #getCVSROOT}.
	 */
	public String toString() {

		try {
			return getCVSROOT();
		} catch (CVSException c) {
			return CVSException.INVALID_CVSROOT.getErrorMessage(Locale.ENGLISH);
		}
	}

	/**
	 * Returns the <code>CVSROOT</code> as a string. Something like <code>:pserver:asmedes@localhost:2401/cvs/pub</code>.
	 *
	 * @return The <code>CVSROOT</code> as a string.
	 * @throws CVSException See {@link CVSException#INVALID_CVSROOT}
	 */
	public String getCVSROOT() throws CVSException {

		// TODO CVSROOT should be validated here by a pattern !
		//

		StringBuffer buffer = new StringBuffer(":" + protocol + ":");

		buffer.append(username).append("@");
		buffer.append(host).append(":");
		buffer.append(port).append(repository.startsWith("/") ? "" : "/");
		buffer.append(repository);

		return buffer.toString();
	}

}
