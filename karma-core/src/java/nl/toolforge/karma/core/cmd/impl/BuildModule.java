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
    File tmp = null;
    try {
      tmp = getBuildFile("build-module.xml");
      helper.parse(project, tmp);
    } finally {
      try {
        FileUtils.deleteDirectory(tmp.getParentFile());
      } catch (IOException e) {
        throw new CommandException(e, CommandException.BUILD_FAILED);
      }
    }

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

      // Instantiate a BuildUtil class to have access to helper methods for the build process.
      //
      BuildUtil util = new BuildUtil(currentManifest);

//<<<<<<< BuildModule.java
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

//=======
//      project.setProperty(JAR_JAR_FILE_PROPERTY, module.getDependencyName());
//>>>>>>> 1.20
    } catch (ManifestException e) {
      e.printStackTrace();
      throw new CommandException(e.getErrorCode(), e.getMessageArguments());
    }

    try {
//      project.executeTarget("compile");

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

  private File getBuildFile(String buildFile) throws CommandException {

    File tmp = null;
    try {

      tmp = MyFileUtils.createTempDirectory();

      BufferedReader in =
          new BufferedReader(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream(buildFile)));
      BufferedWriter out =
          new BufferedWriter(new FileWriter(new File(tmp, buildFile)));

      String str;
      while ((str = in.readLine()) != null) {
        out.write(str);
      }
      out.close();
      in.close();

      // Return a temp reference to the file
      //
      return new File(tmp, buildFile);

    } catch (IOException e) {
      throw new CommandException(e, CommandException.BUILD_FAILED);
    }

  }
}
