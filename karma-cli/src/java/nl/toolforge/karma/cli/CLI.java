package nl.toolforge.karma.cli;

//import nl.toolforge.karma.core.KarmaException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.PosixParser;

import java.util.Calendar;
import java.util.List;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import nl.toolforge.karma.core.cmd.CommandContext;
import nl.toolforge.karma.core.KarmaException;

/**
 * <p>The <code>CLI</code> is the command-line interface for Karma. The class presents a simple-to-use command-line
 * terminal, where developers can type in their commands.
 *
 *
 * @author D.A. Smedes
 */
public class CLI {

	public static final String PROMPT = "[ Karma ] > ";

	public static List quitCmd = new ArrayList();

	static {

		quitCmd.add("QUIT");
		quitCmd.add("BYE");
		quitCmd.add("EXIT");
	}

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

		try {

			while (true) {

				Calendar now = Calendar.getInstance();

				System.out.print(
					StringUtils.leftPad("" + now.get(Calendar.HOUR_OF_DAY) , 2, "0") + ":" +
					StringUtils.leftPad("" + now.get(Calendar.MINUTE) , 2, "0") + ":" +
					StringUtils.leftPad("" + now.get(Calendar.SECOND) , 2, "0") + PROMPT);

				String line = reader.readLine().trim();

				if ((line == null) || ("".equals(line.trim()))) {
					continue;
				}

				// Step : we need to load a manifest. We assume that the last time we used a manifest,
				// it was written as a property to ${user.home}/${karma.home}
				//
				//

				// Step : we need to have a CommandContext, which is our focal point when executing commands
				//

				try {

                    // Parse the command line
					//

					// The first word (until a space is reached)

					System.out.println(">> " + line);

					String commandName = null;
					String options = "";


					if (line.indexOf(" ") > 0) {
						commandName = line.substring(0, line.indexOf(" "));
					} else {
						commandName = line.substring(0);
					}

					if (ctx.isCommand(commandName)) {

						if (line.indexOf(" ") > 0) {
							options = line.substring(line.indexOf(" ") + 1);
						}

						System.out.println(
							">> DEBUG : context will execute command '" + commandName +
							"' with options '" + options);

					} else {
						System.out.println(">> Invalid command ...");
					}

					// Check if the command is a valid command
					//

					if (quitCmd.contains(line.trim().toUpperCase())) {
						break;
					}

					ctx.execute(commandName, options);
				}
				catch (KarmaException k) {
					k.printStackTrace();
					//log.error(k.getMessage());
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
