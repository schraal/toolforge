package nl.toolforge.karma.core.location;

import nl.toolforge.karma.core.KarmaException;

/**
 * <code>BaseLocation</code> implements some generic <code>Location</code> functionality.
 *
 * @author D.A. Smedes
 */
public abstract class BaseLocation implements Location {

	private String id = null;
	private Location.Type type = null;

	/**
	 * Constructs a <code>Location</code> skeleton.
	 *
	 * @param id The unique identifier for the location. Cannot be null.
	 * @param type The type of the location. Cannot be null.
	 *
	 * @throws nl.toolforge.karma.core.KarmaException When either of the parameters is <code>null</code>.
	 */
	public BaseLocation(String id, Location.Type type) throws KarmaException {

		if (id == null) {
			throw new KarmaException(KarmaException.LAZY_BASTARD);
		}
		if (type == null) {
			throw new KarmaException(KarmaException.LAZY_BASTARD);
		}
		this.id = id;
		this.type = type;
	}

	public final Location.Type getType() {
		return type;
	}

	public final String getId() {
        return id;
	}
}
