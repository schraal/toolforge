package nl.toolforge.karma.cli;

import nl.toolforge.karma.core.prefs.Preferences;
import nl.toolforge.karma.core.prefs.UnavailableValueException;
import nl.toolforge.karma.core.KarmaException;
import org.apache.commons.lang.StringUtils;

/**
 * Writer to <code>System.out</code>. This class provides a helper to write output to the console.
 *
 * @author D.A. Smedes
 *
 * @version $Id$
 */
public final class ConsoleWriter {

  private String prompt = null;

  /**
   * Creates a <code>ConsoleWriter</code> to be able to write command output to <code>System.out</code>. A
   * <code>boolean</code> value can be provided to determine whether to use the defaultPrompt or
   *
   * @param defaultPrompt <code>true</code> When the default prompt should be used
   *                      ({@link ConsoleConfiguration#getDefaultPrompt}), or <code>false</code> when the user defined
   *                      prompt ({@link ConsoleConfiguration#getPrompt}) should be used.
   */
  public ConsoleWriter(boolean defaultPrompt) {

    // TODO : logger.info("Output will be written to the console.")

    if (!defaultPrompt) {
      prompt = Preferences.getInstance().get("", ConsoleConfiguration.getDefaultPrompt());
    } else {
      prompt = ConsoleConfiguration.getDefaultPrompt();
    }
    prompt = prompt.concat(" "); // Remove too many spaces, and add one for readability.
  }

  /**
   * Writes the prompt to the console.
   */
  public void prompt() {
    System.out.print(prompt);
  }

  /**
   * Writes <code>text</code> to <code>System.out</code>, preceded with {@link ConsoleConfiguration#getPrompt}.
   *
   * @param text Text to write to the console.
   */
  public void write(String text) {
    System.out.print(prompt.concat(text));
  }

  /**
   * Writes <code>text</code> to <code>System.out</code>, preceded with {@link ConsoleConfiguration#getPrompt} and
   * followed by a newline.
   *
   * @param text Text to write to the console.
   */
  public void writeln(String text) {
    System.out.println(prompt.concat(text));
  }

  public void writeln(boolean writePrompt, String text) {
    if (writePrompt) {
      writeln(text);
    } else {
      System.out.println(StringUtils.repeat(" ", prompt.length()).concat(text));
    }
  }

  public void blankLine() {
    writeln(false, "");
  }
}