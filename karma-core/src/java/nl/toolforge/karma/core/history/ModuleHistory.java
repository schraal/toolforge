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
 * called <b>history.xml</b>. The ModuleHistory has to be retrieved through the {@link ModuleHistoryFactory}.
 *
 * @see ModuleHistoryEvent
 * @author W.H. Schraal
 */
public class ModuleHistory {
    private static final Log logger = LogFactory.getLog(ModuleHistoryFactory.class);

    private List events = new ArrayList();
    private File historyLocation = null;

    public ModuleHistory() {
    }

    public void addEvent(ModuleHistoryEvent moduleHistoryEvent) {
        events.add(moduleHistoryEvent);
    }

    public void setHistoryLocation(File historyLocation) {
        this.historyLocation = historyLocation;
    }

    public void save() {
        if (events != null) {
            String fileContents = "";
            for (Iterator it = events.iterator(); it.hasNext(); ) {
                fileContents += ((ModuleHistoryEvent) it.next()).toXml();
            }
            fileContents = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<history>\n" + fileContents;
            fileContents += "</history>";

            logger.debug(fileContents);
            try {
                Writer writer = new BufferedWriter(new FileWriter(historyLocation));
                writer.write(fileContents);
                writer.flush();
            } catch (IOException ioe) {
                //todo: throw new exception here
                ioe.printStackTrace();
            }
        } else {
            //todo: give decent error message
            System.out.println("fuck off");
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
