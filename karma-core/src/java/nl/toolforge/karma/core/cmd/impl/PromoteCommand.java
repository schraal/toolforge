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
import nl.toolforge.karma.core.manifest.Manifest;
import nl.toolforge.karma.core.manifest.ManifestException;
import nl.toolforge.karma.core.manifest.Module;
import nl.toolforge.karma.core.vc.ModuleStatus;
import nl.toolforge.karma.core.vc.Runner;
import nl.toolforge.karma.core.vc.RunnerFactory;
import nl.toolforge.karma.core.vc.VersionControlException;
import nl.toolforge.karma.core.vc.cvs.CVSModuleStatus;
import nl.toolforge.karma.core.vc.cvs.CVSRunner;

/**
 * Implementation of the 'codeline freeze' concept. Karma increases a modules' version (using whichever pattern is
 * defined for it), thus allowing for a freeze. Development can commence immediately on the module. In that sense, it
 * is not a freeze, just a tiny hick-up in the development process, as modules are generally small in nature.
 *
 * @author D.A. Smedes
 * @author W.H. Schraal
 * @version $Id$
 */
public class PromoteCommand extends DefaultCommand {

  private Version newVersion = null;
  protected CommandResponse commandResponse = new ActionCommandResponse();

  public PromoteCommand(CommandDescriptor descriptor) {
    super(descriptor);
  }

  /**
   * Promotes a module to the next version number in the branch it is active in within the active manifest.
   */
  public void execute() throws CommandException {

    // todo move to aspect; this type of checking can be done by one aspect.
    //
    if (!getContext().isManifestLoaded()) {
      throw new CommandException(ManifestException.NO_ACTIVE_MANIFEST);
    }

    try {

      String moduleName = getCommandLine().getOptionValue("m");
      String comment = getCommandLine().getOptionValue("c");

      Manifest manifest = getContext().getCurrentManifest();
      Module module = manifest.getModule(moduleName);

      if (!manifest.getState(module).equals(Module.WORKING)) {
        throw new CommandException(CommandException.PROMOTE_ONLY_ALLOWED_ON_WORKING_MODULE, new Object[]{moduleName});
      }

      Runner runner = RunnerFactory.getRunner(module.getLocation());

      ModuleStatus status = new CVSModuleStatus(module, ((CVSRunner) runner).log(module));
      Version nextVersion = status.getNextVersion();

      if (getCommandLine().getOptionValue("v") != null) {

        Version manualVersion = new Version(getCommandLine().getOptionValue("v"));
        if (manualVersion.isLowerThan(status.getLastVersion())) {
          throw new CommandException(
              CommandException.MODULE_VERSION_ERROR,
              new Object[]{manualVersion.getVersionNumber(), status.getLastVersion().getVersionNumber()}
          );
        }
        nextVersion = manualVersion;
      }

      newVersion = nextVersion;

      // TODO check whether files exist that have not yet been committed.
      runner.promote(module, comment, newVersion);

    } catch (ManifestException e) {
      throw new CommandException(e.getErrorCode(), e.getMessageArguments());
    } catch (VersionControlException e) {
      throw new CommandException(e.getErrorCode(), e.getMessageArguments());
    }
  }

  public CommandResponse getCommandResponse() {
    return this.commandResponse;
  }

  /**
   * Returns the new version number for the module, or <code>null</code> when no version number could be set.
   *
   * @return The new version number for the module, or <code>null</code> when no version number could be set.
   */
  protected final Version getNewVersion() {
    return newVersion;
  }
}

