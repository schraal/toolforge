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

import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.event.MessageEvent;
import nl.toolforge.karma.core.cmd.event.SimpleMessage;
import nl.toolforge.karma.core.cmd.impl.ListManifests;
import nl.toolforge.karma.core.manifest.Manifest;
import nl.toolforge.karma.core.manifest.ManifestException;
import nl.toolforge.karma.core.manifest.ManifestHeader;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Command line interface implementation of the {@link ListManifests} command.
 *
 * @author D.A. Smedes
 * @author W.H. Schraal
 * @version $Id$
 */
public class ListManifestsImpl extends ListManifests {

//  CommandResponse commandResponse = new QueryCommandResponse();
  private CommandResponse commandResponse = new CommandResponse();

  public ListManifestsImpl(CommandDescriptor descriptor) throws ManifestException {
    super(descriptor);
  }

  public void execute() throws CommandException {

    super.execute();

    CommandResponse response = getCommandResponse();

    Set headers = getHeaders();

    if (headers.size() == 0) {
      response.addEvent(new MessageEvent(this, new SimpleMessage("No manifests found.")));
    } else {

      Iterator manifestsIterator = headers.iterator();
      ManifestHeader header;

      Manifest currentManifest = getContext().getCurrentManifest();

      StringBuffer buffer = new StringBuffer();

      buffer.append("\nManifests for working context `"+ getWorkingContext().getName() +"`:\n\n");

      String h1 = "Type";
      int MAX_TYPE_LENGTH = 13;

      String h2 = "Name";
      int MAX_NAME_LENGTH = 30;

      buffer.append(h1 + StringUtils.repeat(" ", 13 - h1.length()) + " | ");
      buffer.append(h2 + StringUtils.repeat(" ", 32 - h1.length()) + "\n");
      buffer.append(StringUtils.repeat("_", 71) + "\n");


      List manifestList = new ArrayList();

      while (manifestsIterator.hasNext()) {

        header = (ManifestHeader) manifestsIterator.next();

        String typeSpaces = StringUtils.repeat(" ", MAX_TYPE_LENGTH - header.getType().length());
        String nameSpaces = StringUtils.repeat(" ", MAX_NAME_LENGTH - header.getName().length());

        if ((currentManifest != null) && header.getName().equals(currentManifest.getName())) {
          manifestList.add(header.getType() + typeSpaces + " | " + header.getName() + nameSpaces + " ** current manifest **\n");
        } else {
          manifestList.add(header.getType() + typeSpaces + " | " + header.getName() + nameSpaces + "\n");
        }
      }

      Collections.sort(manifestList);

      manifestsIterator = manifestList.iterator();
      while (manifestsIterator.hasNext()) {

        buffer.append((String) manifestsIterator.next());

//        response.addMessage(new SuccessMessage((String) manifestsIterator.next()));
      }
      response.addEvent(new MessageEvent(this, new SimpleMessage(buffer.toString())));
    }

  }

  public CommandResponse getCommandResponse() {
    return this.commandResponse;
  }

}