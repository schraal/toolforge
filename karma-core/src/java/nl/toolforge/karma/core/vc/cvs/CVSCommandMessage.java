package nl.toolforge.karma.core.vc.cvs;

import nl.toolforge.karma.core.cmd.AbstractCommandMessage;

/**
 * Message implementation for CVS messages.
 *
 *
 * @author D.A. Smedes
 * @version $Id:
 *
 * @deprecated Use direct subclasses of <code>AbstractCommandMessage</code> instead.
 */
public final class CVSCommandMessage extends AbstractCommandMessage {

	public CVSCommandMessage(String message) {
		super(message);
	}

	public CVSCommandMessage(String message, Object[] parameters) {
		super(message, parameters);
	}

}