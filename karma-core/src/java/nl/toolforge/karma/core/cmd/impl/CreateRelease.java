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
import nl.toolforge.karma.core.cmd.CompositeCommand;
import nl.toolforge.karma.core.cmd.event.ErrorEvent;
import nl.toolforge.karma.core.cmd.event.MessageEvent;
import nl.toolforge.karma.core.cmd.event.SimpleMessage;
import nl.toolforge.karma.core.location.LocationException;
import nl.toolforge.karma.core.manifest.Manifest;
import nl.toolforge.karma.core.manifest.ManifestException;
import nl.toolforge.karma.core.manifest.ManifestFactory;
import nl.toolforge.karma.core.manifest.ManifestLoader;
import nl.toolforge.karma.core.vc.ModuleStatus;
import nl.toolforge.karma.core.vc.VersionControlException;
import nl.toolforge.karma.core.vc.AuthenticationException;
import nl.toolforge.karma.core.vc.cvsimpl.Utils;
import nl.toolforge.karma.core.vc.cvsimpl.threads.CVSLogThread;
import nl.toolforge.karma.core.vc.threads.ParallelRunner;
import nl.toolforge.karma.core.module.Module;

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

  private CommandResponse commandResponse = new CommandResponse();

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

    File releaseManifestFile = new File(getWorkingContext().getConfiguration().getManifestStore().getModule().getBaseDir(), releaseName + ".xml");

    if (releaseManifestFile.exists()) {
      if (!getCommandLine().hasOption("o")) {
        throw new CommandException(
            ManifestException.DUPLICATE_MANIFEST_FILE,
            new Object[] {releaseName, getWorkingContext().getConfiguration().getManifestStore().getModule().getBaseDir().getPath()}
        );
      }
    }

    // todo are there any rules for file-names (manifest names ...).
    //

    SimpleMessage message = new SimpleMessage(getFrontendMessages().getString("message.CREATE_RELEASE_STARTED"), new Object[]{releaseName});
    commandResponse.addEvent(new MessageEvent(this, message));

    // Checking manifest
    //

    ParallelRunner runner = new ParallelRunner(releaseManifest, CVSLogThread.class);
    runner.execute(100); // Blocks ...

    Map statusOverview = runner.retrieveResults();

    Map modules = releaseManifest.getAllModules();

    for (Iterator i = modules.values().iterator(); i.hasNext();) {

      Module module = (Module) i.next();
      ModuleStatus moduleStatus = (ModuleStatus) statusOverview.get(module);

      if (moduleStatus.connectionFailure()) {

        getCommandResponse().addEvent(new ErrorEvent(this, LocationException.CONNECTION_EXCEPTION));
        message = new SimpleMessage(getFrontendMessages().getString("message.CREATE_RELEASE_FAILED"));
        getCommandResponse().addEvent(new MessageEvent(this, message));

        return;
      }
      if (!moduleStatus.existsInRepository()) {

        getCommandResponse().addEvent(new ErrorEvent(this, VersionControlException.MODULE_NOT_IN_REPOSITORY, new Object[]{module.getName()}));

        // todo message implies an error, should be an error code.
        message = new SimpleMessage(getFrontendMessages().getString("message.CREATE_RELEASE_FAILED"));
        getCommandResponse().addEvent(new MessageEvent(this, message));
        return;
      }
    }


    // Ok, ww're ready to go now. Let's collect the latest versions of modules.
    //

    StringBuffer buffer = new StringBuffer();
    buffer.append("<?xml version=\"1.0\"?>\n");

    buffer.append("<manifest type=\"release\" version=\"1-0\">\n");

    buffer.append("  <modules>\n");

    for (Iterator i = modules.values().iterator(); i.hasNext();) {

      // todo NEED AN ADDITIONAL method in Manifest : getDevelopmentManifests() ...

      Module module = (Module) i.next();

      if (getContext().getCurrentManifest().getState(module).equals(Module.WORKING)) {
        throw new CommandException(CommandException.MODULE_CANNOT_BE_WORKING_FOR_RELEASE_MANIFEST, new Object[]{module.getName()});
      }

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

    boolean store = getCommandLine().hasOption("s");

    if (store) {
      try {
        getWorkingContext().getConfiguration().getManifestStore().commit(releaseName);

        message = new SimpleMessage(getFrontendMessages().getString("message.RELEASE_COMMITTED"), new Object[]{releaseName});
        commandResponse.addEvent(new MessageEvent(this, message));

      } catch (AuthenticationException e) {
        logger.error(e);
        throw new CommandException(e, e.getErrorCode(), e.getMessageArguments());
      } catch (VersionControlException e) {
        logger.error(e);
        throw new CommandException(e, e.getErrorCode(), e.getMessageArguments());
      }
    } else {
      message = new SimpleMessage(getFrontendMessages().getString("message.RELEASE_NOT_COMMITTED"), new Object[]{releaseName});
      commandResponse.addEvent(new MessageEvent(this, message));
    }

    message = new SimpleMessage(getFrontendMessages().getString("message.CREATE_RELEASE_FINISHED"), new Object[]{releaseName});
    commandResponse.addEvent(new MessageEvent(this, message));

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

}
