package nl.toolforge.karma.core.cmd.impl;

import nl.toolforge.karma.core.LocalEnvironment;
import nl.toolforge.karma.core.cmd.ActionCommandResponse;
import nl.toolforge.karma.core.cmd.Command;
import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.cmd.CommandFactory;
import nl.toolforge.karma.core.cmd.CommandMessage;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.CompositeCommand;
import nl.toolforge.karma.core.cmd.SuccessMessage;
import nl.toolforge.karma.core.cmd.event.CommandResponseEvent;
import nl.toolforge.karma.core.manifest.Manifest;
import nl.toolforge.karma.core.manifest.ManifestException;
import nl.toolforge.karma.core.manifest.ManifestFactory;
import nl.toolforge.karma.core.manifest.Module;
import nl.toolforge.karma.core.vc.VersionControlException;
import nl.toolforge.karma.core.vc.cvs.Utils;

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

  private CommandResponse commandResponse = new ActionCommandResponse();

  public CreateRelease(CommandDescriptor descriptor) {
    super(descriptor);
  }

  public void execute() throws CommandException {

    String manifestName = getCommandLine().getOptionValue("d");

    Manifest releaseManifest = null;

    if (manifestName == null) {

      // The option '-m' is optional, so we use the current manifest
      //
      if (!getContext().isManifestLoaded()) {
        throw new CommandException(ManifestException.NO_ACTIVE_MANIFEST);
      }

      releaseManifest = getContext().getCurrentManifest();

    } else {

      try {
        releaseManifest = ManifestFactory.getInstance().createManifest(manifestName);
      } catch (ManifestException e) {
        throw new CommandException(e.getErrorCode(), e.getMessageArguments());
      }
    }

    // The manifest that you want to release has to be a development manifest
    //
    if (!releaseManifest.getType().equals(Manifest.DEVELOPMENT_MANIFEST)) {
      throw new CommandException(ManifestException.NOT_A_DEVELOPMENT_MANIFEST, new Object[] {releaseManifest.getName()});
    }

    String releaseName = getCommandLine().getOptionValue("r");

    // todo are there any rules for file-names (manifest names ...).
    //

    File releaseManifestFile = new File(LocalEnvironment.getManifestStore(), releaseName + ".xml");

    if (releaseManifestFile.exists()) {
      throw new CommandException(
          ManifestException.DUPLICATE_MANIFEST_FILE,
          new Object[] {releaseName, LocalEnvironment.getManifestStore().getPath()}
      );
    }

    CommandMessage message = new SuccessMessage(getFrontendMessages().getString("message.CREATE_RELEASE_STARTED"), new Object[]{releaseName});
    commandResponse.addMessage(message);

    // Ok, ww're ready to go now. Let's collect the latest versions of modules.
    //

    StringBuffer buffer = new StringBuffer();
    buffer.append("<?xml version=\"1.0\"?>\n");

    buffer.append("<manifest name=\"" + releaseName + "\" type=\"release\" version=\"1.0\">\n");

    buffer.append("  <modules>\n");

    Map modules = releaseManifest.getAllModules();

    for (Iterator i = modules.values().iterator(); i.hasNext();) {

      // todo NEED AN ADDITIONAL method in Manifest : getDevelopmentManifests() ...

      Module module = (Module) i.next();
      buffer.append(getModule(module));
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
        e.printStackTrace();
      }
    }

    boolean loadManifest = getCommandLine().hasOption("l");

    if (loadManifest) {

      Command command = CommandFactory.getInstance().getCommand(CommandDescriptor.SELECT_MANIFEST_COMMAND + " -m ".concat(releaseName));
      command.registerCommandResponseListener(this);
      getContext().execute(command);

    }

    // todo The included manifests of type 'development' should be release as well.
  }

  public CommandResponse getCommandResponse() {
    return commandResponse;
  }

  private String getModule(Module module) throws CommandException {

    String n;
    String t;
    String v;
    String l;

    n = "\"" + module.getName() + "\"";
    t = module.getSourceType().getSourceType();
    l = module.getLocation().getId();

    if (module.hasVersion()) {

      v = module.getVersion().getVersionNumber();

    } else {

      try {
        v = Utils.getLastVersion(module).getVersionNumber();
      } catch (VersionControlException e) {
        throw new CommandException(e.getErrorCode(), e.getMessageArguments());
      }
    }

    return "    <module name=" + n + " type=\"" + t + "\" version=\"" + v + "\" location=\"" + l + "\"/>\n";

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
}
