/*
Karma core - Core of the Karma application
Copyright (C) 2004  Toolforge <www.toolforge.nl>

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/
package nl.toolforge.karma.core.cmd.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.helper.ProjectHelperImpl;
import org.apache.tools.ant.taskdefs.Delete;
import org.apache.tools.ant.taskdefs.Mkdir;
import org.apache.tools.ant.types.FileSet;

import nl.toolforge.core.util.file.MyFileUtils;
import nl.toolforge.karma.core.KarmaRuntimeException;
import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.cmd.DefaultCommand;
import nl.toolforge.karma.core.cmd.util.AntLogger;
import nl.toolforge.karma.core.cmd.util.BuildEnvironment;
import nl.toolforge.karma.core.manifest.Manifest;
import nl.toolforge.karma.core.manifest.ManifestException;
import nl.toolforge.karma.core.module.Module;

/**
 * Superclass for all commands dealing with building modules. This class provides all basic property mappers and methods
 * that are required to use the <code>build-module.xml</code> properly.
 *
 * @author D.A. Smedes
 * @author W.H. Schraal
 * @version $Id$
 */
public abstract class AbstractBuildCommand extends DefaultCommand {

  private static final Log logger = LogFactory.getLog(AbstractBuildCommand.class);

  protected Module module = null;

  private File tempBuildFileLocation = null; // Maintains a hook to a temp location for the Ant build file.
  private Project project = null;
  private BuildEnvironment env = null;

  /**
   * Creates a command by initializing the command through its <code>CommandDescriptor</code>.
   *
   * @param descriptor The command descriptor instance containing the basic information for this command
   */
  public AbstractBuildCommand(CommandDescriptor descriptor) {
    super(descriptor);
  }

  public void execute() throws CommandException {

    if (!getContext().isManifestLoaded()) {
      throw new CommandException(ManifestException.NO_ACTIVE_MANIFEST);
    }

    if (getCommandLine().hasOption("m")) {
      String moduleName = getCommandLine().getOptionValue("m");

      try {
        // todo move this bit to aspect-code.
        //
        module = getCurrentManifest().getModule(moduleName);

        if (module == null) {
          throw new CommandException(ManifestException.MODULE_NOT_FOUND, new Object[]{module});
        }

        if (!getCurrentManifest().isLocal(module)) {
          throw new CommandException(ManifestException.MODULE_NOT_LOCAL, new Object[]{module});
        }


      } catch (ManifestException m) {
        throw new CommandException(m.getErrorCode(), m.getMessageArguments());
      }
    }

    // Initialize the current build environment.
    //
    env = new BuildEnvironment(getCurrentManifest(), module);
  }

  /**
   * Helper method to retrieve the current manifest.
   *
   * @return The current manifest.
   */
  protected final Manifest getCurrentManifest() {
    return getContext().getCurrentManifest();
  }

  /**
   * Helper method to get to the current module.
   *
   * @return
   */
  protected Module getCurrentModule() {

    if (module == null) {
      throw new KarmaRuntimeException("Module is null. Execute method has not been called by subclass.");
    }
    return module;
  }

  /**
   * Retrieve the build environment, which is initialized with the current manifest and module.
   */
  protected BuildEnvironment getBuildEnvironment() {
    return env;
  }

  /**
   * Returns the compile directory for a module, relative to the manifests' <code>build</code> directory.
   *
   * @return
   */
  protected final File getCompileDirectory() {
    if (module == null) {
      throw new IllegalArgumentException("Module cannot be null.");
    }

    return new File(env.getModuleBuildRootDirectory(), "build");
  }

  /**
   * Gets an Ant project initializing the project with <code>buildFile</code> which should be located on the
   * classpath in the <code>ant</code> subdirectory.
   *
   * @param buildFile The build file that should be loaded.
   * @return An Ant project initialized with <code>buildFile</code>.
   */
  protected Project getAntProject(String buildFile) throws CommandException {

    ProjectHelper helper = new ProjectHelperImpl();
    try {
      File tmp = getBuildFile(buildFile);
      helper.parse(getProjectInstance(), tmp);
      setBuildFileLocation(tmp);
    } catch (IOException e) {
      throw new CommandException(e, CommandException.BUILD_FAILED, new Object[] {getCurrentModule().getName()});
    }

    return project;
  }


  /**
   * Gets an Ant <code>Project</code> for a module.
   *
   * @return
   * @throws CommandException
   */
  protected Project getProjectInstance() throws CommandException {

    if (project == null) {

      DefaultLogger logger = getAntLogger();

      // todo hmm, this mechanism doesn't integrate with the commandresponse mechanism
      //
      logger.setOutputPrintStream(System.out);
      logger.setErrorPrintStream(System.err);

      //todo: this has to be configurable
      String logLevel = System.getProperty("antloglevel", "");
      int antLogLevel = Project.MSG_WARN;
      if (logLevel.equalsIgnoreCase("debug")) {
        antLogLevel = Project.MSG_DEBUG;
      } else if (logLevel.equalsIgnoreCase("info")) {
        antLogLevel = Project.MSG_INFO;
      }
      logger.setMessageOutputLevel(antLogLevel);

      // Configure underlying ant to run a command.
      //
      project = new Project();
      project.addBuildListener(logger);
      project.init();
    }

    return project;
  }

  /**
   * Get the DefaultLogger instance to be used for the current command. Commands
   * that want to have their own AntLogger can override this method to supply
   * the messagePrefixes and taskNames they need.
   *
   * @return DefaultLogger.
   */
  protected DefaultLogger getAntLogger() {
    return new AntLogger(this);
  }

  /**
   * Performs an &lt;mkdir&gt;-task on this commands' Ant project.
   */
  public void executeMkdir(File dir) throws CommandException {

    if (project == null) {
      project = getProjectInstance();
    }

    Mkdir mkdir = new Mkdir();
    mkdir.setProject(project);
    mkdir.setDir(dir);
    mkdir.execute();
  }

  /**
   * Performs a &lt;delete&gt;-task on this commands' Ant project.
   */
  public void executeDelete(File dir, String includes) throws CommandException {

    if (project == null) {
      project = getProjectInstance();
    }

    if (dir != null && dir.exists()) {

      // <delete>
      //
      Delete delete = new Delete();
      delete.setProject(project);

      FileSet fileset = new FileSet();
      fileset.setDir(dir);
      fileset.setIncludes(includes);

      delete.addFileset(fileset);
      delete.execute();
    }
  }

  public void executeDelete(File dir) throws CommandException {

    if (project == null) {
      project = getProjectInstance();
    }

    try {

      // <delete>
      //
      Delete delete = new Delete();
      delete.setProject(project);

      if (dir.equals(new File("."))) {
        throw new KarmaRuntimeException("We don't do that stuff here ...");
      }

      delete.setDir(dir);
      delete.execute();
    } catch (RuntimeException r) {
      throw new CommandException(CommandException.BUILD_FAILED, new Object[]{r.getMessage()});
    }
  }

  private final File getBuildFile(String buildFile) throws IOException {

    File tmp = null;

    tmp = MyFileUtils.createTempDirectory();
    tmp.deleteOnExit();
    
    ClassLoader loader = this.getClass().getClassLoader();

    BufferedReader in =
        new BufferedReader(new InputStreamReader(loader.getResourceAsStream("ant/" + buildFile)));
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
  }

  private void setBuildFileLocation(File tmpBuildFileLocation) {
    this.tempBuildFileLocation = tmpBuildFileLocation;
  }

  /**
   * Called by {@link nl.toolforge.karma.core.cmd.CommandContext} after executing a command.
   */
  public final void cleanUp() {

    try {
      if (tempBuildFileLocation != null) {
        FileUtils.deleteDirectory(tempBuildFileLocation.getParentFile());
      }
    } catch (IOException e) {
      logger.warn("Could not remove temporary directory for Ant build file.");
    }
  }

}
