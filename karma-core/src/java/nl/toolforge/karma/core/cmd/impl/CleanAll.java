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
package nl.toolforge.karma.core.cmd.impl;

import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.event.MessageEvent;
import nl.toolforge.karma.core.cmd.event.SimpleMessage;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Removes the build directory to create a totally clean environment.
 *
 * @author W.H. Schraal
 * @author D.A. Smedes
 * @version $Id$
 */
public class CleanAll extends AbstractBuildCommand {

  private CommandResponse commandResponse = new CommandResponse();

  public CleanAll(CommandDescriptor descriptor) {
    super(descriptor);
  }

  /**
   * Removes the <code>build</code> directory.
   *
   * @throws CommandException
   */
  public void execute() throws CommandException {

    try {

//      FileUtils.deleteDirectory(new File(getCurrentManifest().getBaseDirectory(), "build"));
      FileUtils.deleteDirectory(getCurrentManifest().getBuildBaseDirectory());

      // todo internationalization
      //
      commandResponse.addEvent(new MessageEvent(this, new SimpleMessage("Manifest has been cleaned.")));

    } catch (IOException e) {
      throw new CommandException(CommandException.CLEAN_ALL_FAILED, new Object[]{e.getLocalizedMessage()});
    }
  }

  public CommandResponse getCommandResponse() {
    return this.commandResponse;
  }
}
