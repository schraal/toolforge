package nl.toolforge.karma.core.cmd.impl;

import nl.toolforge.karma.core.KarmaException;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.DefaultCommand;
import nl.toolforge.karma.core.cmd.CommandDescriptor;
import org.apache.commons.cli.Options;

/**
 * This command updates a module on a developers' local system.
 *
 * @author D.A. Smedes
 */
public class UpdateModuleCommand extends DefaultCommand {

	/**
	 * Creates an UpdateModuleCommand.
	 *
	 * @param descriptor See {@link DefaultCommand}.
	 */
	public UpdateModuleCommand(CommandDescriptor descriptor) throws KarmaException {
		super(descriptor);
	}

	public CommandResponse executeCommand() throws KarmaException {
		return null;
	}
}
