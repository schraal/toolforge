package nl.toolforge.karma.core.cmd.impl;

import nl.toolforge.karma.core.cmd.ActionCommandResponse;
import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.cmd.CommandMessage;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.SimpleCommandMessage;
import nl.toolforge.karma.core.manifest.ManifestException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

import java.io.File;

/**
 * Builds a module in a manifest. Building a module means that all java sources will be compiled into the
 * modules' build directory on disk.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public class BuildModule extends AbstractBuildCommand {

  private CommandResponse commandResponse = new ActionCommandResponse();

  public BuildModule(CommandDescriptor descriptor) {
    super(descriptor);
  }

  public void execute() throws CommandException {

    super.execute();

    CommandMessage message = null;

    Project project = getAntProject();

    try {
      // Define the location where java source files are store for a module (the default location in the context of
      // a manifest).
      //
      File srcBase = getSourceDirectory();
      if (!srcBase.exists()) {
        // No point in building a module, if no src/java is available.
        //
        throw new CommandException(CommandException.NO_SRC_DIR, new Object[] {getCurrentModule().getName()});
      }

      // Configure the Ant project
      //
      project.setProperty(MODULE_SOURCE_DIR_PROPERTY, srcBase.getPath());
      project.setProperty(MODULE_BUILD_DIR_PROPERTY, getBuildDirectory().getPath());
      project.setProperty(MODULE_COMPILE_DIR_PROPERTY, getCompileDirectory().getPath());
      project.setProperty(MODULE_CLASSPATH_PROPERTY, getDependencies(getCurrentModule().getDependencies()));

    } catch (ManifestException e) {
//      e.printStackTrace();
      throw new CommandException(e.getErrorCode(), e.getMessageArguments());
    }

    try {
      project.executeTarget(BUILD_MODULE_TARGET);
    } catch (BuildException e) {
//      e.printStackTrace();
      throw new CommandException(CommandException.BUILD_FAILED, new Object[] {getCurrentModule().getName()});
    }

    message = new SimpleCommandMessage("Module " + getCurrentModule().getName() + " built succesfully."); // todo localize message
    commandResponse.addMessage(message);
  }

  public CommandResponse getCommandResponse() {
    return this.commandResponse;
  }
}
