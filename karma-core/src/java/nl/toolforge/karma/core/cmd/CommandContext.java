package nl.toolforge.karma.core.cmd;

import nl.toolforge.karma.core.exception.ErrorCode;
import nl.toolforge.karma.core.KarmaException;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;

/**
 *
 * @author D.A. Smedes
 */
public class CommandContext {

	private static Options options = null;

	private boolean initialized = false;

	public CommandContext() {

	}

	/**
	 * <p>Checks if this <code>CommandContext</code> has been initialized. A non-initialized context cannot be used and
	 * methods in this class will throw a <code>KarmaException</code> with error code
	 * <code>KarmaException.COMMAND_CONTEXT_NOT_INITIALIZED</code> if a non-initialized context is encountered.
	 *
	 * @return <code>true</code> if this command context has been initialized, false if it isn't
	 */
	public boolean isInitialized() {
		return initialized;
	}

	/**
	 * Initializes the context to run commands. This method can only be called once.
	 *
	 * @throws KarmaException
	 */
	public synchronized void init() throws KarmaException {

		if (!initialized) {



		}
		initialized = true;
	}

	/**
	 * Executes a command. Interface applications should use this method to actually execute a command.
	 *
	 * @param commandLine The command to execute. A full command line is passed as a parameter.
	 * @return The result of the execution run of the command.
	 * @throws KarmaException When the context was not initialized ({@link #isInitialized}).
	 */
	public CommandResponse execute(String commandLine) throws KarmaException {

		if (!isInitialized()) {
			throw new KarmaException(KarmaException.COMMAND_CONTEXT_NOT_INITIALIZED);
		}

		CommandLineParser parser = new PosixParser();

		// A command is extracted from the commandLine
		//
		Command command = null;

		return execute(command);
	}

	public CommandResponse execute(Command command) throws KarmaException {

		if (!isInitialized()) {
			throw new KarmaException(KarmaException.COMMAND_CONTEXT_NOT_INITIALIZED);
		}

		return new CommandResponse();
	}

}
