package nl.toolforge.karma.core.location;



/**
 * <code>BaseLocation</code> implements some generic <code>Location</code> functionality.
 *
 * @author D.A. Smedes
 *
 * @version $Id$
 */
public abstract class BaseLocation implements Location {

	private String id = null;
	private Location.Type type = null;

	/**
	 * Constructs a <code>Location</code> skeleton.
	 *
	 * @param id The unique identifier for the location. Cannot be null.
	 * @param type The type of the location. Cannot be null.
	 */
	public BaseLocation(String id, Location.Type type) {

		if (id == null) {
			throw new IllegalArgumentException("Location id must be set.");
		}
		if (type == null) {
			throw new IllegalArgumentException("Location must be set.");
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
