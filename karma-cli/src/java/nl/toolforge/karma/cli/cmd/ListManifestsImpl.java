package nl.toolforge.karma.cli.cmd;

import nl.toolforge.karma.core.cmd.*;
import nl.toolforge.karma.core.cmd.impl.ListManifests;
import nl.toolforge.karma.core.manifest.ManifestException;
import nl.toolforge.karma.core.manifest.Manifest;

import java.util.Collection;
import java.util.Iterator;

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

      Iterator manifestsIterator = manifests.iterator();
      String manifest;
      int index;
      Manifest currentManifest = getContext().getCurrentManifest();
      while (manifestsIterator.hasNext()) {
        manifest = (String) manifestsIterator.next();
        index = manifest.indexOf(".xml");
        String manifestName = manifest.substring(0, index);
        if ((currentManifest != null) && manifestName.equals(currentManifest.getName())) {
          response.addMessage(new SimpleCommandMessage(" -> "+manifestName+" (selected)"));
        } else {
          response.addMessage(new SimpleCommandMessage(" -  "+manifestName));
        }
      }
    }
  }

  public CommandResponse getCommandResponse() {
    return this.commandResponse;
  }

}