package nl.toolforge.karma.core.cmd;

import nl.toolforge.karma.core.cmd.event.CommandResponseListener;

/**
 * This interface defines how CommandResponses are handled. Each user interface
 * would have its own implementation of this interface.
 *
 * @author W.H. Schraal
 */
public interface CommandResponseHandler extends CommandResponseListener {
}
