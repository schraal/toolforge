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
import nl.toolforge.karma.core.cmd.Command;
import nl.toolforge.karma.core.cmd.CommandFactory;
import nl.toolforge.karma.core.cmd.ErrorMessage;
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

  private final static String DEFAULT_TEST_SRC_DIRECTORY = "test/java";

  private CommandResponse commandResponse = new ActionCommandResponse();

  public TestModule(CommandDescriptor descriptor) {
    super(descriptor);
  }

  public void execute() throws CommandException {

    super.execute();

    CommandMessage message = null;

    Command command = null;
    try {
      String commandLineString = "bm -m " + module.getName();
System.out.println("Going to: "+commandLineString);
      command = CommandFactory.getInstance().getCommand(commandLineString);
      command.setContext(getContext());
      command.registerCommandResponseListener(getResponseListener());
      command.execute();
    } catch (CommandException ce) {
      if (ce.getErrorCode().equals(CommandException.DEPENDENCY_DOES_NOT_EXIST)) {
        commandResponse.addMessage(new ErrorMessage(ce.getErrorCode(), ce.getMessageArguments()));
        throw new CommandException(ce, CommandException.BUILD_FAILED, new Object[]{module.getName()});
      } else {
        message = new ErrorMessage(ce.getErrorCode(), ce.getMessageArguments());
        commandResponse.addMessage(message);
        message = new ErrorMessage(CommandException.TEST_WARNING);
        commandResponse.addMessage(message);
      }
    } finally {
      if ( command != null ) {
        command.deregisterCommandResponseListener(getResponseListener());
      }
    }

    // Define the location where junit source files are stored for a module (the default location in the context of
    // a manifest).
    //
    File srcBase = getSourceDirectory();
    if (!srcBase.exists()) {
      // No point in building a module, if no test/java is available.
      //
      throw new CommandException(CommandException.NO_TEST_DIR, new Object[] {getCurrentModule().getName(), DEFAULT_TEST_SRC_DIRECTORY});
    }

    // Configure the Ant project
    //
    Project project = getAntProject();
    project.setProperty(MODULE_SOURCE_DIR_PROPERTY, srcBase.getPath());
    project.setProperty(MODULE_BUILD_DIR_PROPERTY, getModuleBuildDirectory().getPath());
    project.setProperty(MODULE_TEST_DIR_PROPERTY, getTestDirectory().getPath());
    project.setProperty(MODULE_COMPILE_DIR_PROPERTY, getCompileDirectory().getPath());
    try {
      project.setProperty(MODULE_CLASSPATH_PROPERTY, getDependencies(getCurrentModule().getDependencies(), false, CLASSPATH_SEPARATOR_CHAR));
    } catch (ManifestException me) {
      throw new CommandException(CommandException.DEPENDENCY_FILE_INVALID, me.getMessageArguments());
    }

    try {
      project.executeTarget(TEST_MODULE_TARGET);
    } catch (BuildException e) {
      throw new CommandException(CommandException.TEST_FAILED, new Object[] {getCurrentModule().getName()});
    }

    // todo: localize message
    message = new SuccessMessage("Module " + getCurrentModule().getName() + " tested succesfully.");
    commandResponse.addMessage(message);
  }

  public CommandResponse getCommandResponse() {
    return this.commandResponse;
  }

  protected File getSourceDirectory() {

    if (module == null) {
      throw new IllegalArgumentException("Module cannot be null.");
    }
    return new File(new File(getCurrentManifest().getDirectory(), getCurrentModule().getName()), DEFAULT_TEST_SRC_DIRECTORY);
  }

  /**
   * Overrides {@link AbstractBuildCommand#getDependencies(Set, boolean, char)}. Adds the jar
   * of the current module to the dependencies. This jar is needed to be able to compile the unit tests.
   *
   * @param dependencies
   * @param relative
   * @return
   * @throws ManifestException
   * @throws CommandException
   */
  protected String getDependencies(Set dependencies, boolean relative, char separator) throws ManifestException, CommandException {
    //construct the name of the module's jar file
    //todo: this should be done more general
    File f = new File(getCurrentManifest().getDirectory(), DEFAULT_BUILD_DIR);
    f = new File(f, getCurrentModule().getName());
    f = new File(f, DEFAULT_BUILD_DIR);

    //add the module's jar in front of the module's dependencies (if present)
    String deps = super.getDependencies(dependencies, relative, separator);
    if (deps != null && !deps.equals("")) {
      deps = f.getPath() + separator + deps;
    } else {
      deps = f.getPath();
    }
    return deps;
  }

}
