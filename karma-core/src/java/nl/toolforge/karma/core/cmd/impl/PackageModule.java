package nl.toolforge.karma.core.cmd.impl;

import nl.toolforge.karma.core.cmd.DefaultCommand;
import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.ActionCommandResponse;
import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.manifest.ManifestException;
import nl.toolforge.karma.core.manifest.SourceModule;
import nl.toolforge.karma.core.manifest.Module;
import nl.toolforge.karma.core.manifest.Manifest;
import nl.toolforge.karma.core.build.BuildUtil;
import org.apache.tools.ant.taskdefs.War;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

/**
 * @author D.A. Smedes
 * @version $Id$
 */
public class PackageModule extends DefaultCommand {

  private static final String WAR___DESTINATION_FILE_PROPERTY = "war-destination-dir";

  private CommandResponse commandResponse = new ActionCommandResponse();

  public PackageModule(CommandDescriptor descriptor) {
    super(descriptor);
  }

  public void execute() throws CommandException {

    // todo move to aspect; this type of checking can be done by one aspect.
    //
    if (!getContext().isManifestLoaded()) {
      throw new CommandException(ManifestException.NO_ACTIVE_MANIFEST);
    }

    String moduleName = getCommandLine().getOptionValue("m");

    Module module = null;
    Manifest currentManifest = null;
    try {
      // todo move this bit to aspect-code.
      //
      currentManifest = getContext().getCurrentManifest();
      module = (SourceModule) currentManifest.getModule(moduleName);

    } catch (ManifestException m) {
      throw new CommandException(m.getErrorCode(), m.getMessageArguments());
    }

    // Packaging a module consists of the following tasks:
    //
    // If it is a webapp-module:
    // - create a <war>-task to create a web application archive
    // - specify the following data-items:

    String destFile = "";

    // If the manifest contains a package


    BuildUtil util = new BuildUtil(currentManifest);
    Project project = util.getAntProject(module);

    project.setProperty(WAR___DESTINATION_FILE_PROPERTY, "/tmp/bla.war");

    try {
      project.executeTarget("war");
    } catch (BuildException e) {
      e.printStackTrace();
      throw new CommandException(CommandException.BUILD_FAILED, new Object[] {moduleName});
    }
  }

  public CommandResponse getCommandResponse() {
    return commandResponse;
  }

}
