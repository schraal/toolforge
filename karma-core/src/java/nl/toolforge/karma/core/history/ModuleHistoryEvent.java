package nl.toolforge.karma.core.history;

import nl.toolforge.karma.core.Version;

import java.util.Date;

/**
 * Holds the information of one module history event. A module history event is logged when something changes in the
 * configuration of the module, like the creation or promotion of the module.
 * The module history events are stored in the {@link ModuleHistory}.
 *
 * @author W.H. Schraal
 */
public class ModuleHistoryEvent {

    /** Module history event type for creating a module */
    public final static String CREATE_MODULE_EVENT = "Create module";

    /** Module history event type for promoting a module */
    public final static String PROMOTE_MODULE_EVENT = "Promote module";

    private String type = "";
    private Version version = null;
    private Date datetime = null;
    private String author = "";
    private String comment = "";

    public ModuleHistoryEvent() {
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String toXml() {
        String s = "\t<event";

        s += " type=\"" + type + "\"";
        s += " author=\"" + author + "\"";
        s += " comment=\"" + comment + "\"/>\n";
        s += "\t\t<version value=\"" + version + "\"/>\n";
        s += "\t\t<datetime time=\"" + datetime.getTime() + "\"/>\n";
        s += "\t</event>\n";

        return s;
    }

    public String toString() {
        String s = "[";

        s = s + type;
        s = s + ", " + version;
        if (datetime != null) {
            s = s + ", " + datetime.getTime();
        } else {
            s = s + ", " + datetime;
        }
        s = s + ", " + author;
        s = s + ", " + comment;
        s = s + "]";

        return s;
    }

}
