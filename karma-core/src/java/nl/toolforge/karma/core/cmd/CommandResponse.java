package nl.toolforge.karma.core.cmd;

import nl.toolforge.karma.core.KarmaException;

import java.util.ArrayList;
import java.util.List;

/**
 * Command response objects are returned by the execute method of the Command Object. It is recognized that a GUI needs
 * to be aware of the result of some commands. Commands do sometimes log a few things through the logging facilities,
 * but that's something that is not very usefull for a GUI.
 *
 * @author W.M. Oosterom
 * @author D.A. Smedes
 *
 * @version $Id$
 */
public abstract class CommandResponse {

	private List commandMessages = null;

	// Contains the exception that was thrown during execution of the command
	//
	private Exception commandException = null;

	public CommandResponse() {
		commandMessages = new ArrayList();
	}

	/**
	 * When a command is run, the command response will catch <code>KarmaException</code>s. This method returns the
	 * exception as it was thrown. Interface applications are recommended to handle command errors nicely by quering
	 * the <code>CommandResponse</code> and not by requesting <code>getException().getMessage()</code>.
	 *
	 * @return The exception that was thrown during execution of the command.
	 */
	public KarmaException getException() {
		return (KarmaException) commandException;
	}

	/**
	 * Checks if the command execution resulted in a response that is worth querying.
	 *
	 * @return <code>true</code> When this command response has something to tell.
	 */
	public final boolean hasResponse() {
		return true;
	}

	/**
	 * Gets all command messages in an array.
	 *
	 * @return An array of <code>CommandMessage</code> objects.
	 */
	public final CommandMessage[] getMessages() {
     return (CommandMessage[]) commandMessages.toArray();
	}

	public abstract void addMessage(CommandMessage message);

	protected void add(CommandMessage message) {
		commandMessages.add(message);
	}
}
