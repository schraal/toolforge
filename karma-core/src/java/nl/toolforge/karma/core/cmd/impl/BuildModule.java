package nl.toolforge.karma.core.cmd.impl;

import nl.toolforge.karma.core.KarmaException;
import nl.toolforge.karma.core.manifest.Module;
import nl.toolforge.karma.core.manifest.Manifest;
import nl.toolforge.karma.core.manifest.ManifestException;
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
import org.apache.tools.ant.taskdefs.Javac;
import org.apache.tools.ant.types.FileList;
import org.apache.tools.ant.types.Path;

import java.io.File;

/**
 * Builds a module in a manifest.
 */
public class BuildModule extends DefaultCommand {
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

      // Configure underlying ant to run a command.
      //
      Project project = new Project();
      project.setName("karma"); // Required
      project.setDefault("compile"); // Required

      project.init();

      // A java compile task
      //
      Javac task = (Javac) project.createTask("javac");

      // Determine the correct classpath and apply it to the Javac task
      //

      // Here's some logic that should be implemented :

      // If dependency points to a module in the manifest, include 'getContext().getBuildTarget() + <jar-name>' in the
      // classpath. We assume that if the jar is available, it is up-to-date.
      //
      // todo consider this : if the module is in working mode, recompile it ...
      //
      // If dependency is a jar dependency, locate the dependency and add the dependencey to the compile classpath.
      //

      // The structure we want is something like :
      //
      // <classpath>
      //   <path location="${dependent-module-location}/build/AAA_2-0.jar"
      //   <path location="/home/asmedes/.maven/repository/junit/jars/junit-3.8.1.jar">
      // </classpath>

      Path classPath = new Path(project);

      FileList deps = new FileList();
      deps.setFiles(module.getDependencies());
      classPath.addFilelist(deps);


      //
      //

      // A path should be made for the module at hand
      //

      Path path = new Path(project);
      path.setPath(getContext().getLocalPath(module).getPath() + File.separator + "src" + File.separator + "java"); // Path settings determined by CommandContext

      task.setSrcdir(path);
      task.setDestdir(getContext().getBuildTarget(module)); // Path settings determined by CommandContext
      task.setClasspath(classPath);

      Target target = new Target();
      target.setName("compile");
      target.addTask(task);

      project.addTarget(target);

      try {
        target.execute();
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
