package nl.toolforge.karma.core.cmd.impl;

import nl.toolforge.karma.core.KarmaException;
import nl.toolforge.karma.core.Manifest;
import nl.toolforge.karma.core.ManifestException;
import nl.toolforge.karma.core.cmd.*;

/**
 * @author W.H. Schraal
 *
 * @version $Id$
 */
public class SelectManifest extends DefaultCommand {

  public SelectManifest(CommandDescriptor descriptor) {
    super(descriptor);
  }

  public CommandResponse execute() throws ManifestException {

    // Select a manifest and store it in the command context
    //
    getContext().changeCurrent(getOptions().getOption("m").getValue());

    return new SimpleCommandResponse();
  }

}
