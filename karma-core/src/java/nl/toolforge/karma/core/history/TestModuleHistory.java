package nl.toolforge.karma.core.history;

import junit.framework.TestCase;
import org.apache.commons.digester.Digester;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;

/**
 * @author W.H. Schraal
 */
public class TestModuleHistory extends TestCase {

    public void testReadModuleHistory() {
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
        try {
            ModuleHistory moduleHistory = (ModuleHistory) digester.parse(new File("resources/test/history.xml"));
            System.out.println(moduleHistory);
            assertTrue(true);
        } catch (IOException ioe) {
            ioe.printStackTrace();
            fail(ioe.getMessage());
        } catch (SAXException se) {
            se.printStackTrace();
            fail(se.getMessage());
        }
    }

}
