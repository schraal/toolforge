package nl.toolforge.karma.core.vc.cvs;

import nl.toolforge.karma.core.location.Location;
import nl.toolforge.karma.core.KarmaRuntimeException;
import org.netbeans.lib.cvsclient.connection.PServerConnection;
import org.netbeans.lib.cvsclient.connection.StandardScrambler;

/**
 * Class representing a connection to a CVS repository. The current implementation can only deliver
 * <code>org.netbeans.lib.cvsclient.connection.PServerConnection</code>s.
 *
 * @author D.A. Smedes 
 * 
 * @version $Id:
 *
 */
public final class CVSConnection {

	private CVSLocationImpl location = null;

	/**
	 * Constructor for a CVSConnection. This class is initialized using a <code>CVSLocationImpl</code>, which knows all
	 * intricacies of connection to a CVS repository.
	 *
	 * @param location The location descriptor (<code>CVSLocationImpl</code> instance).
	 */
	public CVSConnection(Location location) {

		try {
      this.location = (CVSLocationImpl) location;
		} catch (ClassCastException c) {
			throw new KarmaRuntimeException(
				"Wrong implementation of Location interface. Must be a CVSLocationImpl instance.", c);
		}
	}

	/**
	 * The <code>org.netbeans.lib.cvsclient PServerConnection</code> implementation. This implementation is required
	 * when a <code>org.netbeans.lib.cvsclient.Client</code> connection is used to fire away on the CVS repository.
	 *
	 * @return A <code>PServerConnection</code> initialized with connection data for a CVS repository.
	 */
	public PServerConnection getConnection() {

		PServerConnection connection = new PServerConnection();

		connection.setHostName(location.getHost());
		connection.setUserName(location.getUsername());
		connection.setEncodedPassword(StandardScrambler.getInstance().scramble(location.getPassword()));
		connection.setPort(location.getPort());
		connection.setRepository(location.getRepository());

		return connection;
	}

}