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
import nl.toolforge.karma.core.manifest.ManifestException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

import java.io.File;

/**
 * Generates Javadoc API documentation for the given module.
 *
 * @author W.H. Schraal
 * @author D.A. Smedes
 * @version $Id$
 */
public class DocModule extends AbstractBuildCommand {

  private static final String JAVADOC_OUTPUT_DIR = "module.javadoc.outputdir";
  private static final String MODULE_NAME = "module.name";
  private static final String MODULE_STATE = "module.state";
  private static final String MODULE_SRC = "module.src";

  protected CommandResponse response = new CommandResponse();

  public DocModule(CommandDescriptor descriptor) {
    super(descriptor);
  }

  /**
   * @throws CommandException When the module doesn't have a <code>src/java</code> or when no source files are
   *                          available.
   */
  public void execute() throws CommandException {

    super.execute();

    File srcDir = getBuildEnvironment().getModuleSourceDirectory();

    if (!srcDir.exists()) {
      throw new CommandException(CommandException.NO_SRC_DIR, new Object[]{module, "src/java"});
    }

    Project project = getAntProject("doc-module.xml");

    project.setProperty(JAVADOC_OUTPUT_DIR, getBuildEnvironment().getModuleJavadocDirectory().getPath());
    project.setProperty(MODULE_NAME, module.getName());
    project.setProperty(MODULE_STATE, getCurrentManifest().getState(module).toString());
    project.setProperty(MODULE_SRC, srcDir.getPath());

    try {
      project.executeTarget("run");
    } catch (BuildException b) {
      throw new CommandException(b, CommandException.BUILD_FAILED, new Object[]{module});
    }
  }

  public CommandResponse getCommandResponse() {
    return response;
  }

  protected File getSourceDirectory() throws ManifestException {
    return new File(getCurrentModule().getBaseDir(), "src/java");
  }
}
