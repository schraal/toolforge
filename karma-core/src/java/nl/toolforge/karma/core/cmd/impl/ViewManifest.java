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
import nl.toolforge.karma.core.manifest.ModuleComparator;
import nl.toolforge.karma.core.vc.ModuleStatus;
import nl.toolforge.karma.core.vc.VersionControlException;
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
  private CommandResponse commandResponse = new ActionCommandResponse();

  public ViewManifest(CommandDescriptor descriptor) {
    super(descriptor);

    renderedList = new ArrayList();
  }

  public void execute() throws CommandException {

    // todo most of it is actually part of cli meuk ...

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

    ParallelRunner runner = new ParallelRunner(manifest);
    runner.execute();

    // todo timing issue ... COULD last forever.
    //
    while (!runner.finished()) {

      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    Map statusOverview = runner.retrieveStatus();

    for (Iterator i = sourceModules.iterator(); i.hasNext();) {

      Module module = (Module) i.next();

      ModuleStatus moduleStatus = (ModuleStatus) statusOverview.get(module);

      String[] moduleData = new String[7];
      moduleData[0] = module.getName();

      boolean existsInRepository = moduleStatus.existsInRepository();

      try {

        if (manifest.getState(module).equals(Module.WORKING)) {
          moduleData[1] = "   ";
        } else {
          Version localVersion = moduleStatus.getLocalVersion();
          moduleData[1] = (localVersion == null ? "   " : localVersion.getVersionNumber());
        }

        if (existsInRepository) {
          Version remoteVersion = moduleStatus.getLastVersion();
          moduleData[2] = (remoteVersion == null ? "   " : "(" + remoteVersion.getVersionNumber() + ")");
        } else {
          moduleData[2] = "";
        }

      } catch (VersionControlException v) {
        // Version for the module is non-existing in the repository.
        //
        throw new CommandException(v.getErrorCode(), v.getMessageArguments());
      }

      moduleData[3] = "(" + module.getVersionAsString() + ")";
      moduleData[4] = (module.hasPatchLine() ? "available" : "not available");

      if (existsInRepository) {
        moduleData[5] = manifest.getState(module).toString();
        moduleData[6] = module.getLocation().getId();
      } else {
        moduleData[5] = "";
        moduleData[6] = "** Not in repository **";
      }
      renderedList.add(moduleData);
    }
  }

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
