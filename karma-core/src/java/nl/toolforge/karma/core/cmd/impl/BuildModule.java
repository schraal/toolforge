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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

import nl.toolforge.karma.core.cmd.ActionCommandResponse;
import nl.toolforge.karma.core.cmd.AntErrorMessage;
import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.cmd.CommandMessage;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.SuccessMessage;
import nl.toolforge.karma.core.cmd.util.BuildEnvironment;
import nl.toolforge.karma.core.cmd.util.DependencyException;
import nl.toolforge.karma.core.cmd.util.DependencyHelper;
import nl.toolforge.karma.core.cmd.util.DependencyPath;
import nl.toolforge.karma.core.manifest.ModuleTypeException;
import nl.toolforge.karma.launcher.KarmaLauncher;

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

  private CommandResponse commandResponse = new ActionCommandResponse();

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
      project.setProperty("module-build-dir", env.getModuleBuildDirectory().getPath());
      project.setProperty("classpath", helper.getClassPath(getCurrentModule()));
      project.setProperty("module-source-dir", env.getModuleSourceDirectory().getPath());


      //
      //

      Set s1 = helper.getJarDependencies(getCurrentModule(), false);
      s1.addAll(helper.getModuleDependencies(getCurrentModule(), false));

      String[] deps = new String[s1.size() + 3];

      deps[0] = "/home/asmedes/dev/toolforge/karma-cli/lib/karma-core-1.0.jar";
      deps[1] = "/home/asmedes/.maven/repository/ant/jars/ant-1.6.1.jar";
      deps[2] = "/home/asmedes/.maven/repository/ant/jars/ant-launcher-1.6.1.jar";

      int j = 3;
      for (Iterator i = s1.iterator(); i.hasNext();) {
        deps[j] = ((DependencyPath) i.next()).getFullPath().getPath();
        j++;
      }

      try {
        KarmaLauncher.getInstance().invoke(
            "nl.toolforge.karma.core.cmd.util.AntJavacWrapper",
            "compile",
            new Object[]{project},
            deps
        );

      } catch (IllegalAccessException e) {
        e.printStackTrace();
      } catch (NoSuchMethodException e) {
        e.printStackTrace();
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      } catch (InvocationTargetException e) {
        e.printStackTrace();
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      }

//      project.executeTarget("run");

    } catch (DependencyException e) {
      logger.error(e);
      throw new CommandException(e.getErrorCode(), e.getMessageArguments());
    } catch (BuildException e) {
      logger.error(e);
      commandResponse.addMessage(new AntErrorMessage(e));
      throw new CommandException(e, CommandException.BUILD_FAILED, new Object[] {getCurrentModule().getName()});
    } catch (ModuleTypeException e) {
      logger.error(e);
      throw new CommandException(e.getErrorCode(), e.getMessageArguments());
    }

    CommandMessage message = new SuccessMessage(getFrontendMessages().getString("message.MODULE_BUILT"), new Object[] {getCurrentModule().getName()});
    commandResponse.addMessage(message);
  }

  public CommandResponse getCommandResponse() {
    return this.commandResponse;
  }



}
