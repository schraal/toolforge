package nl.toolforge.karma.core.cmd;

import nl.toolforge.karma.core.KarmaException;

/**
 * Command response objects are returned by the execute method of the Command Object. It is recognized that a GUI needs
 * to be aware of the result of some commands. Commands do sometimes log a few things through the logging facilities,
 * but that's something that is not very usefull for a GUI.
 *
 * @author W.M. Oosterom
 */
public class CommandResponse {

	// Contains the exception that was thrown during execution of the command
	//
	private Exception commandException = null;

	public CommandResponse() {}

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

    // Empty for now, just introduced to have some kind of setup for GUI support
}
