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
      project.setProperty(MODULE_BUILD_DIR_PROPERTY, getBuildDirectory().getPath());
      project.setProperty(MODULE_COMPILE_DIR_PROPERTY, getCompileDirectory().getPath());
      project.setProperty(MODULE_CLASSPATH_PROPERTY, getDependencies(getCurrentModule().getDependencies(), false));

    } catch (ManifestException e) {
//      e.printStackTrace();
      throw new CommandException(e.getErrorCode(), e.getMessageArguments());
    }

    try {
      project.executeTarget(BUILD_MODULE_TARGET);
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

  /**
   * Returns the build directory for a module.
   *
   * @return
   * @throws ManifestException
   */
  protected File getBuildDirectory() throws ManifestException {

    if (module == null) {
      throw new IllegalArgumentException("Module cannot be null.");
    }

    // the rest, for the time being.
    //
    return new File(new File(getCurrentManifest().getDirectory(), "build"), getCurrentModule().getName());
  }

  /**
   * Returns the compile directory for a module.
   *
   * @return
   * @throws ManifestException
   */
  protected File getCompileDirectory() throws ManifestException {

    if (module == null) {
      throw new IllegalArgumentException("Module cannot be null.");
    }

    if (module.getDeploymentType().equals(Module.WEBAPP)) {
      return new File("WEB-INF/classes");
    } else {
      return new File("");
    }
  }

  protected File getSourceDirectory() throws ManifestException {

    if (module == null) {
      throw new IllegalArgumentException("Module cannot be null.");
    }
    return new File(new File(getCurrentManifest().getDirectory(), getCurrentModule().getName()), DEFAULT_SRC_PATH);
  }

}
