package nl.toolforge.karma.core.vc.subversion;

import nl.toolforge.karma.core.location.BaseLocation;
import nl.toolforge.karma.core.location.Location;
import nl.toolforge.karma.core.KarmaException;

/**
 *
 * @author D.A. Smedes
 */
public final class SubversionLocationImpl extends BaseLocation {

	private String username = null;
	private String password = null;

	public SubversionLocationImpl(String id) throws KarmaException {
         super(id, Location.Type.SUBVERSION_REPOSITORY);
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String encodedPassword) {

		// TODO some encoding scheme should be applied.
		//
		password = encodedPassword;
	}
}