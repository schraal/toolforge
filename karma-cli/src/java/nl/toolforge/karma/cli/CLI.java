package nl.toolforge.karma.cli;

import nl.toolforge.karma.core.KarmaException;
import nl.toolforge.karma.core.Manifest;
import nl.toolforge.karma.core.LocalEnvironment;
import nl.toolforge.karma.core.prefs.Preferences;
import nl.toolforge.karma.core.bundle.BundleCache;
import nl.toolforge.karma.core.cmd.CommandContext;
import nl.toolforge.karma.core.cmd.CommandFactory;
import nl.toolforge.karma.core.cmd.CommandMessage;
import nl.toolforge.karma.core.cmd.CommandResponse;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ResourceBundle;

/**
 * <p>The <code>CLI</code> is the command-line interface for Karma. The class presents a simple-to-use command-line
 * terminal, where developers can type in their commands.
 *
 *
 * @author D.A. Smedes
 *
 * @version $Id$
 */
public class CLI {

	private static final ResourceBundle FRONTEND_MESSAGES =
		BundleCache.getInstance().getBundle(BundleCache.FRONTEND_MESSAGES_KEY);

	private static Log logger = LogFactory.getLog(CLI.class);


	/**
	 * Startup class for the command line interface.
	 *
	 * @param args As per the contract; we don't use it.
	 */
	public static void main(String[] args) {

		LocalEnvironment env = new LocalEnvironment();

		ConsoleWriter writer = new ConsoleWriter(true);

		// Initialize the command context
		//
		CommandContext ctx = new CommandContext();
		try {
			ctx.init(env);

			Manifest currentManifest = ctx.getCurrent();
			if (currentManifest != null) {
				ConsoleConfiguration.setManifest(currentManifest);

				writer.writeln(FRONTEND_MESSAGES.getString("message.MANIFEST_RESTORED"));
			}

		} catch (KarmaException k) {

			writer.writeln(k.getErrorMessage());

			logger.error(k.getMessage());

			writer.writeln(FRONTEND_MESSAGES.getString("message.EXIT"));
			System.exit(1);
		}

		try {

			// Open a reader, which is the actual command line ...
			//
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

			while (true) {

				writer.prompt();

				String line = reader.readLine().trim();

				if ((line == null) || ("".equals(line.trim()))) {
					continue;
				}


				// Check if the user wants to exit
				//
				if (ConsoleConfiguration.getExitCommands().contains(line.trim().toUpperCase())) {

					String text = FRONTEND_MESSAGES.getString("message.THANK_YOU");
					int length = text.length();

					StringBuffer g = new StringBuffer();
					g.append("\n\n").append(StringUtils.repeat("*", length));
					g.append("\n").append(text).append("\n");
					g.append(StringUtils.repeat("*", length)).append("\n");

					writer.writeln(g.toString());

					break;
				}

				try {

					// Filter out the HELP command
					//
					if (line.trim().toLowerCase().startsWith("help") || line.trim().startsWith("?")) {

						writer.writeln("\n\n" + FRONTEND_MESSAGES.getString("message.VALID_COMMANDS"));
//						writer.blankLine();

						CommandRenderer renderer = new CommandRenderer();
						StringBuffer renderedBuffer = renderer.renderedCommands(CommandFactory.getInstance().getCommands());

						writer.writeln(false, renderedBuffer.toString());

					} else {

						CommandResponse response = ctx.execute(line);

						// For now, just print the response.
						//
						CommandMessage[] messages = response.getMessages();

						if (messages.length > 0) {
							// Print the first message for now.
							// TODO do something better with the message array
							//
							writer.writeln(messages[0].getMessageText());
						}
					}
				} catch (KarmaException e) {
					writer.writeln(e.getErrorMessage());
				}
			}
			writer.writeln(FRONTEND_MESSAGES.getString("message.EXIT"));
			logger.info("Exiting Karma ...");

			// Flush the preferences.
			//
			env.flushPreferences();

			System.exit(0);
		}
		catch (IOException e) {
			logger.error(e.getMessage(), e);
			System.exit(1);
		}
	}

	/**
	 * Extension to be able to access the protected <code>renderOptions</code>-method.
	 */
	private class CLIHelpFormatter extends HelpFormatter {

		public StringBuffer renderOptions(StringBuffer buffer, int width, Options options, int leftPad, int descPad) {
			return super.renderOptions(buffer, width, options, leftPad, descPad);
		}
	}
}
