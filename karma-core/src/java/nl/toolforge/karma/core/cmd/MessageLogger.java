package nl.toolforge.karma.core.cmd;

import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.util.StringUtils;

import java.io.BufferedReader;
import java.io.StringReader;
import java.io.IOException;

/**
 * @author D.A. Smedes
 * @version $Id$
 */
public class MessageLogger extends DefaultLogger {

  public String commandAlias = "***";

  public MessageLogger() {
    super();
  }

  public MessageLogger(Command command) {

    super();
    commandAlias = command.getName();
  }

  public void messageLogged(BuildEvent event) {

    int priority = event.getPriority();

    // Filter out messages based on priority

    if (priority <= msgOutputLevel) {

      StringBuffer message = new StringBuffer();

      if (event.getTask() != null && !emacsMode) {
        // Print out the name of the task if we're in one
        String name = commandAlias;
        String label = "[" + name + "] ";
        int size = LEFT_COLUMN_SIZE - label.length();
        StringBuffer tmp = new StringBuffer();
        for (int i = 0; i < size; i++) {
          tmp.append(" ");
        }
        tmp.append(label);
        label = tmp.toString();

        try {
          BufferedReader r =
              new BufferedReader(
                  new StringReader(event.getMessage()));
          String line = r.readLine();
          boolean first = true;
          while (line != null) {
            if (!first) {
              message.append(StringUtils.LINE_SEP);
            }
            first = false;
            message.append(label).append(line);
            line = r.readLine();
          }
        } catch (IOException e) {
          // shouldn't be possible
          message.append(label).append(event.getMessage());
        }
      } else {
        message.append(event.getMessage());
      }

      String msg = message.toString();
      if (priority != Project.MSG_ERR) {
        printMessage(msg, out, priority);
      } else {
        printMessage(msg, err, priority);
      }
      log(msg);
    }
  }
}
