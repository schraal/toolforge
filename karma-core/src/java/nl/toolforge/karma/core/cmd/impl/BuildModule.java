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
import nl.toolforge.core.util.file.MyFileUtils;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.helper.ProjectHelperImpl;
import org.apache.crimson.parser.XMLReaderImpl;
import org.apache.commons.io.FileUtils;
import org.xml.sax.XMLReader;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.BufferedWriter;

/**
 * Builds a module in a manifest.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public class BuildModule extends DefaultCommand {

  private static final String DEFAULT_SRC_PATH = "src/java";

  private static final String JAVAC_SRC_DIR_PROPERTY = "source-dir";
  private static final String JAVAC_DEST_DIR_PROPERTY = "destination-dir";
  private static final String JAVAC_CLASSPATH_PROPERTY = "classpath";

  private static final String JAR_JAR_FILE_PROPERTY = "jar-file";

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
      currentManifest = getContext().getCurrent();
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
      // Where the src-files can be found
      //
      File srcBase = new File(new File(currentManifest.getDirectory(), moduleName), DEFAULT_SRC_PATH);

      // Where compiled classes will be stored.
      //
      File buildDir = new File(new File(getContext().getCurrent().getDirectory(), "build"), moduleName);

      project.setProperty(JAVAC_SRC_DIR_PROPERTY, srcBase.getPath());
      project.setProperty(JAVAC_DEST_DIR_PROPERTY, buildDir.getPath());
      project.setProperty(JAVAC_CLASSPATH_PROPERTY, ((SourceModule) module).getDependencies());

      project.setProperty(JAR_JAR_FILE_PROPERTY, module.getDependencyName());

    } catch (ManifestException e) {
      throw new CommandException(e.getErrorCode(), e.getMessageArguments());
    }

    try {
//      project.executeTarget("compile");
      project.executeTarget("jar");
    } catch (BuildException e) {
//      e.printStackTrace();
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
