package nl.toolforge.karma.core.cmd.impl;

import nl.toolforge.karma.core.KarmaException;
import nl.toolforge.karma.core.cmd.Command;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.DefaultCommand;
import nl.toolforge.karma.core.cmd.CommandDescriptor;

/**
 * @author W.H. Schraal
 *
 * @version $Id$
 */
public abstract class SelectManifest extends DefaultCommand {

  public SelectManifest(CommandDescriptor descriptor) {
    super(descriptor);
  }

  public CommandResponse execute(Command command) throws KarmaException {
    System.out.println(command.getName());
    return null;
  }

}
