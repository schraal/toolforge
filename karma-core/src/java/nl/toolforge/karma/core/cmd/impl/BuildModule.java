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

import nl.toolforge.karma.core.cmd.ActionCommandResponse;
import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.cmd.CommandMessage;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.SuccessMessage;
import nl.toolforge.karma.core.cmd.AntErrorMessage;
import nl.toolforge.karma.core.manifest.ManifestException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Javac;
import org.apache.tools.ant.types.Path;

import java.io.File;

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

    try {
      // moet anders !!!!
      //
//      File compileDirectory = new File(getBuildDirectory().getParentFile(), getCompileDirectory().getPath());
//      File compileDirectory = getCompileDirectory();

      File srcBase = getSourceDirectory();
      if (!srcBase.exists()) {
        // No point in building a module, if no src/java is available.
        //
        throw new CommandException(CommandException.NO_SRC_DIR, new Object[] {getCurrentModule().getName(), DEFAULT_SRC_PATH});
      }

      // Ant project
      //
      Project project = getProjectInstance();

      // <mkdir>
      //
      executeMkdir(getCompileDirectory());
      executeDelete(getBuildDirectory(), "*.jar");

      //  <javac>
      //
      Javac javac = new Javac();
      javac.setProject(project);
      javac.setSource("1.4");
      javac.setCompiler("javac1.4");
      javac.setDebug(true);
      javac.setDeprecation(true);
      javac.setDestdir(getCompileDirectory());

      Path classPath = new Path(project);
      classPath.setPath(getDependencies(getCurrentModule().getDependencies(), false, CLASSPATH_SEPARATOR_CHAR));
      javac.setClasspath(classPath);

      javac.setIncludes("**/*.java");

      Path sourcePath = new Path(project);
      sourcePath.setPath(srcBase.getPath());
      javac.setSrcdir(sourcePath);

      javac.execute();


    } catch (ManifestException e) {
      throw new CommandException(e.getErrorCode(), e.getMessageArguments());
    }  catch (BuildException e) {
//      e.printStackTrace();
      commandResponse.addMessage(new AntErrorMessage(e));
      throw new CommandException(e, CommandException.BUILD_FAILED, new Object[] {getCurrentModule().getName()});
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
