package nl.toolforge.karma.core;

import nl.toolforge.karma.core.location.Location;

/**
 * Base object representation of a <code>&lt;module&gt;</code>-element in a manifest file. See extensions for more
 * specific information.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public abstract class ModuleDescriptor {

  public static final String DESCRIPTION_ATTRIBUTE = "description";

	/** The <code>name</code>-attribute for a module. */
	public static final String NAME_ATTRIBUTE = "name";

	/** The <code>location</code>-attribute for a module. */
	public static final String LOCATION_ATTRIBUTE = "location";

	/** The <code>name</code>-attribute for an <code>include</code>-element. */
	public static final String INCLUDE_NAME_ATTRIBUTE = "name";

  private String name = null;
  private Version version = null;
  private Location location = null;

  /**
   *
   * @param name
   * @param location
   * @throws ManifestException
   */
  public ModuleDescriptor(String name, Location location) throws ManifestException {

    if (name == null) {
      throw new IllegalArgumentException("Module 'name' cannot be null.");
    }
    if (location == null) {
      throw new IllegalArgumentException("Module 'location' cannot be null.");
    }

    this.name = name;
    this.location = location;
  }

  public ModuleDescriptor(String name, Location location, Version version) throws ManifestException {
    this(name, location);
    this.version = version;
  }

  public String getName() {
    return name;
  }

  public Version getVersion() {
    return version;
  }

  public Location getLocation() {
    return location;
  }

  /**
   * Modules are equal when their names and their locations are equal.
   *
   * @param o The object instance that should be compared with <code>this</code>.
   * @return <code>true</code> if this module descriptor
   */
  public boolean equals(Object o) {

    if (o instanceof ModuleDescriptor) {
      if (
        (getName().equals(((ModuleDescriptor) o).getName())) &&
        (getLocation().equals(((ModuleDescriptor) o).getLocation())) ) {
        return true;
      } else {
        return false;
      }
    } else {
      return false;
    }
  }

  public int hashCode() {
    return name.hashCode() + version.hashCode() + location.hashCode();
  }

}


