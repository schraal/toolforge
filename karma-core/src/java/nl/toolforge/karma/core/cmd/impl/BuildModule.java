package nl.toolforge.karma.core.cmd.impl;

import nl.toolforge.core.util.file.MyFileUtils;
import nl.toolforge.karma.core.build.BuildUtil;
import nl.toolforge.karma.core.cmd.ActionCommandResponse;
import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.cmd.CommandMessage;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.DefaultCommand;
import nl.toolforge.karma.core.cmd.SimpleCommandMessage;
import nl.toolforge.karma.core.manifest.Manifest;
import nl.toolforge.karma.core.manifest.ManifestException;
import nl.toolforge.karma.core.manifest.Module;
import nl.toolforge.karma.core.manifest.SourceModule;
import org.apache.commons.io.FileUtils;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.taskdefs.Javac;
import org.apache.tools.ant.helper.ProjectHelperImpl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Builds a module in a manifest.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public class BuildModule extends DefaultCommand {

  private static final String DEFAULT_SRC_PATH = "src/java";

  private static final String JAVAC___SRC_DIR_PROPERTY = "javac-source-dir";
  private static final String JAVAC___DEST_DIR_PROPERTY = "javac-destination-dir";
  private static final String JAVAC___CLASSPATH_PROPERTY = "javac-classpath";

  private static final String JAR___FILE_NAME_PROPERTY = "jar-file-name";
  private static final String JAR___BASE_DIR_PROPERTY = "jar-base-dir";

  private CommandResponse commandResponse = new ActionCommandResponse();

  public BuildModule(CommandDescriptor descriptor) {
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
      module = currentManifest.getModule(moduleName);

    } catch (ManifestException m) {
      throw new CommandException(m.getErrorCode(), m.getMessageArguments());
    }

    CommandMessage message = null;

    BuildUtil util = new BuildUtil(currentManifest);

    Project project = util.getAntProject(module);

    try {
      // Define the location where java source files are store for a module (the default location in the context of
      // a manifest).
      //
      File srcBase = new File(new File(currentManifest.getDirectory(), moduleName), DEFAULT_SRC_PATH);
      if (!srcBase.exists()) {
        // No point in building a module, if no src/java is available.
        //
        throw new CommandException(CommandException.NO_SRC_DIR, new Object[] {module.getName()});
      }

      // Define the location compiled classes and jar-files will be stored --> the build-directory.
      //
      File buildDir = new File(new File(getContext().getCurrentManifest().getDirectory(), "build"), moduleName);

      // Configure the Ant project
      //

      // <javac> 'srcdir'-attribute
      project.setProperty(JAVAC___SRC_DIR_PROPERTY, srcBase.getPath());
      // <javac> 'destdir'-attribute
      project.setProperty(JAVAC___DEST_DIR_PROPERTY, buildDir.getPath());
      // <javac> 'classpath'-attribute
      project.setProperty(JAVAC___CLASSPATH_PROPERTY, util.getDependencies(((SourceModule) module).getDependencies()));
      // <jar> 'destfile'-attribute
      project.setProperty(JAR___FILE_NAME_PROPERTY, new File(buildDir, currentManifest.resolveJarName(module)).getPath());
      // <jar> 'basedir'-attribute
      project.setProperty(JAR___BASE_DIR_PROPERTY, buildDir.getPath());

    } catch (ManifestException e) {
      e.printStackTrace();
      throw new CommandException(e.getErrorCode(), e.getMessageArguments());
    }

    try {
      project.executeTarget("jar");
    } catch (BuildException e) {
      e.printStackTrace();
      throw new CommandException(CommandException.BUILD_FAILED, new Object[] {moduleName});
    }

    message = new SimpleCommandMessage("Module " + module.getName() + " built succesfully."); // todo localize message
    commandResponse.addMessage(message);
  }

  public CommandResponse getCommandResponse() {
    return this.commandResponse;
  }
}
