package nl.toolforge.karma.cli;

//import nl.toolforge.karma.core.KarmaException;
import nl.toolforge.karma.core.KarmaException;
import nl.toolforge.karma.core.cmd.CommandContext;
import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.prefs.Preferences;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;

/**
 * <p>The <code>CLI</code> is the command-line interface for Karma. The class presents a simple-to-use command-line
 * terminal, where developers can type in their commands.
 *
 *
 * @author D.A. Smedes
 */
public class CLI {

//	private static Preferences prefs = Preferences.getInstance();

	public static void main(String[] args) {

		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

		// Initialize the command context
		//
		CommandContext ctx = new CommandContext();
		try {
			ctx.init();
		} catch (KarmaException k) {
			//log.error(k.getMessage());
			k.printStackTrace();
			System.exit(1);
		}

		ConsoleWriter writer = new ConsoleWriter(true);

		try {

			while (true) {

				writer.prompt();

				String line = reader.readLine().trim();

				if ((line == null) || ("".equals(line.trim()))) {
					continue;
				}

				String commandName = null;
				String options = "";

				// Check if the user wants to exit
				//
				if (ConsoleConfiguration.getExitCommands().contains(line.trim().toUpperCase())) {

					String text = "Thank you for using this great product.";
					int length = text.length();

					StringBuffer g = new StringBuffer();
					g.append("\n\n").append(StringUtils.repeat("*", text.length()));
					g.append("\n").append(text).append("\n");
					g.append(StringUtils.repeat("*", text.length())).append("\n");

					writer.writeln(g.toString());

					break;
				}

				// Filter out the HELP command
				//
				if (line.trim().toLowerCase().startsWith("help") || line.trim().startsWith("?")) {

					writer.writeln("The following commands are valid:");

					for (Iterator i = ctx.getCommands().iterator(); i.hasNext();) {
						CommandDescriptor descriptor = (CommandDescriptor) i.next();
						writer.writeln(descriptor.getName());
					}

				} else {

					// Other commands ...
					//
					if (line.indexOf(" ") > 0) {
						commandName = line.substring(0, line.indexOf(" "));
					} else {
						commandName = line.substring(0);
					}

					// Check if the command is a valid command
					//
					if (ctx.isCommand(commandName)) {

						if (line.indexOf(" ") > 0) {
							options = line.substring(line.indexOf(" ") + 1);
						}

						try {
							ctx.execute(commandName, options);
						} catch (KarmaException a) {
							writer.writeln("Non-fatal error : ".concat(a.getErrorMessage()));
						}

					} else {
						writer.writeln(commandName.trim().concat(" is an invalid command"));
					}

				}
			}
			//log.info("Bye!");
			System.exit(0);
		}
		catch (IOException e) {
			//log.error(e.getMessage());
			System.exit(1);
		}
	}
}
