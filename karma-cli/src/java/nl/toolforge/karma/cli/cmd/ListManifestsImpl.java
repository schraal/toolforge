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
import nl.toolforge.karma.core.cmd.QueryCommandResponse;
import nl.toolforge.karma.core.cmd.SuccessMessage;
import nl.toolforge.karma.core.cmd.impl.ListManifests;
import nl.toolforge.karma.core.manifest.Manifest;
import nl.toolforge.karma.core.manifest.ManifestException;

import java.util.Collection;
import java.util.Iterator;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * Command line interface implementation of the {@link ListManifests} command.
 *
 * @author D.A. Smedes
 * @author W.H. Schraal
 * @version $Id$
 */
public class ListManifestsImpl extends ListManifests {

  CommandResponse commandResponse = new QueryCommandResponse();

  public ListManifestsImpl(CommandDescriptor descriptor) throws ManifestException {
    super(descriptor);
  }

  public void execute() throws CommandException {
    CommandResponse response = getCommandResponse();

    Collection manifests = null;
    manifests = getContext().getAllManifests();

    if (manifests.size() == 0) {
      response.addMessage(new SuccessMessage("No manifests found."));
    } else {

      List manifestList = new ArrayList();

      Iterator manifestsIterator = manifests.iterator();
      String manifest;
      int index;
      Manifest currentManifest = getContext().getCurrentManifest();
      while (manifestsIterator.hasNext()) {
        manifest = (String) manifestsIterator.next();
        index = manifest.indexOf(".xml");
        String manifestName = manifest.substring(0, index);
        if ((currentManifest != null) && manifestName.equals(currentManifest.getName())) {
          manifestList.add(" -> " + manifestName + " ** current manifest **");
        } else {
          manifestList.add(" -  " + manifestName + " ");
        }
      }

      Collections.sort(manifestList);

      manifestsIterator = manifestList.iterator();
      while (manifestsIterator.hasNext()) {
        response.addMessage(new SuccessMessage((String) manifestsIterator.next()));
      }
    }

  }

  public CommandResponse getCommandResponse() {
    return this.commandResponse;
  }

}