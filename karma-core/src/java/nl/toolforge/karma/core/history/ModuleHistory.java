package nl.toolforge.karma.core.history;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


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

    public void save() {
        if (events != null) {
            String file = "";
            for (Iterator it = events.iterator(); it.hasNext(); ) {
                file += ((Event) it.next()).toXml();
            }
            file = "<history>\n" + file;
            file += "</history>";

            System.out.println(file);
        } else {
           System.out.println("fuck off");
        }
    }

    public String toString() {
        String s = "";
        Iterator it = events.iterator();
        while (it.hasNext()) {
            s = s + ((Event) it.next()).toString();
        }
        return s;
    }

}
