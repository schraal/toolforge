package nl.toolforge.karma.core.cmd.impl;

import nl.toolforge.karma.core.KarmaException;
import nl.toolforge.karma.core.KarmaRuntimeException;
import nl.toolforge.karma.core.cmd.ActionCommandResponse;
import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.cmd.CommandMessage;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.DefaultCommand;
import nl.toolforge.karma.core.cmd.ErrorMessage;
import nl.toolforge.karma.core.cmd.SimpleCommandMessage;
import nl.toolforge.karma.core.manifest.Manifest;
import nl.toolforge.karma.core.manifest.ManifestException;
import nl.toolforge.karma.core.manifest.Module;
import nl.toolforge.karma.core.manifest.SourceModule;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.helper.ProjectHelperImpl;

import java.io.File;
import java.io.IOException;

/**
 * Builds a module in a manifest.
 */
public class BuildModule extends DefaultCommand {

  private static final String DEFAULT_SRC_PATH = "src/java";

  private static final String JAVAC_SRC_DIR_PROPERTY = "srcdir";
  private static final String JAVAC_DEST_DIR_PROPERTY = "destdir";

  private CommandResponse commandResponse = new ActionCommandResponse();

  public BuildModule(CommandDescriptor descriptor) {
    super(descriptor);
  }

  public void execute() throws CommandException {

    String moduleName = getCommandLine().getOptionValue("m");

    Module module = null;
    try {
      // todo move this bit to aspect-code.
      //
      Manifest currentManifest = getContext().getCurrent();
      module = currentManifest.getModule(moduleName);

    } catch (ManifestException m) {
      throw new CommandException(m.getErrorCode());
    }

    // Create the build directory.
    //
    File buildDir = null;
    try {
      buildDir = new File(getContext().getCurrent().getDirectory(), "build");
      buildDir.mkdir();
    } catch (ManifestException e) {
      throw new CommandException(e.getErrorCode(), e.getMessageArguments());
    }

    CommandMessage message = null;
    try {

      DefaultLogger logger = new DefaultLogger();
      // todo hmm, this mechanism doesn't integrate with the commandresponse mechanism
      //
      logger.setErrorPrintStream(System.out);

      // Configure underlying ant to run a command.
      //
      Project project = new Project();
      project.addBuildListener(logger);
      project.init();

      // Read in the build.xml file
      //
      ProjectHelper helper = new ProjectHelperImpl();
      // todo we require an extra method in ProjectHelper, to be able to pass an InputStream ...
      //
      helper.parse(project, new File(getContext().getLocalPath(module), "build.xml"));

      project.setProperty(JAVAC_SRC_DIR_PROPERTY, DEFAULT_SRC_PATH);
      project.setProperty(JAVAC_DEST_DIR_PROPERTY, buildDir.getPath());

      try {
        project.setProperty("classpath", ((SourceModule) module).getDependencies());
      } catch (ManifestException e) {
        throw new CommandException(e.getErrorCode(), e.getMessageArguments());
      }

      try {
        project.executeTarget("compile");
      } catch (BuildException e) {
        throw new CommandException(CommandException.BUILD_FAILED, new Object[] {moduleName});
      }

      message = new SimpleCommandMessage("Module " + module.getName() + " built succesfully."); // todo localize message
      commandResponse.addMessage(message);

    } catch (KarmaException e) {

      message = new ErrorMessage(e);
      commandResponse.addMessage(message);

      // Throw a build-failed ...
      //
      throw new CommandException(CommandException.BUILD_FAILED, new Object[] {moduleName});
    }
  }

  public CommandResponse getCommandResponse() {
    return this.commandResponse;
  }

}
