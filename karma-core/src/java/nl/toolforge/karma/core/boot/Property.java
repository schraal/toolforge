package nl.toolforge.karma.core.boot;

/**
 * Class representation of a <code>&lt;property&gt</code>-element in the configuration file for a
 * {@link WorkingContext}.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public class Property {

  private String name = null;
  private String value = null;

  public Property() {
    // Empty
  }

  public Property(String name, String value) {
    setName(name);
    setValue(value);
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }
}
