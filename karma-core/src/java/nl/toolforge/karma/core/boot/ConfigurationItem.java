package nl.toolforge.karma.core.boot;

/**
 * @author D.A. Smedes
 * @version $Id$
 */
public class ConfigurationItem {

  private String property = null;
  private String label = null;
  private String defaultValue = null;
  private boolean scrambled = false;

  public ConfigurationItem(String property, String label, String defaultValue) {
    this(property, label, defaultValue, false);
  }

  public ConfigurationItem(String property, String label, String defaultValue, boolean scrambled) {
    this.property = property;
    this.label = label;
    this.defaultValue = defaultValue;
    this.scrambled = scrambled;
  }

  public String getProperty() {
    return property;
  }

  public String getLabel() {
    return label;
  }

  public String getDefaultValue() {
    return defaultValue;
  }

  public boolean isScrambled() {
    return scrambled;
  }
}
