package nl.toolforge.karma.core.cmd.impl;

import nl.toolforge.karma.core.KarmaException;
import nl.toolforge.karma.core.Module;
import nl.toolforge.karma.core.ModuleMap;
import nl.toolforge.karma.core.cmd.*;

import java.util.Iterator;

/**
 * This command updates all modules in the active manifest on a developers' local system.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public class UpdateAllModulesCommand extends DefaultCommand {

	/**
	 * Creates a <code>UpdateAllModulesCommand</code> for module <code>module</code> that should be updated.
	 *
	 * @param descriptor The command descriptor for this command.
	 */
	public UpdateAllModulesCommand(CommandDescriptor descriptor) throws CommandException {
		super(descriptor);
	}

	/**
	 * This command will update all modules in the active manifest from the version control system. An update is done when
	 * the module is already present, otherwise a checkout will be performed. The checkout directory for the module
	 * is relative to the root directory of the <code>active</code> manifest.
	 *
	 * @throws KarmaException When no manifest is loaded, a {@link nl.toolforge.karma.core.cmd.CommandException#NO_MANIFEST_SELECTED} is thrown. For
	 *                        other errors, a more generic {@link KarmaException} is thrown.
	 */
	public CommandResponse execute() throws KarmaException {

		// A manifest must be present for this command
		//
		if (!getContext().isManifestLoaded()) {
			throw new CommandException(CommandException.NO_MANIFEST_SELECTED);
		}

		// todo what to do about jarmodules etc ?
		//
		ModuleMap modules = getContext().getCurrent().getModules();

		// Loop through all modules and use UpdateModuleCommand on each module.
		//
		CommandResponse finalResponse = new SimpleCommandResponse();
		for (Iterator i = modules.keySet().iterator(); i.hasNext();) {

			Module module = (Module) modules.get(i.next());

			// todo hmm, the commandname is hardcoded whilst we have it dynamic in a file ...
			//
			finalResponse.addResponse(getContext().execute("update-module -m ".concat(module.getName())));
		}

		return finalResponse;
	}
}
