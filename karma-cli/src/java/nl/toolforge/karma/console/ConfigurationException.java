package nl.toolforge.karma.console;

public class ConfigurationException extends Exception {
  public ConfigurationException(String message, Throwable cause) {
    super(message, cause);
  }

  public ConfigurationException(String message) {
    super(message);
  }
}
