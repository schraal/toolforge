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
import nl.toolforge.karma.core.cmd.CommandLoadException;
import nl.toolforge.karma.core.cmd.CommandMessage;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.CompositeCommand;
import nl.toolforge.karma.core.cmd.ErrorMessage;
import nl.toolforge.karma.core.cmd.SuccessMessage;
import nl.toolforge.karma.core.cmd.event.CommandResponseEvent;
import nl.toolforge.karma.core.location.LocationException;
import nl.toolforge.karma.core.manifest.Manifest;
import nl.toolforge.karma.core.manifest.ManifestException;
import nl.toolforge.karma.core.manifest.ManifestFactory;
import nl.toolforge.karma.core.manifest.ManifestLoader;
import nl.toolforge.karma.core.manifest.Module;
import nl.toolforge.karma.core.vc.ModuleStatus;
import nl.toolforge.karma.core.vc.VersionControlException;
import nl.toolforge.karma.core.vc.cvsimpl.Utils;
import nl.toolforge.karma.core.vc.cvsimpl.threads.CVSLogThread;
import nl.toolforge.karma.core.vc.threads.ParallelRunner;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

/**
 * Creates a release manifest, based on the current (development) manifest, by checking all the latest versions
 * of modules and adding them
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public class CreateRelease extends CompositeCommand {

  private static final Log logger = LogFactory.getLog(CreateRelease.class);

  private CommandResponse commandResponse = new ActionCommandResponse();

  public CreateRelease(CommandDescriptor descriptor) {
    super(descriptor);
  }

  public void execute() throws CommandException {

    String manifestName = getCommandLine().getOptionValue("d");

    Manifest releaseManifest = null;

    if (manifestName == null) {

      // The option '-d' is optional, so we use the current manifest
      //
      if (!getContext().isManifestLoaded()) {
        throw new CommandException(ManifestException.NO_ACTIVE_MANIFEST);
      }

      releaseManifest = getContext().getCurrentManifest();

    } else {

      try {
        ManifestFactory factory = new ManifestFactory();
        ManifestLoader loader = new ManifestLoader(getWorkingContext());
        releaseManifest = factory.create(getWorkingContext(), loader.load(manifestName));
      } catch (ManifestException e) {
        throw new CommandException(e.getErrorCode(), e.getMessageArguments());
      } catch (LocationException e) {
        throw new CommandException(e.getErrorCode(), e.getMessageArguments());
      }
    }

    String releaseName = getCommandLine().getOptionValue("r");

    File releaseManifestFile = new File(getWorkingContext().getManifestStore(), releaseName + ".xml");

    if (releaseManifestFile.exists()) {
      if (!getCommandLine().hasOption("o")) {
        throw new CommandException(
            ManifestException.DUPLICATE_MANIFEST_FILE,
            new Object[] {releaseName, getWorkingContext().getManifestStore().getPath()}
        );
      }
    }

    // todo are there any rules for file-names (manifest names ...).
    //

    CommandMessage message = new SuccessMessage(getFrontendMessages().getString("message.CREATE_RELEASE_STARTED"), new Object[]{releaseName});
    commandResponse.addMessage(message);

    // Checking manifest
    //

    ParallelRunner runner = new ParallelRunner(releaseManifest, CVSLogThread.class);
    runner.execute(); // Blocks ...

    Map statusOverview = runner.retrieveResults();

    Map modules = releaseManifest.getAllModules();

    for (Iterator i = modules.values().iterator(); i.hasNext();) {

      Module module = (Module) i.next();
      ModuleStatus moduleStatus = (ModuleStatus) statusOverview.get(module);

      if (moduleStatus.connectionFailure()) {
        
        message = new ErrorMessage(LocationException.CONNECTION_EXCEPTION);
        getCommandResponse().addMessage(message);
        message = new SuccessMessage(getFrontendMessages().getString("message.CREATE_RELEASE_FAILED"));
        getCommandResponse().addMessage(message);

        return;
      }
      if (!moduleStatus.existsInRepository()) {

        message = new ErrorMessage(VersionControlException.MODULE_NOT_IN_REPOSITORY, new Object[]{module.getName()});
        getCommandResponse().addMessage(message);
        message = new SuccessMessage(getFrontendMessages().getString("message.CREATE_RELEASE_FAILED"));
        getCommandResponse().addMessage(message);

        return;
      }
    }


    // Ok, ww're ready to go now. Let's collect the latest versions of modules.
    //

    StringBuffer buffer = new StringBuffer();
    buffer.append("<?xml version=\"1.0\"?>\n");

    buffer.append("<manifest name=\"" + releaseName + "\" type=\"release\" version=\"1-0\">\n");

    buffer.append("  <modules>\n");

    for (Iterator i = modules.values().iterator(); i.hasNext();) {

      // todo NEED AN ADDITIONAL method in Manifest : getDevelopmentManifests() ...

      Module module = (Module) i.next();
      buffer.append(getModule(module, getCommandLine().hasOption("u")));
    }

    buffer.append("  </modules>\n");

    buffer.append("</manifest>\n");

    FileWriter writer = null;
    try {
      // Write the manifest to the manifest store.
      //
      writer = new FileWriter(releaseManifestFile);

      writer.write(buffer.toString());
      writer.flush();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        writer.close();
      } catch (IOException e) {
        logger.error(e);
      }
    }

    message = new SuccessMessage(getFrontendMessages().getString("message.CREATE_RELEASE_FINISHED"), new Object[]{releaseName});
    commandResponse.addMessage(message);

    boolean loadManifest = getCommandLine().hasOption("l");

    if (loadManifest) {

      Command command = null;
      try {
        command = CommandFactory.getInstance().getCommand("select-manifest -m ".concat(releaseName));
      } catch (CommandLoadException e) {
        throw new CommandException(e.getErrorCode(), e.getMessageArguments());
      }
      command.registerCommandResponseListener(this);
      getContext().execute(command);
    }

    // todo The included manifests of type 'development' should be release as well.
  }

  public CommandResponse getCommandResponse() {
    return commandResponse;
  }

  private String getModule(Module module, boolean useRemote) throws CommandException {

    String n;
//    String t;
    String v;
    String l;

    n = "\"" + module.getName() + "\"";
    l = module.getLocation().getId();

    if (module.hasVersion()) {

      v = module.getVersion().getVersionNumber();

    } else {

      try {
        if (useRemote) {
          v = Utils.getLastVersion(module).getVersionNumber();
        } else {
          v = Utils.getLocalVersion(module).getVersionNumber();
        }
      } catch (VersionControlException e) {
        throw new CommandException(e.getErrorCode(), e.getMessageArguments());
      }
    }

    return "    <module name=" + n + " version=\"" + v + "\" location=\"" + l + "\"/>\n";

  }

  public void commandResponseFinished(CommandResponseEvent event) {
    // Finished. Nothing to do.
  }

  public void commandHeartBeat() {
    // todo implementation required
  }

  public void commandResponseChanged(CommandResponseEvent event) {
    //
  }

  public void manifestChanged(Manifest manifest) {

  }
}
