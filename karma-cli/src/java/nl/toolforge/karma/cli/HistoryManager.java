package nl.toolforge.karma.cli;

import java.util.List;
import java.util.ArrayList;

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
