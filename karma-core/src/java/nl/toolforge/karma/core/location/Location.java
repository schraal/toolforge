package nl.toolforge.karma.core.location;

/**
 * <p>A <code>Location</code> describes a location aspect of a module. Source modules are kept in a version control
 * system, binary (third party) modules are kept in libraries. These locations are maintained in
 * <code>locations.xml</code>.
 * <p/>
 * <p>A developer should maintain a <code>location-authentication.xml</code> file in the Karma configuration directory.
 * This file contains
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public interface Location {

	/**
	 * The locations' type descriptor.
	 *
	 * @return A <code>Location.Type</code> instance.
	 */
	public Type getType();

	/**
	 * A locations' identifier. Should be unique over all <code>location</code>-elements. This id is
	 * matched against the <code>id</code>-attribute of a <code>location</code>-element in the
	 * <code>location-authentication.xml</code> file (see class documentation {@link nl.toolforge.karma.core.location.Location}.
	 *
	 * @return An identifier string for a location.
	 */
	public String getId();

	/**
	 * Enumeration for the location type.
	 *
	 * @author D.A. Smedes
	 */
	public final class Type {

		/**
		 * A CVS repository location
		 */
		public static final Type CVS_REPOSITORY = new Type("CVS-REPOSITORY");

		/**
		 * A Subversion repository location
		 */
		public static final Type SUBVERSION_REPOSITORY = new Type("SUBVERSION-REPOSITORY");

		/**
		 * A Subversion repository location
		 */
		public static final Type MAVEN_REPOSITORY = new Type("MAVEN-REPOSITORY");

		String type = null;

		private Type(String type) {
			this.type = type;
		}

		/**
		 * Shows which type we're talking about.
		 *
		 * @return A lowercase string for the specific type of location.
		 */
		public String toString() {
			return this.type.toLowerCase();
		}
	}
}
