package nl.toolforge.karma.core.cmd.impl;

import nl.toolforge.karma.core.KarmaException;
import nl.toolforge.karma.core.Module;
import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.DefaultCommand;
import nl.toolforge.karma.core.vc.Runner;

/**
 * This command updates a module on a developers' local system.
 *
 * @author D.A. Smedes
 *
 * @version $Id$
 */
public class UpdateModuleCommand extends DefaultCommand {

	private Module module = null;

	/**
	 * Creates a <code>UpdateModuleCommand</code> for module <code>module</code> that should be updated.
	 *
	 * @param module A module from the manifest.
	 */
	public UpdateModuleCommand(Module module) throws CommandException {
       this.module = module;
	}

	/**
	 * Creates an UpdateModuleCommand.
	 *
	 * @param descriptor See {@link DefaultCommand}.
	 */
//	public UpdateModuleCommand(CommandDescriptor descriptor) throws KarmaException {
//		super(descriptor);
//	}

	public CommandResponse execute() throws KarmaException {

		Runner runner = getContext().getRunner(module);



		return null;
	}
}
