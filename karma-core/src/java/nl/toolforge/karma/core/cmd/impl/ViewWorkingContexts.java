/*
Karma CLI - Command Line Interface for the Karma application
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

import nl.toolforge.karma.core.boot.WorkingContext;
import nl.toolforge.karma.core.boot.Karma;
import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.DefaultCommand;
import org.apache.tools.ant.DirectoryScanner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Retrieves all working contexts (found as subdirectories with a <code>working-context.xml</code>) and presents all
 * entries as a <code>String</code> array through
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public class ViewWorkingContexts extends DefaultCommand {

  private CommandResponse commandResponse = new CommandResponse();

  private List workingContexts = new ArrayList();

  public ViewWorkingContexts(CommandDescriptor descriptor) {
    super(descriptor);
  }

  public void execute() throws CommandException {

    File workingContextBaseDir = new File(Karma.getConfigurationBaseDir(), "working-contexts");

    DirectoryScanner scanner = new DirectoryScanner();
    scanner.setBasedir(workingContextBaseDir);

    scanner.scan();

    String[] dirs = scanner.getIncludedDirectories();

    for (int i = 0; i < dirs.length; i++) {

      File base = new File(workingContextBaseDir, dirs[i]);

      File workingContextConfiguration = new File(base, "working-context.xml");

      if (workingContextConfiguration.exists()) {
        workingContexts.add(dirs[i]);
      }
    }
  }

  protected List getWorkingContexts() {
    return workingContexts;
  }

  public CommandResponse getCommandResponse() {
    return this.commandResponse;
  }

}