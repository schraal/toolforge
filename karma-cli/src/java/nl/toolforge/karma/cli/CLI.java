package nl.toolforge.cli;

//import nl.toolforge.karma.core.KarmaException;
import org.apache.commons.lang.StringUtils;

import java.util.Calendar;
import java.util.List;
import java.util.ArrayList;
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

		CommandContext ctx = new CommandContext();

		// The command context must call the init() method to ensure that


		try {
			while (true) {

				Calendar now = Calendar.getInstance();

				String prompt = "Karma"; // Can be delegated to a configuration file if need be.

				System.out.print(
					StringUtils.leftPad("" + now.get(Calendar.HOUR_OF_DAY) , 2, "0") + ":" +
					StringUtils.leftPad("" + now.get(Calendar.MINUTE) , 2, "0") + ":" +
					StringUtils.leftPad("" + now.get(Calendar.SECOND) , 2, "0") + PROMPT);

				String line = reader.readLine();

				if ((line == null) || ("".equals(line.trim()))) {
					continue;
				}



				if (quitCmd.contains(line.trim().toUpperCase())) {
					break;
				}

				try {
					ctx.execute(line);
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
