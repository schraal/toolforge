package nl.toolforge.karma.core.cmd;

/**
 * A simple command response.
 *
 * @author D.A. Smedes  
 * @version $Id$
 */
public class SimpleCommandResponse extends CommandResponse {

	public void addMessage(CommandMessage message) {

		// Do some checking if need be ...
		//

		super.add(message);
	}

}