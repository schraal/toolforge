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
import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.DefaultCommand;
import nl.toolforge.karma.core.manifest.Manifest;
import nl.toolforge.karma.core.manifest.ManifestException;
import nl.toolforge.karma.core.module.Module;
import nl.toolforge.karma.core.module.ModuleComparator;
import nl.toolforge.karma.core.module.ModuleTypeException;
import nl.toolforge.karma.core.vc.ModuleStatus;
import nl.toolforge.karma.core.vc.VersionControlException;
import nl.toolforge.karma.core.vc.cvsimpl.threads.CVSLogThread;
import nl.toolforge.karma.core.vc.threads.ParallelRunner;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This command gets the active manifest and presents it in the UI. UI implementations are responsible for the rendering
 * part.
 */
public class ViewManifest extends DefaultCommand {

  Log logger = LogFactory.getLog(ViewManifest.class);

  private List renderedList = null;
  private CommandResponse commandResponse = new CommandResponse();

  public ViewManifest(CommandDescriptor descriptor) {
    super(descriptor);

    renderedList = new ArrayList();
  }

  public void execute() throws CommandException {

    if (!getContext().isManifestLoaded()) {
      throw new CommandException(ManifestException.NO_ACTIVE_MANIFEST);
    }
    Manifest manifest = getContext().getCurrentManifest();

    List sourceModules = new ArrayList();

    // Transform and sort
    //

    // todo hmm kan dit niet simpeler ?
    //
    Collection c = manifest.getAllModules().values();

    for (Iterator i = c.iterator(); i.hasNext();) {
      sourceModules.add(i.next());
    }

    Collections.sort(sourceModules, new ModuleComparator());

    ParallelRunner runner = new ParallelRunner(manifest, CVSLogThread.class);
    runner.execute(100); // Blocks ...

    Map statusOverview = runner.retrieveResults();

    for (Iterator i = sourceModules.iterator(); i.hasNext();) {

      Module module = (Module) i.next();

      ModuleStatus moduleStatus = (ModuleStatus) statusOverview.get(module);

      String[] moduleData = new String[8];
      moduleData[0] = module.getName();

      boolean existsInRepository = moduleStatus.existsInRepository();

      try {

        if (!existsInRepository) {
          moduleData[1] = Module.UNKNOWN.getType();
        } else {
          try {
            moduleData[1] = module.getType().getShortType();
          } catch (ModuleTypeException e) {
            //this exception occurs when a module has not yet been checked out.
            //therefore, only a warning.
            logger.warn(e);
            moduleData[1] = Module.UNKNOWN.getType();
          }
        }

        if (!manifest.isLocal(module)) {
          moduleData[2] = "N/A";
        } else {
          if (manifest.getState(module).equals(Module.WORKING)) {
            moduleData[2] = "HEAD";
          } else {
            Version localVersion = moduleStatus.getLocalVersion();
            moduleData[2] = (localVersion == null ? "" : localVersion.getVersionNumber());
          }
        }

        if (existsInRepository) {
          Version remoteVersion = moduleStatus.getLastVersion();
          moduleData[3] = (remoteVersion == null ? "" : "(" + remoteVersion.getVersionNumber() + ")");
        } else {
          moduleData[3] = "";
        }

      } catch (VersionControlException v) {
        // Version for the module is non-existing in the repository.
        //
        throw new CommandException(v.getErrorCode(), v.getMessageArguments());
      }

      moduleData[4] = "(" + module.getVersionAsString() + ")";
      if ( moduleData[4].equals("(N/A)") ) {
        moduleData[4] = "";
      }
      moduleData[5] = (module.hasPatchLine() ? "available" : "not available");

      if (existsInRepository) {
        moduleData[6] = manifest.getState(module).toString();
        moduleData[7] = module.getLocation().getId();
      } else {
        moduleData[6] = "";
        if (moduleStatus.connectionFailure()) {
          moduleData[7] = "<Connection failed>";
        } else if (moduleStatus.authenticationFailure()) {
          moduleData[7] = "<Authentication failed>";
        } else {
          moduleData[7] = "<Not in repository>";
        }
      }
      renderedList.add(moduleData);
    }
  }

  /**
   * Gets the commands' response object.
   *
   * @return The commands' response object.
   */
  public CommandResponse getCommandResponse() {
    return this.commandResponse;
  }

  /**
   * <p>Returns the contents of the manifest in a two-dimensional <code>String[]</code> data-structure, for easy
   * reference. The contents of this structure can be queried through <code>renderedList.get(i)</code>. This call
   * retrieves a <code>String[]</code> with the most important data items for each module.
   *
   * @return A <code>List</code> containing <code>String[]</code> instances.
   */
  protected List getData() {
    return renderedList;
  }
}
