package nl.toolforge.karma.core;

/**
 * A <code>Location</code> describes a location aspect of a module. Source modules are kept in a version control
 * system, binary (third party) modules are kept in libraries. These locations are maintained in
 *
 * @author D.A. Smedes
 */
public interface Location {

	/**
	 * A <code>Location</code> is indentified by an alias, which is generally a lookup in some configuration resource.
	 *
	 * @return The alias for the location object.
	 */
	public String getAlias();
}
