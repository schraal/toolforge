/*
Karma core - Core of the Karma application
Copyright (C) 2004  Toolforge <www.toolforge.nl>

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/
package nl.toolforge.karma.core.cmd.util;

import nl.toolforge.karma.core.cmd.Command;
import nl.toolforge.karma.core.cmd.event.MessageEvent;
import nl.toolforge.karma.core.cmd.event.SimpleMessage;
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
