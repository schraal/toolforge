package nl.toolforge.karma.core.history;

import nl.toolforge.karma.core.Version;

/**
 * Holds the information of one module history event.
 *
 * @author W.H. Schraal
 */
public class Event {
  String type;
  Version version = null;
  Datetime datetime = null;
  String comment;

  public Event() {
  }

  public void setType(String type) {
    this.type = type;
  }

  public void setVersion(Version version) {
    this.version = version;
  }

  public void setDatetime(Datetime datetime) {
    this.datetime = datetime;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public String toString() {
    String s = "[";

    s = s + type;
    s = s + ", " + version;
    s = s + ", " + datetime;
    s = s + ", " + comment;
    s = s + "]";
    
    return s;
  }

}
