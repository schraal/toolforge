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

import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.event.ExceptionEvent;
import nl.toolforge.karma.core.cmd.event.MessageEvent;
import nl.toolforge.karma.core.cmd.event.SimpleMessage;
import nl.toolforge.karma.core.cmd.util.BuildEnvironment;
import nl.toolforge.karma.core.cmd.util.DependencyException;
import nl.toolforge.karma.core.cmd.util.DependencyHelper;
import nl.toolforge.karma.core.manifest.ModuleTypeException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

/**
 * Builds a module in a manifest. Building a module means that all java sources will be compiled into the
 * modules' build directory on disk.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public class BuildModule extends AbstractBuildCommand {

  private static final Log logger = LogFactory.getLog(BuildModule.class);

  private static final String DEFAULT_SRC_PATH = "src/java";

  private CommandResponse commandResponse = new CommandResponse();

  public BuildModule(CommandDescriptor descriptor) {
    super(descriptor);
  }

  public void execute() throws CommandException {

    super.execute();

    BuildEnvironment env = new BuildEnvironment(getCurrentManifest(), getCurrentModule());

    if (!env.getModuleSourceDirectory().exists()) {
      // No point in building a module, if no src/java is available.
      //
      throw new CommandException(CommandException.NO_SRC_DIR, new Object[] {getCurrentModule().getName(), DEFAULT_SRC_PATH});
    }

    DependencyHelper helper = new DependencyHelper(getCurrentManifest());

    Project project = getAntProject("build-module.xml");

    try {
//      boolean dependenciesChecked = false;
//      while (!dependenciesChecked) {
//        try {
          project.setProperty("classpath", helper.getClassPath(getCurrentModule()));
//          dependenciesChecked = true;
//        } catch (DependencyException de) {
//          if ( !getCommandLine().hasOption("n") ||
//               de.getErrorCode().equals(DependencyException.DEPENDENCY_NOT_FOUND)) {
//            //a dependency was not found. Let's build it.
//            //if it's a module, build it.
//            //else, rethrow the exception, since we can do nothing about it.
//            String dep = (String) de.getMessageArguments()[0];
//            try {
//              Module module = getCurrentManifest().getModule(dep);
//
////----------
//              Command command = null;
//              try {
//                //todo: this has to become a normal build command
//                //however, then build needs to use classes, not packages.
//                getCommandResponse().addMessage(new StatusMessage("Module `{0}` is needed, but is not built yet. Doing that now.", new Object[]{module.getName()}));
//
//                String commandLineString = "pam -m " + module.getName();
//System.out.println("Going to: "+commandLineString);
//                command = CommandFactory.getInstance().getCommand(commandLineString);
//                command.setContext(getContext());
//                command.registerCommandResponseListener(getResponseListener());
//                command.execute();
//
//                //the dependency built successfully.
//              } catch (CommandLoadException e) {
//                throw new CommandException(e.getErrorCode(), e.getMessageArguments());
//              } finally {
//                if ( command != null ) {
//                  command.deregisterCommandResponseListener(getResponseListener());
//                }
//              }
////------------------
//            } catch (ManifestException me) {
//              //obviously it was not a module...
//              throw de;
//            }
//          } else {
//            //rethrow the exception. Don't know what to do with it here.
//            throw de;
//          }
//        }
//      }
      project.setProperty("module-build-dir", env.getModuleBuildDirectory().getPath());
      project.setProperty("module-source-dir", env.getModuleSourceDirectory().getPath());

      project.executeTarget("run");

    } catch (DependencyException e) {
      logger.error(e);
      throw new CommandException(e.getErrorCode(), e.getMessageArguments());
    } catch (BuildException e) {
      logger.error(e);
      commandResponse.addEvent(new ExceptionEvent(this, e));
      throw new CommandException(e, CommandException.BUILD_FAILED, new Object[] {getCurrentModule().getName()});
    } catch (ModuleTypeException e) {
      logger.error(e);
      throw new CommandException(e.getErrorCode(), e.getMessageArguments());
    }

    SimpleMessage message = new SimpleMessage(getFrontendMessages().getString("message.MODULE_BUILT"), new Object[] {getCurrentModule().getName()});
    commandResponse.addEvent(new MessageEvent(this, message));
  }

  public CommandResponse getCommandResponse() {
    return this.commandResponse;
  }



}
