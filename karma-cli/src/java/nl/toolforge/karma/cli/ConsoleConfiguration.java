package nl.toolforge.karma.cli;

import nl.toolforge.karma.core.prefs.Preferences;
import org.apache.commons.lang.StringUtils;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

/**
 * <p>This class defines stuff for a the console (<code>stdout</code>) to which the command line interface
 * writes its command output.
 *
 * @author D.A. Smedes
 *
 * @version $Id$
 */
public final class ConsoleConfiguration {

 	public static Set exitCommands = new HashSet();

	static {

		exitCommands.add("QUIT");
		exitCommands.add("BYE");
		exitCommands.add("EXIT");
	}

	private static Preferences prefs = Preferences.getInstance();

	/**
	 * <p>Returns the prompt which is defined by the <code>console.prompt</code> property. The following syntax is
	 * supported : <font color="red">to be implemented.</font>.
	 *
	 * @return
	 */
	public static String getPrompt() {

		String prompt = null;

		// TODO : logger.debug("getPrompt() returns getDefaultPrompt(); requires implementation");
		prompt = getDefaultPrompt();

		return prompt;
	}

	/**
	 * Gets the default prompt, constructed as follows : <code>HH:MM:SS [ Karma ]</code>
	 * @return
	 */
	public static String getDefaultPrompt() {

		Calendar now = Calendar.getInstance();

		return
			StringUtils.leftPad("" + now.get(Calendar.HOUR_OF_DAY) , 2, "0") + ":" +
			StringUtils.leftPad("" + now.get(Calendar.MINUTE) , 2, "0") + ":" +
			StringUtils.leftPad("" + now.get(Calendar.SECOND) , 2, "0") + " [ Karma ] > ";
	}

	/**
	 * Gets a list of exit commands (<code>Strings</code>) that can be used to exit the command line interface.
	 *
	 * @return A set of exit commands as strings.
	 */
	public static Set getExitCommands() {
		return exitCommands;
	}

}