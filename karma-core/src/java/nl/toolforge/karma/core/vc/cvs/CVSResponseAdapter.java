package nl.toolforge.karma.core.vc.cvs;

import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.SuccessMessage;
import nl.toolforge.karma.core.manifest.Module;
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

import java.util.List;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/**
 * Adapts a response from CVS to Karma specific messages. This class listens to CVS responses as per the Netbeans API.
 * Success messages are sent to the <code>CommandResponse</code> instance (which can optionally be registered with this
 * instance). Errors are thrown as CVSRuntimeExceptions
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public final class CVSResponseAdapter implements CVSListener {

  private FileInfoContainer logInformation = null;
  private CommandResponse response = null;
//  private ErrorCode errorCode = null;

  private static Log logger = LogFactory.getLog(CVSResponseAdapter.class);
  private Map arguments = null;

//  private String moduleName = null;
//  private String version = null;

  public CVSResponseAdapter() {}

  /**
   * This class can use a <code>CommandResponseListener</code> to send cvs events to. For example, user interfaces can
   * register a listener to receive events from underlying code, thus creating interactivity.
   *
   * @param response
   */
  public CVSResponseAdapter(CommandResponse response) {
    this.response = response;
  }

  public void setArguments(Map arguments) {
    this.arguments = arguments;
  }

  public Object[] getArguments(String args) {

    if (arguments == null) {
      return null;
    }

    List argList = new ArrayList();
    StringTokenizer tokenizer = new StringTokenizer(args, ",");
    while (tokenizer.hasMoreTokens()) {
      argList.add((String) tokenizer.nextElement());
    }

    // First pass ... count actual available parameters; is there a value for each requested key ?
    //

    int j = 0;
    for (Iterator i = argList.iterator(); i.hasNext();) {
      String value = (String) arguments.get((String) i.next());
//      if (value != null) {
//        j += 1;
//      }
      j = (value == null ? j : (j += 1));
    }

    // Second pass ... assign values
    //

    Object[] argArray = new Object[j];
    j = 0;  // Reset
    for (Iterator i = argList.iterator(); i.hasNext();) {
      String value = (String) arguments.get((String) i.next());
      if (value != null) {
        argArray[j] = value;
        j++;
      }
    }

    arguments = null; // Reset this session ...

    return argArray;
  }

  /**
   * <p>Copied from the Netbeans API documentation : Called when a file is removed.
   *
   * @param event The event from CVS.
   */
  public void fileRemoved(FileRemovedEvent event) {}

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
  public void moduleExpanded(ModuleExpansionEvent event) {}

  /**
   * <p>Copied from the Netbeans API documentation : Called when a file has been added.
   *
   * @param event The event from CVS.
   */
  public void fileAdded(FileAddedEvent event) {}

  /**
   * <p>Copied from the Netbeans API documentation : Called when file information has been received.
   * <p/>
   * <p>This method constructs the <code>LogInformation</code> object that contains the log for a specific file as
   * a result of the <code>cvs log</code> command.
   *
   * @param event The event from CVS.
   */
  public void fileInfoGenerated(FileInfoEvent event) {
    logInformation = event.getInfoContainer();
  }

  /**
   * Gets the log that is the result of the <code>cvs log</code> command.
   *
   * @return A <code>LogInformation</code> that can be queried by classes for all information on a (set of) file(s).
   */
  public LogInformation getLogInformation() {
    return (LogInformation) logInformation;
  }

  /**
   * <p>Copied from the Netbeans API documentation : Called when server responses with "ok" or "error", (when the command finishes)
   *
   * @param event The event from CVS.
   */
  public void commandTerminated(TerminationEvent event) { }

  /**
   * <p>Copied from the Netbeans API documentation : Called when a file has been updated.
   *
   * @param event The event from CVS.
   */
  public void fileUpdated(FileUpdatedEvent event) { }

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
      //
      if (response != null) {
        //response.addMessage(new SuccessMessage("File has been added to the CVS repository."));
      } else {
        logger.debug("'SuccessMessage' not routed to CommandResponseHandler : " + event.getMessage());
      }

    } else if (message.startsWith("cvs server: Updating")) {

      // TODO Localize message
      //
      if (response != null) {
        response.addMessage(new SuccessMessage("Module has been updated."));
      } else {
        logger.debug("'SuccessMessage' not routed to CommandResponseHandler : " + event.getMessage());
      }
    } else if (message.startsWith("cvs server: cannot find module")) {

      throw new CVSRuntimeException(CVSException.NO_SUCH_MODULE_IN_REPOSITORY, getArguments("MODULE, REPOSITORY"));

    } else if (message.startsWith("cvs add:") && message.indexOf("already exists") >= 0) {

      throw new CVSRuntimeException(CVSException.FILE_EXISTS_IN_REPOSITORY, getArguments("FILE, MODULE, REPOSITORY"));

    } else if (message.startsWith("cvs") && message.indexOf("no such tag") >= 0) {

      throw new CVSRuntimeException(CVSException.VERSION_NOT_FOUND, getArguments("MODULE, VERSION"));

    } else if (message.indexOf("contains characters other than digits") >= 0) {

      throw new CVSRuntimeException(CVSException.INVALID_SYMBOLIC_NAME, getArguments("MODULE, SYMBOLIC_NAME"));

    } else if (message.indexOf("permission denied") >= 0) {

      // todo does this work ??
      // should be tested properly ...

      throw new CVSRuntimeException(CVSException.SECURITY_VIOLATION);

    }

    if (!"".equals(message)) {
      logger.debug("MessageEvent from CVS : " + message);
    }
  }
}