package nl.toolforge.karma.core.cmd.util;

import nl.toolforge.karma.core.cmd.Command;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.event.MessageEvent;
import nl.toolforge.karma.core.cmd.event.SimpleMessage;
import org.apache.commons.lang.StringUtils;
import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.DefaultLogger;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author D.A. Smedes
 * @version $Id$
 */
public class AntLogger extends DefaultLogger {

  public static final int LEFT_COLUMN_SIZE = 16;

  // Specific messages we want to capture
  //
  private static Set messagePrefixes = null;

  // Specific tasks we want to capture
  //
  private static Set taskNames = null;

  private Command command = null;

  static {

    messagePrefixes = new HashSet();

    // "<javac> Compiling ..."
    //
    messagePrefixes.add("Compiling");

    taskNames = new HashSet();
    taskNames.add("javadoc"); // <javadoc>
  }

  public AntLogger(Command command) {
    this.command = command;
  }

  private boolean map(BuildEvent event) {

    if (taskNames.contains(event.getTask().getTaskName())) {
      return true;
    }

    for (Iterator i = messagePrefixes.iterator(); i.hasNext();) {
      if (event.getMessage().startsWith((String) i.next())) {
        return true;
      }
    }
    return false;
  }

  /**
   * Filters out very specific ant event messages and prints them. Very specific. Not all of the stuff that you
   * normally get.
   *
   * @param event
   */
  public void messageLogged(BuildEvent event) {

    int priority = event.getPriority();

    if (priority <= msgOutputLevel) {
      if (map(event)) {
        command.getCommandResponse().addEvent(new MessageEvent(command, new SimpleMessage(event.getMessage())));
      }
    }
  }

  /**
   * Overridden version.
   *
   * @param event
   */
  public void targetStarted(BuildEvent event) {}

}
