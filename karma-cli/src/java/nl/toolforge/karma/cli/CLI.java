package nl.toolforge.karma.cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ResourceBundle;
import java.text.MessageFormat;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import nl.toolforge.karma.cli.cmd.CLICommandResponseHandler;
import nl.toolforge.karma.core.KarmaException;
import nl.toolforge.karma.core.LocalEnvironment;
import nl.toolforge.karma.core.location.LocationException;
import nl.toolforge.karma.core.manifest.Manifest;
import nl.toolforge.karma.core.manifest.ManifestException;
import nl.toolforge.karma.core.bundle.BundleCache;
import nl.toolforge.karma.core.cmd.CommandContext;
import nl.toolforge.karma.core.cmd.CommandFactory;
import nl.toolforge.karma.core.cmd.CommandException;

/**
 * <p>The <code>CLI</code> is the command-line interface for Karma. The class presents a simple-to-use command-line
 * terminal, where developers can type in their commands.
 *
 * @author D.A. Smedes
 *
 * @version $Id$
 */
public class CLI {

	private static final ResourceBundle FRONTEND_MESSAGES =
		BundleCache.getInstance().getBundle(BundleCache.FRONTEND_MESSAGES_KEY);

	private static Log logger = LogFactory.getLog(CLI.class);

  private static String lastLine = "";

	/**
	 * Startup class for the command line interface.
	 *
	 * @param args As per the contract; we don't use it.
	 */
	public static void main(String[] args) {

		ConsoleWriter writer = new ConsoleWriter(true);

    // Initialize the command context
    //
    CommandContext ctx = null;
		try {
      LocalEnvironment env = LocalEnvironment.getInstance();

      ctx = new CommandContext();
			ctx.init(env, new CLICommandResponseHandler(writer));

			Manifest currentManifest = ctx.getCurrent();
			if (currentManifest != null) {
				ConsoleConfiguration.setManifest(currentManifest);

				writer.writeln(new MessageFormat(FRONTEND_MESSAGES.getString("message.MANIFEST_RESTORED")).format(new Object[]{currentManifest.getName()}));
      }

		} catch (KarmaException k) {

			writer.writeln(k.getErrorMessage());

			logger.error(k.getMessage(), k);

			writer.writeln(FRONTEND_MESSAGES.getString("message.EXIT"));

      System.exit(1);

    } catch (LocationException e) {
      writer.writeln(e.getErrorMessage());
    }

		try {

			// Open a reader, which is the actual command line ...
			//
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

			while (true) {

				writer.prompt();

				String line = reader.readLine().trim();

        //System.out.println("Keystroke = " + line);

				if ((line == null) || ("".equals(line.trim()))) {
					continue;
				}

        if ("[A".equals(line)) {
          line = lastLine;
          writer.writeln(line);
        } else {
          lastLine = line;
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

						CommandRenderer renderer = new CommandRenderer();
						StringBuffer renderedBuffer = renderer.renderedCommands(CommandFactory.getInstance().getCommands());

						writer.writeln(renderedBuffer.toString());

					} else {
						ctx.execute(line);
					}
				} catch (CommandException e) {
					writer.writeln(e.getErrorMessage());
          logger.error(e.getMessage(), e);
				}
			}
			writer.writeln(FRONTEND_MESSAGES.getString("message.EXIT"));
			logger.info("Exiting Karma ...");

			System.exit(0);
		}
		catch (IOException e) {
			logger.error(e.getMessage(), e);
			System.exit(1);
		}
	}
}
