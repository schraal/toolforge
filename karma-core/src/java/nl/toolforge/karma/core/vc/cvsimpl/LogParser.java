package nl.toolforge.karma.core.vc.cvsimpl;

import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.DefaultLogger;
import org.netbeans.lib.cvsclient.command.log.LogInformation;
import org.netbeans.lib.cvsclient.event.CVSListener;
import org.netbeans.lib.cvsclient.event.FileAddedEvent;
import org.netbeans.lib.cvsclient.event.FileInfoEvent;
import org.netbeans.lib.cvsclient.event.FileRemovedEvent;
import org.netbeans.lib.cvsclient.event.FileUpdatedEvent;
import org.netbeans.lib.cvsclient.event.ModuleExpansionEvent;
import org.netbeans.lib.cvsclient.event.TerminationEvent;

//
// todo MUST comply to Sun Public License !
//

/**
 * @author D.A. Smedes
 * @version $Id$
 */
public class LogParser extends DefaultLogger implements CVSListener {

  private static final String SYM_NAME = "symbolic names:";
  private static final String KEYWORD_SUBST = "keyword substitution: ";

  private LogInformation logInfo = new LogInformation();

  private boolean addingSymNames;

  /**
   * Filters symbolic names from a cvs log. The rest of the messages are ignored.
   *
   * @param event
   */
  public void messageLogged(BuildEvent event) {

    int priority = event.getPriority();

    if (priority <= msgOutputLevel) {

      String line = event.getMessage();

      if (addingSymNames) {
        processSymbolicName(line);
      } else if (line.startsWith(SYM_NAME)) {
        addingSymNames = true;
      } else if (line.startsWith(KEYWORD_SUBST)) {
        addingSymNames = false;
      }
    }
    System.out.println(event.getMessage());
  }

  private void processSymbolicName(String line) {
    if (!line.startsWith(KEYWORD_SUBST)) {
      line = line.trim();
      int index = line.indexOf(':');
      if (index > 0) {
        String symName = line.substring(0, index).trim();
        String revName = line.substring(index + 1, line.length()).trim();
        logInfo.addSymbolicName(symName.intern(), revName.intern());
      }
    }
  }
  
  public LogInformation getLogInformation() {
    return logInfo;
  }

  // Empty implementations of CVSListener interface
  //

  /**
   * Not implemented for this implementation.
   * @param e Not implemented for this implementation.
   */
  public void messageSent(org.netbeans.lib.cvsclient.event.MessageEvent e) { }

  /**
   * Not implemented for this implementation.
   * @param e Not implemented for this implementation.
   */
  public void fileAdded(FileAddedEvent e) { }

  /**
   * Not implemented for this implementation.
   * @param e Not implemented for this implementation.
   */
  public void fileRemoved(FileRemovedEvent e) { }

  /**
   * Not implemented for this implementation.
   * @param e Not implemented for this implementation.
   */
  public void fileUpdated(FileUpdatedEvent e) { }

  /**
   * Not implemented for this implementation.
   * @param e Not implemented for this implementation.
   */
  public void fileInfoGenerated(FileInfoEvent e) { }

  /**
   * Not implemented for this implementation.
   * @param e Not implemented for this implementation.
   */
  public void commandTerminated(TerminationEvent e) { }

  /**
   * Not implemented for this implementation.
   * @param e Not implemented for this implementation.
   */
  public void moduleExpanded(ModuleExpansionEvent e) { }
}
