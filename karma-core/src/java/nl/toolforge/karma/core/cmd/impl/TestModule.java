package nl.toolforge.karma.core.cmd.impl;

import java.io.File;
import java.util.Set;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

import nl.toolforge.karma.core.cmd.ActionCommandResponse;
import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.cmd.CommandMessage;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.SuccessMessage;
import nl.toolforge.karma.core.manifest.ManifestException;

/**
 * Run the unit tests of a given module.
 * <p>
 * At this moment this class only supports Java/JUnit in combination with Ant.
 * </p>
 *
 * @author W.H. Schraal
 * @version $Id$
 */
public class TestModule extends AbstractBuildCommand {

  private CommandResponse commandResponse = new ActionCommandResponse();

  public TestModule(CommandDescriptor descriptor) {
    super(descriptor);
  }

  public void execute() throws CommandException {

    super.execute();

    CommandMessage message = null;

    Project project = getAntProject();

    try {
      // Define the location where junit source files are stored for a module (the default location in the context of
      // a manifest).
      //
      File srcBase = getSourceDirectory();
      if (!srcBase.exists()) {
        // No point in building a module, if no test/java is available.
        //
        throw new CommandException(CommandException.NO_TEST_DIR, new Object[] {getCurrentModule().getName(), getSourceDirectory()});
      }

      // Configure the Ant project
      //
      project.setProperty(MODULE_SOURCE_DIR_PROPERTY, srcBase.getPath());
      project.setProperty(MODULE_BUILD_DIR_PROPERTY, getBuildDirectory().getPath());
      project.setProperty(MODULE_COMPILE_DIR_PROPERTY, getCompileDirectory().getPath());
      project.setProperty(MODULE_CLASSPATH_PROPERTY, getDependencies(getCurrentModule().getDependencies(), false));
    } catch (ManifestException e) {
      e.printStackTrace();
      throw new CommandException(e.getErrorCode(), e.getMessageArguments());
    }

    try {
      project.executeTarget(TEST_MODULE_TARGET);
    } catch (BuildException e) {
      e.printStackTrace();
      throw new CommandException(CommandException.TEST_FAILED, new Object[] {getCurrentModule().getName()});
    }

    message = new SuccessMessage("Module " + getCurrentModule().getName() + " tested succesfully."); // todo localize message
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
    return new File(new File(getCurrentManifest().getDirectory(), "test"), getCurrentModule().getName());
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

    return new File("");
  }

  protected File getSourceDirectory() throws ManifestException {

    if (module == null) {
      throw new IllegalArgumentException("Module cannot be null.");
    }
    return new File(new File(getCurrentManifest().getDirectory(), getCurrentModule().getName()), "test/java");
  }

  /**
   * Overrides {@link AbstractBuildCommand#getDependencies(java.util.Set, boolean)}. Adds the jar
   * of the current module to the dependencies. This jar is needed to be able to compile the unit tests.
   *
   * @param dependencies
   * @param relative
   * @return
   * @throws ManifestException
   * @throws CommandException
   */
  protected String getDependencies(Set dependencies, boolean relative) throws ManifestException, CommandException {
    //construct the name of the module's jar file
    //todo: this should be done more general
    File f = new File(getCurrentManifest().getDirectory(), DEFAULT_BUILD_DIR);
    f = new File(f, getCurrentModule().getName());
    f = new File(f, getCurrentManifest().resolveArchiveName(getCurrentModule()));

    //add the module's jar in front of the module's dependencies (if present)
    String deps = super.getDependencies(dependencies, relative);
    if (deps != null && !deps.equals("")) {
      deps = f.getPath() + DEPENDENCY_SEPARATOR_CHAR + deps;
    } else {
      deps = f.getPath();
    }
    return deps;
  }

}
