package nl.toolforge.karma.core.cmd.impl;

import nl.toolforge.karma.core.KarmaException;
import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.cmd.CommandResponseHandler;
import nl.toolforge.karma.core.cmd.DefaultCommand;

/**
 * <p>Bogus implementation of {@link nl.toolforge.karma.core.cmd.DefaultCommand}. Used in testcases.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public final class CommandFaker extends DefaultCommand {

	public CommandFaker(CommandDescriptor descriptor) throws KarmaException {
		super(null);

		throw new RuntimeException("Implementation " + CommandFaker.class.getName() + " is fake ...");
	}

	public void execute(CommandResponseHandler handler) throws KarmaException {
		throw new RuntimeException("Implementation " + CommandFaker.class.getName() + " is fake ...");
	}
}
