package nl.toolforge.karma.core.cmd.impl;

import nl.toolforge.karma.core.KarmaException;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.DefaultCommand;

/**
 * <p>Bogus implementation of {@link nl.toolforge.karma.core.cmd.DefaultCommand}. Used in testcases.
 *
 * @author D.A. Smedes
 * @version $revision$ $date$ $author$
 */
public final class CommandFaker extends DefaultCommand {

	public CommandResponse execute() throws KarmaException {
		throw new RuntimeException("Implementation " + CommandFaker.class.getName() + " is fake ...");
	}
}
