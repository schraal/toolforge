package nl.toolforge.karma.cli.cmd;

import nl.toolforge.karma.core.cmd.impl.PromoteCommand;
import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.SimpleCommandMessage;
import nl.toolforge.karma.core.cmd.CommandMessage;
import nl.toolforge.karma.core.cmd.CommandResponseHandler;
import nl.toolforge.karma.core.cmd.ActionCommandResponse;
import nl.toolforge.karma.core.ManifestException;
import nl.toolforge.karma.core.KarmaException;

public class PromoteModuleImpl extends PromoteCommand {

	public PromoteModuleImpl(CommandDescriptor descriptor) {
		super(descriptor);
	}

	public void execute() {

		super.execute();

		CommandMessage message =
			new SimpleCommandMessage(
				getFrontendMessages().getString("message.MODULE_PROMOTED"),
				new Object[]{getCommandLine().getOptionValue("m"), getNewVersion()}
			);

		CommandResponse response = new ActionCommandResponse();
    response.addMessage(message);
	}


}
