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
package nl.toolforge.karma.cli.cmd;

import nl.toolforge.karma.core.cmd.ActionCommandResponse;
import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.SuccessMessage;
import nl.toolforge.karma.core.cmd.impl.ViewManifest;
import nl.toolforge.karma.core.manifest.ReleaseManifest;
import org.apache.commons.lang.StringUtils;

import java.util.Iterator;
import java.util.List;

/**
 * @author D.A. Smedes
 *
 * @version $Id$
 */
public class ViewManifestImpl extends ViewManifest {

  private CommandResponse commandResponse = new ActionCommandResponse();

  public ViewManifestImpl(CommandDescriptor descriptor) {
    super(descriptor);
  }

  /**
   * Shows the contents using simple rendering.
   */
  public void execute() throws CommandException {

    SuccessMessage message = new SuccessMessage("Checking manifest status, please wait ...");
    commandResponse.addMessage(message);

    super.execute(); // Ignore the response from the superclass

    List renderedData = getData();

    // -- formatting manifest data

    StringBuffer buffer = new StringBuffer();
    buffer.append("\n");

    if (getContext().getCurrentManifest() instanceof ReleaseManifest) {
      buffer.append("RELEASE MANIFEST\n\n");

      String h1 = "MODULE-NAME";
      String h2 = "VERSION";
      String h3 = "PATCHLINE";
      String h4 = "STATE";
      String h5 = "LOCATION";

      buffer.append(h1 + StringUtils.repeat(" ", 30 - h1.length()) + "| ");
      buffer.append(h2 + StringUtils.repeat(" ", 20 - h2.length()) + "| ");
      buffer.append(h3 + StringUtils.repeat(" ", 20 - h3.length()) + "| ");
      buffer.append(h4 + StringUtils.repeat(" ", 20 - h4.length()) + "| ");
      buffer.append(h5 + StringUtils.repeat(" ", 20 - h5.length()) + "|\n");
      buffer.append(StringUtils.repeat("_", h5.length() + 110));
      buffer.append("\n");

      for (Iterator i = renderedData.iterator(); i.hasNext();) {

        String[] data = (String[]) i.next();

        // Column 1
        //
        buffer.append(data[0] + StringUtils.repeat(" ", 30 - data[0].length()) + "| ");

        // Column 2
        //
        buffer.append(data[1] + " " + data[2] + " " + data[3]);
        buffer.append(StringUtils.repeat(" ", 20 - data[1].length() - data[2].length() - data[3].length() - 2) + "| ");

        // Cols 3-5
        //
        buffer.append(data[4] + StringUtils.repeat(" ", 20 - data[4].length()) + "| ");
        buffer.append(data[5] + StringUtils.repeat(" ", 20 - data[5].length()) + "| ");
        buffer.append(data[6] + StringUtils.repeat(" ", 20 - data[6].length()) + "|\n");
      }

    } else {

      buffer.append("DEVELOPMENT MANIFEST\n\n");

      String h1 = "MODULE-NAME";
      String h2 = "VERSION";
//    String h3 = "PATCHLINE";
      String h4 = "STATE";
      String h5 = "LOCATION";

      buffer.append(h1 + StringUtils.repeat(" ", 30 - h1.length()) + "| ");
      buffer.append(h2 + StringUtils.repeat(" ", 20 - h2.length()) + "| ");
//    buffer.append(h3 + StringUtils.repeat(" ", 20 - h3.length()) + "| ");
      buffer.append(h4 + StringUtils.repeat(" ", 20 - h4.length()) + "| ");
      buffer.append(h5 + StringUtils.repeat(" ", 25 - h5.length()) + "|\n");
      buffer.append(StringUtils.repeat("_", h5.length() + 90));
      buffer.append("\n");

      for (Iterator i = renderedData.iterator(); i.hasNext();) {

        String[] data = (String[]) i.next();

        // Column 1
        //
        buffer.append(data[0] + StringUtils.repeat(" ", 30 - data[0].length()) + "| ");

        // Column 2
        //
        buffer.append(data[1] + " " + data[2] + " " + data[3]);
        buffer.append(StringUtils.repeat(" ", 20 - data[1].length() - data[2].length() - data[3].length() - 2) + "| ");

        // Cols 3-5
        //
        //buffer.append(data[4] + StringUtils.repeat(" ", 20 - data[4].length()) + "| ");
        buffer.append(data[5] + StringUtils.repeat(" ", 20 - data[5].length()) + "| ");
        buffer.append(data[6] + StringUtils.repeat(" ", 25 - data[6].length()) + "|\n");
      }
    }

    // -- end of formatting

    message = new SuccessMessage(buffer.toString());
    commandResponse.addMessage(message);
  }

  public CommandResponse getCommandResponse() {
    return this.commandResponse;
  }
}
