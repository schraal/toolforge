package nl.toolforge.karma.core.cmd.impl;

import nl.toolforge.karma.core.KarmaException;
import nl.toolforge.karma.core.exception.ErrorCode;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.DefaultCommand;
import nl.toolforge.karma.core.cmd.CommandDescriptor;
import org.apache.commons.cli.Options;

/**
 * <p>Bogus implementation of {@link nl.toolforge.karma.core.cmd.DefaultCommand}. Used in testcases.
 *
 * @author D.A. Smedes
 */
public final class CommandFaker extends DefaultCommand {

//	public CommandFaker(CommandDescriptor descriptor) throws KarmaException {
//		//super(descriptor);
//	}

	public CommandResponse executeCommand() throws KarmaException {
		throw new RuntimeException("Implementation " + CommandFaker.class.getName() + " is fake ...");
	}
}
