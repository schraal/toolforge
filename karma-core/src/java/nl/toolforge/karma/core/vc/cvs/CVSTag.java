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
package nl.toolforge.karma.core.vc.cvs;

import nl.toolforge.karma.core.vc.SymbolicName;

/**
 * Symbolic names in CVS are implemented in two ways: 'sticky-tag' and 'branch'.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public class CVSTag implements SymbolicName {

	private String symbolicName = null;

	/**
	 * Creates a CVS tag with <code>symbolicName</code>.
	 */
	public CVSTag(String symbolicName) {

		// TODO parse through the SymbolicNameParser
		//
		this.symbolicName = symbolicName;
	}

	public CVSTag(String symbolicName, boolean isBranch) {
		this.symbolicName = symbolicName;
	}

//	/**
//	 * Constructs a CVS tag, based on the <code>version</code> supplied.
//	 *
//	 * @param version A <code>Version</code> instance, which represents the current module version.
//	 */
//	public CVSTag(Module module, Version version) {
//
//		// TODO this implementation is temporary, and should reflect the actual situation in a better way.
//		//
//		symbolicName = module.getName().concat("_").concat(version.getVersionNumber());
//	}

	public boolean isBranch() {
		return false;
	}

	/**
	 * Negation of {@link #isBranch}.
	 *
	 * @return <code>true</code> if this symbolic name is 'sticky' (not a branch tag).
	 */
	public boolean isSticky() {
		return !isBranch();
	}

	public String getSymbolicName() {
		return symbolicName;
	}

	public String toString() {
		return symbolicName;
	}

	public int hashCode() {
		return symbolicName.hashCode();
	}

	public boolean equals(Object o) {

		if (o instanceof CVSTag) {
			return ((CVSTag) o).symbolicName.equals(symbolicName);
		}

		return false;
	}
}