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

import nl.toolforge.karma.core.Version;
import nl.toolforge.karma.core.cmd.ActionCommandResponse;
import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.DefaultCommand;
import nl.toolforge.karma.core.cmd.ErrorMessage;
import nl.toolforge.karma.core.cmd.SuccessMessage;
import nl.toolforge.karma.core.manifest.Manifest;
import nl.toolforge.karma.core.manifest.ManifestException;
import nl.toolforge.karma.core.manifest.Module;
import nl.toolforge.karma.core.manifest.ReleaseManifest;
import nl.toolforge.karma.core.vc.Runner;
import nl.toolforge.karma.core.vc.RunnerFactory;
import nl.toolforge.karma.core.vc.VersionControlException;
import nl.toolforge.karma.core.vc.cvsimpl.AdminHandler;
import nl.toolforge.karma.core.vc.cvsimpl.Utils;

/**
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public class StopWorkCommand extends DefaultCommand {

  protected CommandResponse response = null;
  /**
   *
   * @param descriptor The command descriptor for this command.
   */
  public StopWorkCommand(CommandDescriptor descriptor) {

    super(descriptor);

    response = new ActionCommandResponse();
  }

  public void execute() throws CommandException {

    String moduleName = "";
    Module module = null;

    if (!getContext().isManifestLoaded()) {
      throw new CommandException(ManifestException.NO_ACTIVE_MANIFEST);
    }

    moduleName = getCommandLine().getOptionValue("m");
    try {
      module = getContext().getCurrentManifest().getModule(moduleName);
    } catch (ManifestException e) {
      throw new CommandException(e.getErrorCode(), e.getMessageArguments());
    }

//    if (!(module instanceof SourceModule)) {
//      throw new CommandException(CommandException.MODULE_TYPE_MUST_BE_SOURCEMODULE, new Object[] {module.getName()});
//    }

    if (!Module.WORKING.equals(getContext().getCurrentManifest().getState(module))) {
      throw new CommandException(CommandException.INVALID_STATE_MODULE_NOT_WORKING, new Object[]{moduleName});
    }

    // Detect new files.
    //
    AdminHandler handler = new AdminHandler(module);
    handler.administrate();

    boolean proceed = true;
    if (handler.hasNewStuff()) {
      response.addMessage(new ErrorMessage("ERROR : Module " + moduleName + " has new, but uncommitted files."));
      proceed = false;
    }
    if (handler.hasChangedStuff()) {
      response.addMessage(new ErrorMessage("ERROR : Module " + moduleName + " has changed, but uncommitted files."));
      proceed = false;
    }
    if (handler.hasRemovedStuff()) {
      response.addMessage(new ErrorMessage("ERROR : Module " + moduleName + " has removed, but uncommitted files."));
      proceed = false;
    }

    if (proceed) {

      Manifest m = getContext().getCurrentManifest();
      try {

        Version version = Utils.getLastVersion(module);

        // Update to the latest available version (in a DevelopmentLine).
        //
        Runner runner = RunnerFactory.getRunner(module.getLocation());
        runner.checkout(module, version);

        if (m instanceof ReleaseManifest) {
          m.setState(module, Module.STATIC);
        } else {
          // Only development manifests can have dynamic modules.
          //
          m.setState(module, Module.DYNAMIC);
        }
      } catch (VersionControlException e) {
        throw new CommandException(e.getErrorCode(), e.getMessageArguments());
      }
      response.addMessage(
          new SuccessMessage(
              getFrontendMessages().getString("message.STOP_WORK_SUCCESSFULL"),
              new Object[]{module.getName(), m.getState(module).toString()}));
    } else {
      response.addMessage(new SuccessMessage("Module " + moduleName + " still WORKING."));
    }
  }

  public CommandResponse getCommandResponse() {
    return response;
  }
}
