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
package nl.toolforge.karma.core.vc.svnimpl;

import nl.toolforge.karma.core.location.LocationException;
import nl.toolforge.karma.core.location.LocationType;
import nl.toolforge.karma.core.vc.VersionControlSystem;

/**
 * <p>Subversion implementation of the version control system.
 * <p/>
 * <p>Have a look at http://subversion.tigris.org for more information on the subversion
 * vcs.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public class SubversionRepository extends VersionControlSystem {

  public SubversionRepository(String id) {
    super(id, LocationType.SUBVERSION);
  }

  public boolean isAvailable() {
    return false;
  }

  public void connect() throws LocationException {

  }

  public StringBuffer asXML() {
    return new StringBuffer("<location type=\"subversion\" id=\"\"");
  }
}
