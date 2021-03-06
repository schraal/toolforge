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

import nl.toolforge.karma.core.module.Module;
import org.apache.commons.digester.Digester;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;

/**
 * Factory for {@link ModuleHistory} objects. The factory is initialized with the manifest home, which is the
 * fysical location where the modules of the current manifest are located. Then, for each module the module
 * history can be retrieved.
 *
 * @author W.H. Schraal
 */
public class ModuleHistoryFactory {

  private static final Log logger = LogFactory.getLog(ModuleHistoryFactory.class);
  private static File manifestHome = null;

  private static ModuleHistoryFactory instance = null;

  private ModuleHistoryFactory(File manifestHome) {
    ModuleHistoryFactory.manifestHome = manifestHome;
  }

  /**
   * Retrieve the one and only instance of this class, given the specified manifest home.
   *
   * @param manifestHome  The physical location of the local copies of the current manifest's module
   * @return ModuleHistoryFactory
   */
  public static ModuleHistoryFactory getInstance(File manifestHome) {
    if ( (instance == null) || !ModuleHistoryFactory.manifestHome.equals(manifestHome)) {
      logger.info("ModuleHistoryFactory not initialized yet. Making new instance with manifestHome: "+manifestHome);
      instance = new ModuleHistoryFactory(manifestHome);
    }
    return instance;
  }

  /**
   * Retrieve the module history (from file) of the given module. Returns a new
   * module history when it did not yet exist.
   *
   * @param module  The module for which to retrieve the module history.
   * @return <code>ModuleHistory</code> of the given module .
   */
  public ModuleHistory getModuleHistory(Module module) throws ModuleHistoryException {
    ModuleHistory moduleHistory = null;

    try {
      File historyLocation = new File(module.getBaseDir(), ModuleHistory.MODULE_HISTORY_FILE_NAME);
      logger.info("loading history from: " + historyLocation);


      if (historyLocation.exists()) {
        Digester digester = new Digester();
        digester.setValidating(false);   //todo: dit moet true worden
        digester.addObjectCreate("history", "nl.toolforge.karma.core.history.ModuleHistory");
        digester.addObjectCreate("history/event", "nl.toolforge.karma.core.history.ModuleHistoryEvent");
        digester.addSetProperties("history/event");
        digester.addObjectCreate("history/event/datetime", "java.util.Date");
        digester.addSetProperties("history/event/datetime");
        digester.addFactoryCreate("history/event/version", "nl.toolforge.karma.core.VersionCreationFactory");
        digester.addSetNext("history/event/version", "setVersion", "nl.toolforge.karma.core.Version");
        digester.addSetNext("history/event/datetime", "setDatetime", "java.util.Date");
        digester.addSetNext("history/event", "addEvent", "nl.toolforge.karma.core.history.ModuleHistoryEvent");

        moduleHistory = (ModuleHistory) digester.parse(historyLocation);
        logger.debug(moduleHistory);
      } else {
        moduleHistory = new ModuleHistory();
        logger.info("Module history did not exist yet. Created new one.");
      }
      moduleHistory.setHistoryLocation(historyLocation);

    } catch (IOException ioe) {
      logger.error(ioe);
      throw new ModuleHistoryException(ModuleHistoryException.HISTORY_FILE_DOES_NOT_EXIST, new Object[]{module.getName()});
    } catch (SAXException se) {
      logger.error(se);
      throw new ModuleHistoryException(ModuleHistoryException.INVALID_HISTORY_FILE, new Object[]{module.getName()});
    }
    return moduleHistory;
  }

}
