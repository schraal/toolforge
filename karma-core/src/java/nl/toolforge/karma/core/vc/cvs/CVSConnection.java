package nl.toolforge.karma.core.vc.cvs;

import nl.toolforge.karma.core.KarmaRuntimeException;
import nl.toolforge.karma.core.location.Location;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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

	private static Log logger = LogFactory.getLog(CVSConnection.class);
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
}