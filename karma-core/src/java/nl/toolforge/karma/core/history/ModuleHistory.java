package nl.toolforge.karma.core.history;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.File;
import java.io.IOException;

import org.apache.commons.digester.Digester;
import org.xml.sax.SAXException;


/**
 * This class holds the history information of a module.
 *
 * @author W.H. Schraal
 */
public class ModuleHistory {
  private List events = new ArrayList();

  public ModuleHistory() {
  }

  public void addEvent(Event event) {
    events.add(event);
  }

  public String toString() {
    String s = "";
    Iterator it = events.iterator();
    while (it.hasNext()) {
      s = s + ((Event) it.next()).toString();
    }
    return s;
  }

  public static void main(String[] args) {
    Digester digester = new Digester();
    digester.setValidating(false);
    digester.addObjectCreate("history", "nl.toolforge.karma.core.history.ModuleHistory");
    digester.addObjectCreate("history/event", "nl.toolforge.karma.core.history.Event");
    digester.addSetProperties("history/event");
    digester.addObjectCreate("history/event/datetime", "nl.toolforge.karma.core.history.Datetime");
    digester.addSetProperties("history/event/datetime");
    digester.addFactoryCreate("history/event/version", "nl.toolforge.karma.core.VersionCreationFactory");
    digester.addSetNext("history/event/version", "setVersion", "nl.toolforge.karma.core.Version");
    digester.addSetNext("history/event/datetime", "setDatetime", "nl.toolforge.karma.core.history.Datetime");
    digester.addSetNext("history/event", "addEvent", "nl.toolforge.karma.core.history.Event");
    try {
        ModuleHistory moduleHistory = (ModuleHistory) digester.parse(new File("history.xml"));
        System.out.println(moduleHistory);
    } catch (IOException ioe) {
      ioe.printStackTrace();
    } catch (SAXException se) {
      se.printStackTrace();
    }
  }

}
