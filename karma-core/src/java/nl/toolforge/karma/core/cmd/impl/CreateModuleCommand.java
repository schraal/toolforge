package nl.toolforge.karma.core.cmd.impl;

import org.apache.commons.cli.CommandLine;

import nl.toolforge.karma.core.KarmaException;
import nl.toolforge.karma.core.Module;
import nl.toolforge.karma.core.ModuleFactory;
import nl.toolforge.karma.core.ManifestException;
import nl.toolforge.karma.core.cmd.ActionCommandResponse;
import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.cmd.CommandMessage;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.DefaultCommand;
import nl.toolforge.karma.core.cmd.SimpleCommandMessage;
import nl.toolforge.karma.core.cmd.CommandResponseHandler;
import nl.toolforge.karma.core.cmd.ErrorMessage;
import nl.toolforge.karma.core.cmd.SuccessMessage;
import nl.toolforge.karma.core.cmd.event.CommandResponseEvent;
import nl.toolforge.karma.core.vc.Runner;
import nl.toolforge.karma.core.vc.RunnerFactory;
import nl.toolforge.karma.core.vc.VersionControlException;

/**
 * Creates a module in a repository. The command provides the option to create the module in the current manifest as
 * well.
 *
 * @author D.A. Smedes
 * @version $Id:
 * @since 2.0
 */
public class CreateModuleCommand extends DefaultCommand {
  private CommandResponse commandResponse = new ActionCommandResponse();

  public CreateModuleCommand(CommandDescriptor descriptor) {
		super(descriptor);
	}

	/**
	 * Physical creation of a module in a version control system.
	 */
	public void execute() {

		CommandLine commandLine = getCommandLine();

		String locationAlias = commandLine.getOptionValue("l");
		String moduleName = commandLine.getOptionValue("m");
		boolean include = commandLine.hasOption("i");

		// Part 1 of the transaction is the creation of a Module instance.
		//

		// The manifest itself is responsible for creating new modules.
		//
//		Module module = null;
//		if (include) {
//			// Include the module in the manifest
//			//
//			if (!getContext().isManifestLoaded()) {
//				throw new CommandException(CommandException.NO_MANIFEST_SELECTED);
//			}
//
//			module = getContext().getCurrent().createModule(moduleName, locationAlias, true);
//
//		} else {
//			// Just create the module
//			//
//			module = ModuleFactory.getInstance().createModule(Module.SOURCE_MODULE, moduleName, locationAlias);
//		}

    try {
      Module module = ModuleFactory.getInstance().createModule(moduleName, locationAlias);

      // Part 2 of the transaction is the creation in a version control system.
      //
      Runner runner = RunnerFactory.getRunner(module, getContext().getCurrent().getDirectory());
      runner.setCommandResponse(getCommandResponse());
      runner.create(module);

      // If we get to this point, creation of the module was succesfull.
      //
      //todo dit moet anders
      CommandMessage message =
        new SimpleCommandMessage(getFrontendMessages().getString("message.MODULE_CREATED"), new Object[]{moduleName, locationAlias});

      // Ensure that only this message is passed back to the client
      //
      commandResponse.addMessage(new SuccessMessage(message.getMessageText()));
    } catch (KarmaException ke) {
      commandResponse.addMessage(new ErrorMessage(ke));
    }
	}

  public CommandResponse getCommandResponse() {
    return this.commandResponse;
  }
}