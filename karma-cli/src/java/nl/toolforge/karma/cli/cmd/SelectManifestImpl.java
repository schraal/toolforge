package nl.toolforge.karma.cli.cmd;

import nl.toolforge.karma.core.cmd.impl.SelectManifest;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.KarmaException;
import nl.toolforge.karma.core.ManifestException;

/**
 * @author W.H. Schraal
 *
 * @version $Id$
 */
public class SelectManifestImpl extends SelectManifest {

  public SelectManifestImpl(CommandDescriptor descriptor) throws ManifestException {
    super(descriptor);
  }

  public CommandResponse execute() throws KarmaException {
    return null;
  }
}
