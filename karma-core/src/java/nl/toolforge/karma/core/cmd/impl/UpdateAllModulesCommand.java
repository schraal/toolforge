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

import nl.toolforge.karma.core.cmd.ActionCommandResponse;
import nl.toolforge.karma.core.cmd.Command;
import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.cmd.CommandFactory;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.CompositeCommand;
import nl.toolforge.karma.core.cmd.ErrorMessage;
import nl.toolforge.karma.core.cmd.event.CommandResponseEvent;
import nl.toolforge.karma.core.manifest.ManifestException;
import nl.toolforge.karma.core.manifest.Module;

import java.util.Iterator;
import java.util.Map;

/**
 * This command updates all modules in the active manifest on a developers' local system.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
//public class UpdateAllModulesCommand extends DefaultCommand {
public class UpdateAllModulesCommand extends CompositeCommand {

  private CommandResponse commandResponse = new ActionCommandResponse();

  private boolean errorOccurred = false;

  /**
   * Creates a <code>UpdateAllModulesCommand</code> for module <code>module</code> that should be updated.
   *
   * @param descriptor The command descriptor for this command.
   */
  public UpdateAllModulesCommand(CommandDescriptor descriptor) {
    super(descriptor);
  }

  /**
   * This command will update all modules in the active manifest from the version control system. An update is done when
   * the module is already present, otherwise a checkout will be performed. The checkout directory for the module
   * is relative to the root directory of the <code>active</code> manifest.
   *
   */
  public void execute() throws CommandException {
    
    // A manifest must be present for this command
    //
    if (!getContext().isManifestLoaded()) {
      throw new CommandException(ManifestException.NO_ACTIVE_MANIFEST);
    }

    // todo what to do about jarmodules etc ?
    //
    Map modules = getContext().getCurrentManifest().getAllModules();

    // Loop through all modules and use UpdateModuleCommand on each module.
    //
    for (Iterator i = modules.keySet().iterator(); i.hasNext() && !errorOccurred;) {

      Module module = (Module) modules.get(i.next());

      // todo hmm, the commandname is hardcoded whilst we have it dynamically in a file ...
      //
      //getContext().execute(CommandDescriptor.UPDATE_MODULE_COMMAND + " -m ".concat(module.getName()));
      //todo cast to updatecommand?
      Command command = CommandFactory.getInstance().getCommand(CommandDescriptor.UPDATE_MODULE_COMMAND + " -m ".concat(module.getName()));
      command.registerCommandResponseListener(this);
      try {
        getContext().execute(command);
      } catch (CommandException c) {
        commandResponse.addMessage(new ErrorMessage(c.getErrorCode()));
        break; //break out of the for loop.
      } finally {
        command.deregisterCommandResponseListener(this);
      }
    }
  }

  public CommandResponse getCommandResponse() {
    return commandResponse;
  }

  public void commandHeartBeat() {
    // todo implementation required
  }

  /**
   *
   *
   * @param event
   */
  public void commandResponseChanged(CommandResponseEvent event) {
    //check what the change is. In case of an error, we want to give an error
    //message and stop with updating the modules.
    if (event.getEventMessage() instanceof ErrorMessage) {
      errorOccurred = true;  
    }
  }

  public void commandResponseFinished(CommandResponseEvent event) {
    //the update module has finished. which is good. nothing to do.
  }

}
