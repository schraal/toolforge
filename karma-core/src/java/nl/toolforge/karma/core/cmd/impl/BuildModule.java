package nl.toolforge.karma.core.cmd.impl;

import nl.toolforge.karma.core.cmd.ActionCommandResponse;
import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.cmd.CommandMessage;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.SuccessMessage;
import nl.toolforge.karma.core.manifest.ManifestException;
import nl.toolforge.karma.core.manifest.Module;
import org.apache.commons.io.FileUtils;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

import java.io.File;
import java.io.IOException;

/**
 * Builds a module in a manifest. Building a module means that all java sources will be compiled into the
 * modules' build directory on disk.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public class BuildModule extends AbstractBuildCommand {

  private static final String DEFAULT_SRC_PATH = "src/java";

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
        //todo: make the source dir dynamic (getSourceDir()).
        throw new CommandException(CommandException.NO_SRC_DIR, new Object[] {getCurrentModule().getName()});
      }

      // Configure the Ant project
      //
      project.setProperty(MODULE_SOURCE_DIR_PROPERTY, srcBase.getPath());
      project.setProperty(MODULE_BUILD_DIR_PROPERTY, getModuleBuildDirectory().getPath());
      project.setProperty(MODULE_COMPILE_DIR_PROPERTY, getCompileDirectory().getPath());
      project.setProperty(MODULE_CLASSPATH_PROPERTY, getDependencies(getCurrentModule().getDependencies(), false, CLASSPATH_SEPARATOR_CHAR));

    } catch (ManifestException e) {
//      e.printStackTrace();
      throw new CommandException(e.getErrorCode(), e.getMessageArguments());
    }

    try {
      project.executeTarget(BUILD_MODULE_TARGET);
    } catch (OutOfMemoryError oome) {
      throw new CommandException(CommandException.BUILD_FAILED_TOO_MANY_MISSING_DEPENDENCIES, new Object[] {getCurrentModule().getName()});
    } catch (BuildException e) {
      e.printStackTrace();
      throw new CommandException(CommandException.BUILD_FAILED, new Object[] {getCurrentModule().getName()});
    }

    message = new SuccessMessage(getFrontendMessages().getString("message.MODULE_BUILT"), new Object[] {getCurrentModule().getName()});
    commandResponse.addMessage(message);
  }

  public CommandResponse getCommandResponse() {
    return this.commandResponse;
  }

  protected File getSourceDirectory() throws ManifestException {

    if (module == null) {
      throw new IllegalArgumentException("Module cannot be null.");
    }
    return new File(new File(getCurrentManifest().getDirectory(), getCurrentModule().getName()), DEFAULT_SRC_PATH);
  }

}
