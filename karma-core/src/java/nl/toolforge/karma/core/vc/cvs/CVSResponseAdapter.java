package nl.toolforge.karma.core.vc.cvs;

import nl.toolforge.karma.core.cmd.CommandMessage;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.CommandException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.netbeans.lib.cvsclient.event.*;

/**
 * Adapts a response from CVS to Karma specific messages. This class listens to CVS responses as per the Netbeans API.
 *
 * @author D.A. Smedes 
 * 
 * @version $Id:
 */
public final class CVSResponseAdapter extends CommandResponse implements CVSListener {

  public static final Integer FILE_ADDED_OK = new Integer(0);
  public static final Integer FILE_REMOVED_OK = new Integer(1);

	private static Log logger = LogFactory.getLog(CVSResponseAdapter.class);

	/**
	 * Adds a message to a <code>CommandResponse</code>s' messages list.
   *
	 * @param message A <code>CommandMessage</code>. All types are valid, yet it is wise to add a
   *   <code>CVSCommandMessage</code>
	 */
	public void addMessage(CommandMessage message) {
		add(message);
	}

	/**
	 * <p>Copied from the Netbeans API documentation : Called when a file is removed.
	 *
	 * @param event The event from CVS.
	 */
	public void fileRemoved(FileRemovedEvent event) {

		logger.debug("FileRemovedEvent from CVS : " + event.toString());

    if (!hasStatus(FILE_REMOVED_OK)) {
      try { addStatusUpdate(FILE_REMOVED_OK); } catch (CommandException e) { } // Ignore
    }

	}

	/**
	 * <p>Copied from the Netbeans API documentation : Fire a module expansion event. This is called when the servers has
	 * responded to an expand-modules request.
	 *
	 * <p>Copied from the Netbeans API documentation : This event is really intended only for the use in the Checkout command. During a checkout command, the client
	 * must ask the server to expand modules to determine whether there are aliases defined for a particular module. The
	 * client must then use the expansion to determine if a local directory exists and if so, send appropriate Modified
	 * requests etc.
	 *
	 * @param event The event from CVS.
	 */
	public void moduleExpanded(ModuleExpansionEvent event) {
		logger.debug("ModuleExpansionEvent from CVS : " + event.toString());
	}

	/**
	 * <p>Copied from the Netbeans API documentation : Called when a file has been added.
	 *
	 * @param event The event from CVS.
	 */
	public void fileAdded(FileAddedEvent event) {

    String messageText = "File " + event.getFilePath() + " has been added to the CVS repository.";

    addMessage(new CVSCommandMessage(messageText));

    if (!hasStatus(FILE_ADDED_OK)) {
      try { addStatusUpdate(FILE_ADDED_OK); } catch (CommandException e) { } // Ignore
    }

		logger.debug("FileAddedEvent from CVS : " + event.toString());
	}

	/**
	 * <p>Copied from the Netbeans API documentation : Called when file information has been received.
	 *
	 * @param event The event from CVS.
	 */
	public void fileInfoGenerated(FileInfoEvent event) {
		logger.debug("FileInfoEvent from CVS : " + event.toString());
	}


	/**
	 * <p>Copied from the Netbeans API documentation : Called when server responses with "ok" or "error", (when the command finishes)
	 *
	 * @param event The event from CVS.
	 */
	public void commandTerminated(TerminationEvent event) {
		logger.debug("TerminationEvent from CVS : " + event.toString());
	}


	/**
	 * <p>Copied from the Netbeans API documentation : Called when a file has been updated.
	 *
	 * @param event The event from CVS.
	 */
	public void fileUpdated(FileUpdatedEvent event) {
		logger.debug("FileUpdatedEvent from CVS : " + event.toString());
	}

	/**
	 * <p>Copied from the Netbeans API documentation : Called when the server wants to send a message to be displayed to the user. The message is only for information
	 * purposes and clients can choose to ignore these messages if they wish.
	 *
	 * @param event The event from CVS.
	 */
	public void messageSent(MessageEvent event) {
		logger.debug("MessageEvent from CVS : " + event.toString());



	}

}