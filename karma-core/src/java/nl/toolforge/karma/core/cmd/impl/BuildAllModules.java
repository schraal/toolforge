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

import nl.toolforge.karma.core.cmd.Command;
import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.cmd.CommandFactory;
import nl.toolforge.karma.core.cmd.CommandLoadException;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.DefaultCommand;
import nl.toolforge.karma.core.cmd.event.CommandFailedEvent;
import nl.toolforge.karma.core.cmd.event.ErrorEvent;
import nl.toolforge.karma.core.cmd.util.KarmaBuildException;
import nl.toolforge.karma.core.manifest.Manifest;
import nl.toolforge.karma.core.manifest.ManifestException;
import nl.toolforge.karma.core.manifest.Module;
import nl.toolforge.karma.core.scm.ModuleDependency;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Builds all modules in a manifest.
 *
 * @author D.A. Smedes
 * @version $Id$
 *
 * @deprecated Not in use, but the current code remains. Not to be used.
 */
public class BuildAllModules extends DefaultCommand {

  private CommandResponse commandResponse = new CommandResponse();
  private List orderedCollection = null;
  private Manifest currentManifest = null;

  public BuildAllModules(CommandDescriptor descriptor) {
    super(descriptor);
  }

  public void execute() throws CommandException {

    currentManifest = getContext().getCurrentManifest();

    try {
      orderedCollection = new ArrayList();

      createBuildList(currentManifest.getAllModules().values());

    } catch (KarmaBuildException e) {
      throw new CommandException(e, CommandException.BUILD_FAILED);
    } catch (ManifestException e) {
      throw new CommandException(e.getErrorCode(), e.getMessageArguments());
    }

    for (Iterator i = orderedCollection.iterator(); i.hasNext();) {

      Module module = (Module) i.next();

      Command command = null;
      try {
        String commandLineString = PackageModule.COMMAND_NAME + " -m " + module.getName() + " -n";

        command = CommandFactory.getInstance().getCommand(commandLineString);
        command.setContext(getContext());
        command.registerCommandResponseListener(getResponseListener());
        command.execute();
        
      } catch (CommandException ce) {
        if (ce.getErrorCode().equals(CommandException.DEPENDENCY_DOES_NOT_EXIST) ||
            ce.getErrorCode().equals(CommandException.BUILD_FAILED) ) {

          commandResponse.addEvent(new CommandFailedEvent(this, ce));
//          commandResponse.addEvent(new ErrorMessage(ce.getErrorCode(), ce.getMessageArguments()));
          throw new CommandException(ce, CommandException.TEST_FAILED, new Object[]{module.getName()});
        } else {
//          Message message = new ErrorMessage(ce.getErrorCode(), ce.getMessageArguments());
          commandResponse.addEvent(new CommandFailedEvent(this, ce));

          commandResponse.addEvent(new ErrorEvent(CommandException.BUILD_WARNING));
        }
      } catch (CommandLoadException e) {
        throw new CommandException(e.getErrorCode(), e.getMessageArguments());
      } finally {
        if ( command != null ) {
          command.deregisterCommandResponseListener(getResponseListener());
        }
      }
    }
  }

  private void createBuildList(Collection collection) throws ManifestException, KarmaBuildException {

    for (Iterator i = collection.iterator(); i.hasNext();) {

      Module module = (Module) i.next();

      Set deps = module.getDependencies();
      if (deps.size() == 0) {
        orderedCollection.add(module);
      } else {
        Set mods = new HashSet();
        for (Iterator j = deps.iterator(); j.hasNext();) {
          ModuleDependency dep = (ModuleDependency) j.next();
          Module mod = null;
          if (dep.isModuleDependency()) {
            mod = currentManifest.getModule(dep.getModule());
            if (!orderedCollection.contains(mod)) {
              mods.add(mod);
            }
          }
        }

        if (mods.size() == 0) {
          orderedCollection.add(module);
        } else {
          createBuildList(mods);
        }
      }
    }
  }

  public CommandResponse getCommandResponse() {
    return this.commandResponse;
  }
}
