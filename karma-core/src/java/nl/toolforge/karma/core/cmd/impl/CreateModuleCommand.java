package nl.toolforge.karma.core.cmd.impl;

import nl.toolforge.karma.core.KarmaException;
import nl.toolforge.karma.core.Module;
import nl.toolforge.karma.core.ModuleFactory;
import nl.toolforge.karma.core.cmd.*;
import nl.toolforge.karma.core.vc.Runner;
import org.apache.commons.cli.CommandLine;

/**
 * Creates a module in a repository. The command provides the option to create the module in the current manifest as
 * well.
 *
 * @author D.A. Smedes
 * @version $Id:
 * @since 2.0
 */
public class CreateModuleCommand extends DefaultCommand {

	public CreateModuleCommand(CommandDescriptor descriptor) {
		super(descriptor);
	}

	/**
	 * Physical creation of a module in a version control system.
	 */
	public CommandResponse execute() throws KarmaException {

		CommandLine commandLine = getCommandLine();

		String locationAlias = commandLine.getOptionValue("l");
		String moduleName = commandLine.getOptionValue("m");
		boolean include = commandLine.hasOption("i");

		// Part 1 of the transaction is the creation of a Module instance.
		//

		// The manifest itself is responsible for creating new modules.
		//
		Module module = null;
		if (include) {
			// Include the module in the manifest
			//
			if (!getContext().isManifestLoaded()) {
				throw new CommandException(CommandException.NO_MANIFEST_SELECTED);
			}
			module = getContext().getCurrent().createModule(moduleName, locationAlias, true);

		} else {
			// Just create the module
			//
			module = ModuleFactory.getInstance().createModule(Module.SOURCE_MODULE, moduleName, locationAlias);
		}

		// Part 2 of the transaction is the creation in a version control system.
		//
		Runner runner = getContext().getRunner(module);

		runner.create(module);

		// If we get to this point, creation of the module was succesfull.
		//
		CommandMessage message =
				new SimpleCommandMessage(getFrontendMessages().getString("message.MODULE_CREATED"), new Object[]{moduleName, locationAlias});

		// Ensure that only this message is passed back to the client
		//
		CommandResponse response = new SimpleCommandResponse();
		response.addMessage(message);

		return response;
	}
}