package nl.toolforge.karma.cli.cmd;

import nl.toolforge.karma.core.cmd.impl.PromoteCommand;
import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.SimpleCommandMessage;
import nl.toolforge.karma.core.cmd.CommandMessage;
import nl.toolforge.karma.core.ManifestException;
import nl.toolforge.karma.core.KarmaException;

public class PromoteModuleImpl extends PromoteCommand {

	public PromoteModuleImpl(CommandDescriptor descriptor) {
		super(descriptor);
	}

	public CommandResponse execute() throws KarmaException {

		CommandResponse response = super.execute();

		CommandMessage message =
			new SimpleCommandMessage(
				getFrontendMessages().getString("message.MODULE_PROMOTED"),
				new Object[]{getCommandLine().getOptionValue("m"), getNewVersion()}
			);

		response.addMessage(message);

		return response;
	}


}
