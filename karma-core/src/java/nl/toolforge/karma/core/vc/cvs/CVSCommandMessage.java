package nl.toolforge.karma.core.vc.cvs;

import nl.toolforge.karma.core.cmd.SimpleCommandMessage;

/**
 * Message implementation for CVS messages.
 *
 * @author D.A. Smedes
 * @version $Id:
 */
public final class CVSCommandMessage extends SimpleCommandMessage {

	//public Collection events = null;

	public CVSCommandMessage(String message) {
		super(message);
	}

	public CVSCommandMessage(String message, Object[] parameters) {
		super(message, parameters);
	}

}