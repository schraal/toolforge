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

import nl.toolforge.karma.core.KarmaRuntimeException;
import nl.toolforge.karma.core.cmd.ActionCommandResponse;
import nl.toolforge.karma.core.cmd.Command;
import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.cmd.CommandFactory;
import nl.toolforge.karma.core.cmd.CommandLoadException;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.CompositeCommand;
import nl.toolforge.karma.core.cmd.ErrorMessage;
import nl.toolforge.karma.core.cmd.event.CommandResponseEvent;
import nl.toolforge.karma.core.cmd.threads.ParallelCommandWrapper;
import nl.toolforge.karma.core.manifest.Manifest;
import nl.toolforge.karma.core.manifest.ManifestException;
import nl.toolforge.karma.core.manifest.Module;

import java.util.Collection;
import java.util.Iterator;

/**
 * This command updates all modules in the active manifest on a developers' local system.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
//public class UpdateAllModulesCommand extends DefaultCommand {
public class UpdateAllModulesCommand extends CompositeCommand {

  public void commandHeartBeat() {
    // hmmm
  }

  public void commandResponseChanged(CommandResponseEvent event) {
    // hmmm
  }

  public void commandResponseFinished(CommandResponseEvent event) {
    // hmmm
  }

  public void manifestChanged(Manifest manifest) {
    // hmmm
  }

  public CommandResponse getCommandResponse() {
    return commandResponse;
  }

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
   */
  public void execute() throws CommandException {

    // A manifest must be present for this command
    //
    if (!getContext().isManifestLoaded()) {
      throw new CommandException(ManifestException.NO_ACTIVE_MANIFEST);
    }

    Collection modules = getContext().getCurrentManifest().getAllModules().values();

    // Initialize an array of threads.
    //
    ParallelCommandWrapper[] threads = new ParallelCommandWrapper[modules.size()];

    int j = 0;
    for (Iterator i = modules.iterator(); i.hasNext();) {
      Module module = (Module) i.next();

      String commandLineString = "um -m " + module.getName();
      Command clone = null;
      try {
        clone = CommandFactory.getInstance().getCommand(commandLineString);
      } catch (CommandLoadException e) {
        throw new CommandException(e.getErrorCode(), e.getMessageArguments());
      }
      clone.setContext(getContext());

      threads[j] = new ParallelCommandWrapper(clone, getResponseListener());
      threads[j].start();
      j++;
    }

    int totalThreads = threads.length;

    for (int i = 0; i < totalThreads; i++) {

      try {
        threads[i].join();

        if (threads[i].getException() != null) {
          getCommandResponse().addMessage(
              new ErrorMessage(
                  threads[i].getException().getErrorCode(),
                  threads[i].getException().getMessageArguments()
              )
          );
        }
      } catch (InterruptedException e) {
        throw new KarmaRuntimeException(e.getMessage());
      }
    }
  }
}
