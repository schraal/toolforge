package nl.toolforge.karma.core.cmd.impl;

import nl.toolforge.karma.core.cmd.ActionCommandResponse;
import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.DefaultCommand;
import nl.toolforge.karma.core.cmd.QueryCommandResponse;
import nl.toolforge.karma.core.location.LocationException;
import nl.toolforge.karma.core.manifest.ManifestException;

/**
 * <p>This command activates a manifest, which is a general requirement for most other commands. The newly activated
 * manifest is stored for the Karma session in the {@link nl.toolforge.karma.core.cmd.CommandContext} that is associated
 * with the Karma session.
 *
 * @author W.H. Schraal
 * @author D.A. Smedes
 * @version $Id$
 */
public class SelectManifest extends DefaultCommand {
  private CommandResponse commandResponse = new ActionCommandResponse();

  public SelectManifest(CommandDescriptor descriptor) {
		super(descriptor);
	}

	/**
	 * Activates a manifest.
	 *	 */
	public void execute() throws CommandException {

		// Select a manifest and store it in the command context
		//
    try {
		  getContext().changeCurrentManifest(getCommandLine().getOptionValue("m"));
    } catch (ManifestException me) {
      throw new CommandException(me.getErrorCode(), me.getMessageArguments());
    } catch (LocationException e) {
      throw new CommandException(e.getErrorCode(), e.getMessageArguments());
    }
		new QueryCommandResponse();
	}

  public CommandResponse getCommandResponse() {
    return this.commandResponse;
  }

}
