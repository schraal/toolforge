package nl.toolforge.karma.core.cmd;

import nl.toolforge.karma.core.KarmaException;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.PosixParser;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author D.A. Smedes
 */
public class CommandContext {

	// Reference to all loaded commands
	//
	private Set commands = null;

	// Reference ro all command names
	//
	private Set commandNames = null;

	private boolean initialized = false;

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
			commands = CommandLoader.getInstance().load();

			// Create a set of all command names.
			//
            commandNames = new HashSet();
			for (Iterator i = commands.iterator(); i.hasNext();) {
				CommandDescriptor descriptor = (CommandDescriptor) i.next();
				commandNames.add(descriptor.getName());
				commandNames.add(descriptor.getAlias());
			}
		}
		initialized = true;
	}

	/**
	 * Executes a command. Interface applications should use this method to actually execute a command.
	 *
	 * @param commandName
	 * @param commandLine The command to execute. A full command line is passed as a parameter.
	 * @return The result of the execution run of the command.
	 * @throws KarmaException When the context was not initialized ({@link #isInitialized}).
	 */
	public CommandResponse execute(String commandName, String commandLine) throws KarmaException {

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

	public Set commands() {

		return commands;
	}

	/**
	 * Checks if some string is a command within this context.
	 *
	 * @param name
	 * @return
	 */
	public boolean isCommand(String name) {

       return commandNames.contains(name);
	}
}
