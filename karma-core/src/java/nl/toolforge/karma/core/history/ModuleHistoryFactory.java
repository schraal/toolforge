package nl.toolforge.karma.core.history;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.digester.Digester;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;

/**
 * @author W.H. Schraal
 */
public class ModuleHistoryFactory {

    private static final Log logger = LogFactory.getLog(ModuleHistoryFactory.class);
    private File projectRoot = null;

    private static ModuleHistoryFactory instance = null;

    private ModuleHistoryFactory(File projectRoot) {
        this.projectRoot = projectRoot;
    }

    public static ModuleHistoryFactory getInstance(File projectRoot) {
        if (instance == null) {
            logger.info("ModuleHistoryFactory not initialized yet. Making new instance with projectRoot: "+projectRoot);
            instance = new ModuleHistoryFactory(projectRoot);
        }
        return instance;
    }

    public ModuleHistory getModuleHistory(String moduleName) {
        try {
            File history = new File(projectRoot, moduleName+File.separator+"history.xml");
            System.out.println("loading history from: "+history);

            Digester digester = new Digester();
            digester.setValidating(false);
            digester.addObjectCreate("history", "nl.toolforge.karma.core.history.ModuleHistory");
            digester.addObjectCreate("history/event", "nl.toolforge.karma.core.history.Event");
            digester.addSetProperties("history/event");
            digester.addObjectCreate("history/event/datetime", "java.util.Date");
            digester.addSetProperties("history/event/datetime");
            digester.addFactoryCreate("history/event/version", "nl.toolforge.karma.core.VersionCreationFactory");
            digester.addSetNext("history/event/version", "setVersion", "nl.toolforge.karma.core.Version");
            digester.addSetNext("history/event/datetime", "setDatetime", "java.util.Date");
            digester.addSetNext("history/event", "addEvent", "nl.toolforge.karma.core.history.Event");

            ModuleHistory moduleHistory = (ModuleHistory) digester.parse(history);
            System.out.println(moduleHistory);
            return moduleHistory;

        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (SAXException se) {
            se.printStackTrace();
        }
        return null;
    }

}
