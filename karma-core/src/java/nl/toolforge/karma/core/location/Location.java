/*
Karma core - Core of the Karma application
Copyright (C) 2004  Toolforge <www.toolforge.nl>

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/
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
   * Is the (remote) location available on the specified port ? Usefull to implement for remote locations.
   * @return <code>false</code> if the location is not available, <code>true</code> if it is.
   */
  public boolean ping();

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
