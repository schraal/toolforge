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

import nl.toolforge.karma.core.cmd.Command;
import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.cmd.CommandFactory;
import nl.toolforge.karma.core.cmd.CommandLoadException;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.event.ExceptionEvent;
import nl.toolforge.karma.core.cmd.event.MessageEvent;
import nl.toolforge.karma.core.cmd.event.SimpleMessage;
import nl.toolforge.karma.core.cmd.util.DependencyException;
import nl.toolforge.karma.core.cmd.util.DependencyHelper;
import nl.toolforge.karma.core.manifest.ManifestException;
import nl.toolforge.karma.core.module.Module;
import nl.toolforge.karma.core.module.ModuleTypeException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;

/**
 * Builds a module in a manifest. Building a module means that all java sources will be compiled into the
 * modules' build directory on disk.
 * <p>
 * BuildModule supports the following variables in the karma.properties:
 * <ul>
 *   <li>java.compiler</li>
 *   <li>java.source</li>
 *   <li>java.target</li>
 *   <li>java.debug</li>
 *   <li>java.debuglevel</li>
 *   <li>javac.nowarn</li>
 *   <li>javac.optimize</li>
 *   <li>javac.deprecation</li>
 *   <li>javac.verbose</li>
 *   <li>javac.depend</li>
 * </ul>
 *
 * @author D.A. Smedes
 * @author W.H. Schraal
 * @version $Id$
 */
public class BuildModule extends AbstractBuildCommand {

  private static final Log logger = LogFactory.getLog(BuildModule.class);

  private CommandResponse commandResponse = new CommandResponse();

  public BuildModule(CommandDescriptor descriptor) {
    super(descriptor);
  }

  public void execute() throws CommandException {

    super.execute();


    if (!getBuildEnvironment().getModuleSourceDirectory().exists()) {
      // No point in building a module, if no src/java is available or no .java files are present in that dir.
      //
      throw new CommandException(CommandException.NO_SRC_DIR, new Object[] {getCurrentModule().getName()});
    }
    DirectoryScanner scanner = new DirectoryScanner();
    scanner.setBasedir(getBuildEnvironment().getModuleSourceDirectory());
    scanner.setIncludes(new String[]{"**/*.java"});
    scanner.scan();
    if (scanner.getIncludedFiles().length == 0) {
      // No point in building a module, if no src/java is available or no .java files are present in that dir.
      //
      throw new CommandException(CommandException.NO_SRC_DIR, new Object[] {getCurrentModule().getName()});
    }

    DependencyHelper helper = new DependencyHelper(getCurrentManifest());

    Project project = getAntProject("build-module.xml");

    try {
      boolean dependenciesChecked = false;
      while (!dependenciesChecked) {
        try {
          project.setProperty("classpath", helper.getClassPath(getCurrentModule()));
          dependenciesChecked = true;
        } catch (DependencyException de) {
          if ( !getCommandLine().hasOption("n") ||
               de.getErrorCode().equals(DependencyException.DEPENDENCY_NOT_FOUND)) {
            //a dependency was not found. Let's build it.
            //if it's a module, build it.
            //else, rethrow the exception, since we can do nothing about it.
            String dep = (String) de.getMessageArguments()[0];
            try {
              Module module = getCurrentManifest().getModule(dep);

              Command command = null;
              try {
                getCommandResponse().addEvent(new MessageEvent(this, new SimpleMessage("Module `{0}` is needed, but is not built yet. Doing that now.", new Object[]{module.getName()})));
                String commandLineString = "bm -m " + module.getName();
                logger.info("Going to: "+commandLineString);
                command = CommandFactory.getInstance().getCommand(commandLineString);
                command.setContext(getContext());
                command.registerCommandResponseListener(getResponseListener());
                command.execute();

                //the dependency built successfully.
              } catch (CommandLoadException e) {
                throw new CommandException(e.getErrorCode(), e.getMessageArguments());
              } finally {
                if ( command != null ) {
                  command.deregisterCommandResponseListener(getResponseListener());
                }
              }
            } catch (ManifestException me) {
              //obviously it was not a module...
              throw de;
            }
          } else {
            //rethrow the exception. Don't know what to do with it here.
            throw de;
          }
        }
      }
      project.setProperty("module-build-dir", getBuildEnvironment().getModuleBuildDirectory().getPath());
      project.setProperty("module-source-dir", getBuildEnvironment().getModuleSourceDirectory().getPath());
      project.setProperty("java.compiler", getWorkingContext().getProperties().getProperty("java.compiler", "modern"));
      project.setProperty("java.source", getWorkingContext().getProperties().getProperty("java.source", "1.4"));
      project.setProperty("java.target", getWorkingContext().getProperties().getProperty("java.target", "1.4"));
      project.setProperty("java.debug", getWorkingContext().getProperties().getProperty("java.debug", "off"));
      project.setProperty("java.debuglevel", getWorkingContext().getProperties().getProperty("java.debuglevel", "none"));
      project.setProperty("javac.nowarn", getWorkingContext().getProperties().getProperty("javac.nowarn", "off"));
      project.setProperty("javac.optimize", getWorkingContext().getProperties().getProperty("javac.optimize", "off"));
      project.setProperty("javac.deprecation", getWorkingContext().getProperties().getProperty("javac.deprecation", "off"));
      project.setProperty("javac.verbose", getWorkingContext().getProperties().getProperty("javac.verbose", "no"));
      project.setProperty("javac.depend", getWorkingContext().getProperties().getProperty("javac.depend", "off"));

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
