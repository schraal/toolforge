package nl.toolforge.karma.core.vc.cvs;

import nl.toolforge.karma.core.KarmaException;
import nl.toolforge.karma.core.location.BaseLocation;
import nl.toolforge.karma.core.location.Location;

/**
 *
 * @author D.A. Smedes
 *
 * @version $Id$
 */
public final class CVSLocationImpl extends BaseLocation {

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
	public final void setHost(String host) {
		this.host = host;
	}

	public final String getHost() {
		return host;
	}

	/**
	 * The CVS username.
	 *
	 * @param username The CVS username path (<code>:pserver:<b>asmedes</b>@localhost:2401/home/cvsroot</code>
	 */
	public final void setUsername(String username) {
		this.username = username;
	}
	public final String getUsername() {
		return username;
	}

	/**
	 * The CVS password in the normal (<b>* insecure *</b>) format. This password can generally be found in
	 * <code>${user.home}/.cvspass</code>.
	 *
	 * @param encodedPassword The CVS password in the normal (<b>* insecure *</b>) password.
	 */
	public final void setPassword(String encodedPassword) {

		// TODO some encoding scheme should be applied.
		//
		this.password = encodedPassword;
	}
	final String getPassword() {
		return password;
	}

	/**
	 * The CVS repository protocol (<code>ext</code>, <code>pserver</code>, etc).
	 *
	 * @param protocol The CVS protocol (<code>:<b>pserver</b>:asmedes@localhost:2401/home/cvsroot</code>
	 */
	public final void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	public final String getProtocol() {
		return protocol;
	}

	/**
	 * The CVS repository port.
	 *
	 * @param port The CVS repository path (<code>:pserver:asmedes@localhost:<b>2401</b>/home/cvsroot</code>
	 */
	public final void setPort(int port) {
		this.port = port;
	}
	public final int getPort() {
		return port;
	}

	/**
	 * The CVS repository string.
	 *
	 * @param repository The CVS repository path (<code>:pserver:asmedes@localhost:2401<b>/home/cvsroot</b></code>
	 */
	public final void setRepository(String repository) {
		this.repository = repository;
	}
	public final String getRepository() {
		return repository;
	}
}
