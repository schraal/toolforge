/*
Karma CLI - Command Line Interface for the Karma application
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
package nl.toolforge.karma.cli;

import java.util.ArrayList;
import java.util.List;

/**
 * Class managing the CLI history of commands that have been run. The CLI is capable of fetching arrow-up and
 * arrow-down commands
 *
 * @author D.A. Smedes
 * @version $Id$
 */

// todo maybe abstract to karma-core; usable for aura as well ?
public final class HistoryManager {

  private static final int MAX_HISTORY_ITEMS = 100;

  private int maxHistoryItems = 1;
  private List historyItems = null;
  private int currentItem = -1;

  public HistoryManager(int maxHistoryItems) {
    this.maxHistoryItems = (maxHistoryItems < 1 ? MAX_HISTORY_ITEMS : maxHistoryItems);
    historyItems = new ArrayList(this.maxHistoryItems);
  }

  /**
   * Adds a command line string to the history. The current history
   *
   * @param commandLine The string that is fetched from <code>stdin</code>.
   */
  public synchronized void addHistoryItem(String commandLine) {

    if (historyItems.size() == maxHistoryItems) {
      historyItems.remove(historyItems.get(0)); // Fifo ...
    }
    historyItems.add(commandLine);
    if (currentItem == -1) {
      currentItem = 0;
    } else {
      currentItem++;
    }
//    currentItem = (currentItem == -1 ? 0 : currentItem++); // Initialize pointer when -1, or leave intact.
  }

  /**
   * Returns the current history item that is been pointed at. The history maintains a pointer to the last selected
   * item, and returns that one.
   *
   * @return A command line item as a string or an empty string when no history items were available.
   */
  public String getCurrentHistoryItem() {

//    currentItem = (currentItem == 0 ? currentItem : --currentItem);

    if (currentItem != -1) {
      return (String) historyItems.get(currentItem--);
    } else {
      return "";
    }
  }
}
