package nl.toolforge.karma.core.cmd;

import nl.toolforge.karma.core.cmd.event.CommandResponseListener;

/**
 * This interface defines a thingy that handles CommandResponses. Each user interface
 * will have its own implementation of this interface.
 *
 * @author W.H. Schraal
 */
public interface CommandResponseHandler extends CommandResponseListener {
}
