package nl.toolforge.karma.core.cmd.impl;

import nl.toolforge.karma.core.KarmaException;
import nl.toolforge.karma.core.manifest.Module;
import nl.toolforge.karma.core.manifest.Manifest;
import nl.toolforge.karma.core.manifest.ManifestException;
import nl.toolforge.karma.core.manifest.SourceModule;
import nl.toolforge.karma.core.cmd.ActionCommandResponse;
import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.cmd.CommandMessage;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.DefaultCommand;
import nl.toolforge.karma.core.cmd.SimpleCommandMessage;
import nl.toolforge.karma.core.cmd.ErrorMessage;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.BuildListener;
import org.apache.tools.ant.BuildLogger;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.helper.ProjectHelperImpl;
import org.apache.tools.ant.taskdefs.Javac;
import org.apache.tools.ant.types.FileList;
import org.apache.tools.ant.types.Path;

import java.io.File;
import java.util.Set;
import java.util.Collections;
import java.util.Arrays;

/**
 * Builds a module in a manifest.
 */
public class BuildModule extends DefaultCommand {

  private static final String DEFAULT_SRC_PATH = "src/java";

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

    CommandMessage message = null;
    try {


      DefaultLogger logger = new DefaultLogger();
      logger.setErrorPrintStream(System.out);

      // Configure underlying ant to run a command.
      //
      Project project = new Project();
      project.addBuildListener(logger);

//      project.setUserProperty("srcdir", DEFAULT_SRC_PATH);

      project.init();

      ProjectHelper helper = new ProjectHelperImpl();
      helper.parse(project, new File(getContext().getLocalPath(module), "build.xml"));

      project.setProperty("srcdir", DEFAULT_SRC_PATH);

      try {
        project.executeTarget("compile");
      } catch (BuildException e) {
        e.printStackTrace();
      }

      //project.executeTarget("compile");

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
