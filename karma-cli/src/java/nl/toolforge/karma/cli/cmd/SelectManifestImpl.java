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

import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.cmd.CommandMessage;
import nl.toolforge.karma.core.cmd.SuccessMessage;
import nl.toolforge.karma.core.cmd.event.ManifestChangedEvent;
import nl.toolforge.karma.core.cmd.impl.SelectManifest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author W.H. Schraal
 *
 * @version $Id$
 */
public class SelectManifestImpl extends SelectManifest {

	private static Log logger = LogFactory.getLog(SelectManifestImpl.class);

  public SelectManifestImpl(CommandDescriptor descriptor) {
    super(descriptor);
  }

  /**
   * Execute the command in the CLI. When the execution was succesfull, a message is shown on the console.
   *
   */
  public void execute() throws CommandException {

    // Use stuff that's being done in the superclass.
    //
    super.execute(); // Ignore the response from the superclass

    getCommandResponse().addMessage(new ManifestChangedEvent(getSelectedManifest()));

		CommandMessage message =
        new SuccessMessage(
            getFrontendMessages().getString("message.MANIFEST_ACTIVATED"), new Object[]{getSelectedManifest().getName()}
        );
    getCommandResponse().addMessage(message);
  }
}
