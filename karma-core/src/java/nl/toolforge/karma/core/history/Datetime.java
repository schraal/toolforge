package nl.toolforge.karma.core.history;

/**
 * Represents the datetime in a module's history.
 *
 * @author W.H. Schraal
 */
public class Datetime {

  String datetime;

  public Datetime() {
  }

  public void setValue(String datetime) {
    this.datetime = datetime;
  }

  public String toString() {
    return datetime;
  }
  
}
