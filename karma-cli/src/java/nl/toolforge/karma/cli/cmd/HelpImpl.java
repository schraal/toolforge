/*
Karma CLI - Command Line Interface for the Karma application
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
package nl.toolforge.karma.cli.cmd;

import nl.toolforge.karma.cli.CommandRenderer;
import nl.toolforge.karma.core.cmd.*;
import nl.toolforge.karma.core.cmd.impl.HelpCommand;
import nl.toolforge.karma.core.bundle.BundleCache;

import java.util.ResourceBundle;

/**
 * @author W.G.Helmantel
 * @version $Id$
 */
public class HelpImpl extends HelpCommand {

	private static final ResourceBundle FRONTEND_MESSAGES = BundleCache.getInstance().getBundle(BundleCache.FRONTEND_MESSAGES_KEY);

	public HelpImpl(CommandDescriptor descriptor) {
		super(descriptor);
	}

	public void execute() throws CommandException {

		CommandMessage message = new SuccessMessage("\n\n" + FRONTEND_MESSAGES.getString("message.VALID_COMMANDS") + "\n" + CommandRenderer.renderedCommands(CommandFactory.getInstance().getCommands()));
		response.addMessage(message);
		super.execute();
	}

}
