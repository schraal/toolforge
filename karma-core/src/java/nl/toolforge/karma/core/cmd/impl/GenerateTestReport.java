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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.manifest.Manifest;

/**
 * Generates a test report based on the test output of all modules. The command
 * expects this information to be present already. It will generate a partial report
 * when not all test output is available.
 *
 * @author W.H. Schraal
 */
public class GenerateTestReport extends AbstractBuildCommand {

  private static final Log logger = LogFactory.getLog(GenerateTestReport.class);

  private CommandResponse commandResponse = new CommandResponse();

  public GenerateTestReport(CommandDescriptor descriptor) {
    super(descriptor);
  }

  public void execute() throws CommandException {
    super.execute();

    // Configure the Ant project
    //
    Project project = getAntProject("testreport.xml");

    Manifest currentManifest = getContext().getCurrentManifest();
    logger.debug("Setting 'build-dir' to: "+ currentManifest.getBuildBaseDirectory().getPath());
    logger.debug("Setting 'reports-dir' to: "+ currentManifest.getReportsBaseDirectory().getPath());
    project.setProperty("build-dir", currentManifest.getBuildBaseDirectory().getPath());
    project.setProperty("reports-dir", currentManifest.getReportsBaseDirectory().getPath());

    try {
      project.executeTarget("run");
    } catch (BuildException e) {
      logger.info(e.getMessage(), e);
      throw new CommandException(CommandException.TEST_REPORT_FAILED);
    }
  }

  /**
   * @{inheritDoc}
   */
  public CommandResponse getCommandResponse() {
    return this.commandResponse;
  }

}
