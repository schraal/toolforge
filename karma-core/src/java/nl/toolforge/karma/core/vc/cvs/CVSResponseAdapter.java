package nl.toolforge.karma.core.vc.cvs;

import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.cmd.CommandMessage;
import nl.toolforge.karma.core.cmd.CommandResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.netbeans.lib.cvsclient.command.FileInfoContainer;
import org.netbeans.lib.cvsclient.command.log.LogInformation;
import org.netbeans.lib.cvsclient.event.CVSListener;
import org.netbeans.lib.cvsclient.event.FileAddedEvent;
import org.netbeans.lib.cvsclient.event.FileInfoEvent;
import org.netbeans.lib.cvsclient.event.FileRemovedEvent;
import org.netbeans.lib.cvsclient.event.FileUpdatedEvent;
import org.netbeans.lib.cvsclient.event.MessageEvent;
import org.netbeans.lib.cvsclient.event.ModuleExpansionEvent;
import org.netbeans.lib.cvsclient.event.TerminationEvent;

/**
 * Adapts a response from CVS to Karma specific messages. This class listens to CVS responses as per the Netbeans API.
 *
 * @author D.A. Smedes
 * @version $Id:
 */
public final class CVSResponseAdapter extends CommandResponse implements CVSListener {

  /**
   * CVS aborted unexpectedly
   */
  public static final Integer INVALID_SYMBOLIC_NAME = new Integer(0);

  /**
   * File has succesfully been added to the CVS repository.
   */
  public static final Integer FILE_ADDED_OK = new Integer(1);

  /**
   * File has succesfully been removed from the CVS repository.
   */
  public static final Integer FILE_REMOVED_OK = new Integer(2);

  /**
   * Module has succesfully been updated from CVS.
   */
  public static final Integer MODULE_UPDATED_OK = new Integer(3);

  /**
   * The module does not exist in the CVS repository.
   */
  public static final Integer MODULE_NOT_FOUND = new Integer(4);

  /**
   * File already exists in the CVS repository.
   */
  public static final Integer FILE_EXISTS = new Integer(5);

  /**
   * The symbolic name does not exist.
   */
  public static final Integer SYMBOLIC_NAME_NOT_FOUND = new Integer(6);

  private FileInfoContainer logInformation = null;

  private static Log logger = LogFactory.getLog(CVSResponseAdapter.class);

  /**
   * Adds a message to a <code>CommandResponse</code>s' messages list.
   *
   * @param message A <code>CommandMessage</code>. All types are valid, yet it is wise to add a
   *                <code>CVSCommandMessage</code>
   */
  public void addMessage(CommandMessage message) {
    super.addMessage(message);
  }

  /**
   * <p>Copied from the Netbeans API documentation : Called when a file is removed.
   *
   * @param event The event from CVS.
   */
  public void fileRemoved(FileRemovedEvent event) {

    logger.debug("FileRemovedEvent from CVS");

//    if (!hasStatus(FILE_REMOVED_OK)) {
//      try { addStatusUpdate(FILE_REMOVED_OK); } catch (CommandException e) { } // Ignore
//    }
  }

  /**
   * <p>Copied from the Netbeans API documentation : Fire a module expansion event. This is called when the servers has
   * responded to an expand-modules request.
   * <p/>
   * <p>Copied from the Netbeans API documentation : This event is really intended only for the use in the Checkout command. During a checkout command, the client
   * must ask the server to expand modules to determine whether there are aliases defined for a particular module. The
   * client must then use the expansion to determine if a local directory exists and if so, send appropriate Modified
   * requests etc.
   *
   * @param event The event from CVS.
   */
  public void moduleExpanded(ModuleExpansionEvent event) {
    //logger.debug("ModuleExpansionEvent from CVS");
  }

  /**
   * <p>Copied from the Netbeans API documentation : Called when a file has been added.
   *
   * @param event The event from CVS.
   */
  public void fileAdded(FileAddedEvent event) {
    //
  }

  /**
   * <p>Copied from the Netbeans API documentation : Called when file information has been received.
   * <p/>
   * <p>This method constructs the <code>LogInformation</code> object that contains the log for a specific file as
   * a result of the <code>cvs log</code> command.
   *
   * @param event The event from CVS.
   */
  public void fileInfoGenerated(FileInfoEvent event) {
    this.logInformation = event.getInfoContainer();
  }

  /**
   * Gets the log that is the result of the <code>cvs log</code> command.
   *
   * @return A <code>LogInformation</code> that can be queried by classes for all information on a (set of) file(s).
   */
  public LogInformation getLogInformation() {
    return (LogInformation) this.logInformation;
  }

  /**
   * <p>Copied from the Netbeans API documentation : Called when server responses with "ok" or "error", (when the command finishes)
   *
   * @param event The event from CVS.
   */
  public void commandTerminated(TerminationEvent event) {
    //logger.debug("TerminationEvent from CVS : " + event.toString());
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
   * <p>Copied from the Netbeans API documentation : Called when the server wants to send a message to be displayed to
   * the user. This method is called whenever
   *
   * @param event The event from CVS.
   */
  public void messageSent(MessageEvent event) {

    // Get the message from CVS and parse it into something usefull.
    //
    String message = event.getMessage();

    if (message.startsWith("Checking in")) {

      // TODO Localize message
      String messageText = "File has been added to the CVS repository.";

      addMessage(new CVSCommandMessage(messageText));

      if (!hasStatus(FILE_ADDED_OK)) {
        try {
          addStatusUpdate(FILE_ADDED_OK);
        } catch (CommandException e) {
        } // Ignore
      }
    } else if (message.startsWith("cvs server: Updating")) {

      // TODO Localize message
      String messageText = "Module has been updated.";

      addMessage(new CVSCommandMessage(messageText));

      if (!hasStatus(MODULE_UPDATED_OK)) {
        try {
          addStatusUpdate(MODULE_UPDATED_OK);
        } catch (CommandException e) {
        } // Ignore
      }
    } else if (message.startsWith("cvs server: cannot find module")) {

      // TODO Localize message
      String messageText = "Module does not exist in repository.";

      addMessage(new CVSCommandMessage(messageText));

      if (!hasStatus(MODULE_NOT_FOUND)) {
        try {
          addStatusUpdate(MODULE_NOT_FOUND);
        } catch (CommandException e) {
        } // Ignore
      }
    } else if (message.startsWith("cvs add:") && message.indexOf("already exists") > 0) {

      // TODO Localize message; guess this is handled by calling class ...
      String messageText = "File already exists in repository.";

      addMessage(new CVSCommandMessage(messageText));

      if (!hasStatus(FILE_EXISTS)) {
        try {
          addStatusUpdate(FILE_EXISTS);
        } catch (CommandException e) {
        } // Ignore
      }
    } else if (message.startsWith("cvs") && message.indexOf("no such tag") > 0) {

      // TODO Localize message; guess this is handled by calling class ...
      String messageText = "Symbolic name not found.";

      addMessage(new CVSCommandMessage(messageText));

      if (!hasStatus(SYMBOLIC_NAME_NOT_FOUND)) {
        try {
          addStatusUpdate(SYMBOLIC_NAME_NOT_FOUND);
        } catch (CommandException e) {
        } // Ignore
      }
    } else if (message.indexOf("contains characters other than digits") > 0) {
      String messageText = event.getMessage();

      addMessage(new CVSCommandMessage(messageText));

      if (!hasStatus(INVALID_SYMBOLIC_NAME)) {
        try {
          addStatusUpdate(INVALID_SYMBOLIC_NAME);
        } catch (CommandException e) {
        } // Ignore
      }
    }

    logger.debug("MessageEvent from CVS : " + event.getMessage());
  }

}