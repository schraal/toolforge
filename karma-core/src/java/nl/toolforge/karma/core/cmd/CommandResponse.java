package nl.toolforge.karma.core.cmd;

import nl.toolforge.karma.core.KarmaException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Command response objects are returned by the execute method of the Command Object. It is recognized that a GUI needs
 * to be aware of the result of some commands. Therefor, all communication from to command back to the caller goes through
 * a response object.
 *
 * @author W.M. Oosterom
 * @author D.A. Smedes
 * @author W.H. Schraal
 * @version $Id$
 */
public abstract class CommandResponse {

	private List commandMessages = null;

	private Set statusUpdates = null;

	// Contains the exception that was thrown during execution of the command
	//
	private Exception commandException = null;

	public CommandResponse() {
		commandMessages = new ArrayList();
		statusUpdates = new HashSet();
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

		return (CommandMessage[]) commandMessages.toArray(new SimpleCommandMessage[commandMessages.size()]);
	}

  /**
   * Add a message to the command response.
   *
   * @param message  The message to add to the response.
   */
	public void addMessage(CommandMessage message) {
    commandMessages.add(message);
  }


	/**
	 * The response maintains an list of status updates that have been set by commands. For instance, a CVS repository
	 * generates a <code>fileAdded</code> event, when a file has been added. The class that fetches these events, should
	 * implement constant values for these statusses and add them here. This class can then be queried for these
	 * statusses and do something with it.
	 *
	 * @param statusIdentifier A status identifier. Should be unique.
	 * @see #hasStatus
	 */
	public synchronized final void addStatusUpdate(Integer statusIdentifier) throws CommandException {

		if (statusUpdates.contains(statusIdentifier)) {
			throw new CommandException(CommandException.DUPLICATE_COMMAND_STATUS);
		}

		statusUpdates.add(statusIdentifier);
	}

	/**
	 * Checks if this response has a status update <code>statusIdentifier</code> attached.
	 *
	 * @param statusIdentifier A status identifier.
	 * @return <code>true</code> when this response has the status update attached, <code>false</code> if it hasn't.
	 */
	public final boolean hasStatus(Integer statusIdentifier) {
		return statusUpdates.contains(statusIdentifier);
	}

	/**
	 * Clears all statusses for the response.
	 */
	public final void clearStatus() {

		statusUpdates.removeAll(statusUpdates);
	}

	/**
	 * Includes <code>response</code> in this response.
	 *
	 * @param response The response to include in this response.
	 */
	public final void addResponse(CommandResponse response) {

		CommandMessage[] messages = response.getMessages();

		for (int i = 0; i < messages.length; i++) {
			this.addMessage(messages[i]);
		}
	}
}
