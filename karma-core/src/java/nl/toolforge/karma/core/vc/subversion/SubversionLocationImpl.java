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
package nl.toolforge.karma.core.vc.subversion;

import nl.toolforge.karma.core.location.BaseLocation;
import nl.toolforge.karma.core.location.Location;
import nl.toolforge.karma.core.location.LocationType;

/**
 * @author D.A. Smedes
 * @version $Id$
 */
public final class SubversionLocationImpl extends BaseLocation {

	private String username = null;
	private String password = null;

	public SubversionLocationImpl(String id) {
		super(id, LocationType.SUBVERSION);
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String encodedPassword) {

		// TODO some encoding scheme should be applied.
		//
		password = encodedPassword;
	}

  /**
   * Always true; Subversion not yet supported.
   * @return
   */
  public boolean isAvailable() {
    return true;
  }
}
