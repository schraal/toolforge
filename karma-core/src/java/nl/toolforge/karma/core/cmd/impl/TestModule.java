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

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

import nl.toolforge.karma.core.cmd.ActionCommandResponse;
import nl.toolforge.karma.core.cmd.Command;
import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.cmd.CommandFactory;
import nl.toolforge.karma.core.cmd.CommandLoadException;
import nl.toolforge.karma.core.cmd.CommandMessage;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.ErrorMessage;
import nl.toolforge.karma.core.cmd.SuccessMessage;
import nl.toolforge.karma.core.cmd.util.DependencyException;
import nl.toolforge.karma.core.cmd.util.DependencyHelper;
import nl.toolforge.karma.core.manifest.ModuleTypeException;

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

//  private final static String DEFAULT_TEST_SRC_DIRECTORY = "test/java";

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
//System.out.println("Going to: "+commandLineString);
      command = CommandFactory.getInstance().getCommand(commandLineString);
      command.setContext(getContext());
      command.registerCommandResponseListener(getResponseListener());
      command.execute();
    } catch (CommandException ce) {
      if (    ce.getErrorCode().equals(CommandException.DEPENDENCY_DOES_NOT_EXIST) ||
          ce.getErrorCode().equals(CommandException.BUILD_FAILED) ||
          ce.getErrorCode().equals(DependencyException.DEPENDENCY_NOT_FOUND) ) {
        commandResponse.addMessage(new ErrorMessage(ce.getErrorCode(), ce.getMessageArguments()));
        throw new CommandException(ce, CommandException.TEST_FAILED, new Object[]{module.getName()});
      } else {
        message = new ErrorMessage(ce.getErrorCode(), ce.getMessageArguments());
        commandResponse.addMessage(message);
        message = new ErrorMessage(CommandException.BUILD_WARNING);
        commandResponse.addMessage(message);
      }
    } catch (CommandLoadException e) {
      throw new CommandException(e.getErrorCode(), e.getMessageArguments());
    } finally {
      if ( command != null ) {
        command.deregisterCommandResponseListener(getResponseListener());
      }
    }

    // Define the location where junit source files are stored for a module (the default location in the context of
    // a manifest).
    //
    if (!getBuildEnvironment().getModuleTestSourceDirectory().exists()) {
      // No point in building a module, if no test/java is available.
      //
      throw new CommandException(CommandException.NO_TEST_DIR, new Object[] {getCurrentModule().getName(), "test/java"});
    }

    // Configure the Ant project
    //
    Project project = getAntProject("test-module.xml");

    project.setProperty("module-source-dir", getBuildEnvironment().getModuleTestSourceDirectory().getPath());
    project.setProperty("module-test-dir", getBuildEnvironment().getModuleTestBuildDirectory().getPath());

    try {

      project.setProperty("module-compile-dir", getCompileDirectory().getPath());

      // todo should be replaced by call to DependencyHelper.getTestClassPath()

      String deps = "";

      DependencyHelper helper = new DependencyHelper(getCurrentManifest());
      helper.getClassPath(getCurrentModule());

      if (getCurrentModule().getDependencies().size() > 0) {
        deps = helper.getClassPath(getCurrentModule()) + ";";
      }

      File f = new File(getCurrentManifest().getBaseDirectory(), "build");
      f = new File(f, getCurrentModule().getName());
      f = new File(f, "build");

      deps += f.getPath();

      project.setProperty("module-classpath", deps);

    } catch (DependencyException d) {
      throw new CommandException(d.getErrorCode(), d.getMessageArguments());
    } catch (ModuleTypeException d) {
      throw new CommandException(d.getErrorCode(), d.getMessageArguments());
    }

    try {
      project.executeTarget("run");
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
}
