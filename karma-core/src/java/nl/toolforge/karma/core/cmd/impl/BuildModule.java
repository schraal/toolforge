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
import nl.toolforge.karma.core.cmd.AntErrorMessage;
import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.cmd.CommandMessage;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.SuccessMessage;
import nl.toolforge.karma.core.cmd.util.BuildEnvironment;
import nl.toolforge.karma.core.cmd.util.DependencyHelper;
import nl.toolforge.karma.core.cmd.util.KarmaBuildException;
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

      project.executeTarget("run");

    } catch (KarmaBuildException e) {
      e.printStackTrace();
      throw new CommandException(e, CommandException.BUILD_FAILED, new Object[] {getCurrentModule().getName()});
    } catch (BuildException e) {
      commandResponse.addMessage(new AntErrorMessage(e));
      throw new CommandException(e, CommandException.BUILD_FAILED, new Object[] {getCurrentModule().getName()});
    }

    CommandMessage message = new SuccessMessage(getFrontendMessages().getString("message.MODULE_BUILT"), new Object[] {getCurrentModule().getName()});
    commandResponse.addMessage(message);
  }

  public CommandResponse getCommandResponse() {
    return this.commandResponse;
  }
}
