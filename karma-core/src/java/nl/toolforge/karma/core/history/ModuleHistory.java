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
package nl.toolforge.karma.core.history;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * This class holds the history information of a module. This information is stored per module in a file
 * called <b>history.xml</b>. The <code>ModuleHistory</code> has to be retrieved through the
 * {@link ModuleHistoryFactory}.
 *
 * @see ModuleHistoryEvent
 * @author W.H. Schraal
 */
public class ModuleHistory {
  private static final Log logger = LogFactory.getLog(ModuleHistoryFactory.class);

  public final static String MODULE_HISTORY_FILE_NAME = "history.xml";

  private List events = new ArrayList();
  private File historyLocation = null;

  public ModuleHistory() {
  }

  public void addEvent(ModuleHistoryEvent moduleHistoryEvent) {
    events.add(moduleHistoryEvent);
  }

  /**
   * Gets all history events. The method returns a <code>List</code> of <code>ModuleHistoryEvent</code>s.
   *
   * @return
   *   A <code>List</code> of <code>ModuleHistoryEvent</code>s or an empty <code>List</code>, when no events are
   *   available.
   */
  public List getEvents() {
    return events;
  }

  public void setHistoryLocation(File historyLocation) {
    this.historyLocation = historyLocation;
  }

  public File getHistoryLocation() {
    return this.historyLocation;
  }

  public void save() throws ModuleHistoryException {
    if (events != null && historyLocation != null) {
      logger.info("Saving module history");

      String fileContents = "";
      for (Iterator it = events.iterator(); it.hasNext(); ) {
        fileContents += ((ModuleHistoryEvent) it.next()).toXml();
      }
      fileContents = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<history>\n" + fileContents;
      fileContents += "</history>";

      logger.debug(fileContents);
      Writer writer = null;
      try {
        if (!historyLocation.exists()) {
          historyLocation.createNewFile();
        }
        writer = new BufferedWriter(new FileWriter(historyLocation));
        writer.write(fileContents);

        logger.info("Saved module history");
      } catch (IOException ioe) {
        //todo: throw new exception here
        logger.error("Something went wrong when saving module history", ioe);
      } finally {
        if (writer != null) {
          try {
            writer.close();
          } catch (Exception e) {
            //too bad.
          }
        }
      }
    } else {
      if ( events == null ) {
        logger.warn("Save called on empty history. Skipping...");
      } else {
        logger.warn("Save called on history without a location. Skipping...");
        throw new ModuleHistoryException(ModuleHistoryException.HISTORY_FILE_LOCATION_NOT_DEFINED);
      }
    }
  }

  public String toString() {
    String s = "location: "+historyLocation+"\n";
    Iterator it = events.iterator();
    while (it.hasNext()) {
      s = s + ((ModuleHistoryEvent) it.next()).toString();
    }
    return s;
  }

}
