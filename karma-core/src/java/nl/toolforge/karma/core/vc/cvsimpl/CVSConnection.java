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
package nl.toolforge.karma.core.vc.cvsimpl;

import nl.toolforge.karma.core.KarmaRuntimeException;
import nl.toolforge.karma.core.location.Location;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Class representing a connection to a CVS repository. The current implementation can only deliver
 * <code>org.netbeans.lib.cvsclient.connection.PServerConnection</code>s.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public final class CVSConnection {

	private static Log logger = LogFactory.getLog(CVSConnection.class);
	private CVSRepository location = null;

	/**
	 * Constructor for a CVSConnection. This class is initialized using a <code>CVSRepository</code>, which knows all
	 * intricacies of connection to a CVS repository.
	 *
	 * @param location The location descriptor (<code>CVSRepository</code> instance).
	 */
	public CVSConnection(Location location) {

		try {
			this.location = (CVSRepository) location;
		} catch (ClassCastException c) {
			throw new KarmaRuntimeException("Wrong implementation of Location interface. Must be a CVSRepository instance.", c);
		}
	}
}