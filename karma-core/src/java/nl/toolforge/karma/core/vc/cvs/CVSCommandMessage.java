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

import nl.toolforge.karma.core.cmd.AbstractCommandMessage;

/**
 * Message implementation for CVS messages.
 *
 *
 * @author D.A. Smedes
 * @version $Id$
 *
 * @deprecated Use direct subclasses of <code>AbstractCommandMessage</code> instead.
 */
public final class CVSCommandMessage extends AbstractCommandMessage {

	public CVSCommandMessage(String message) {
		super(message);
	}

	public CVSCommandMessage(String message, Object[] parameters) {
		super(message, parameters);
	}

}