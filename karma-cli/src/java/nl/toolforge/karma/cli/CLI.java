package nl.toolforge.karma.cli;

import nl.toolforge.karma.core.KarmaException;
import nl.toolforge.karma.core.bundle.BundleCache;
import nl.toolforge.karma.core.cmd.CommandContext;
import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.CommandMessage;
import nl.toolforge.karma.core.cmd.CommandFactory;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Iterator;
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

  private static final ResourceBundle FRONTEND_MESSAGES = BundleCache.FRONTEND_MESSAGES;

  private static Log logger = LogFactory.getLog(CLI.class);


  /**
   * Startup class for the command line interface.
   *
   * @param args As per the contract; we don't use it.
   */
  public static void main(String[] args) {

    CLI cli = new CLI();


    ConsoleWriter writer = new ConsoleWriter(true);

    // Initialize the command context
    //
    CommandContext ctx = new CommandContext();
    try {
      ctx.init();
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

            writer.writeln(FRONTEND_MESSAGES.getString("message.VALID_COMMANDS"));

            String[] commandStrings = cli.formatCommands(CommandFactory.getInstance().getCommands());
            for (int i = 0; i < commandStrings.length; i++) {
              writer.writeln(commandStrings[i]);
            }

          } else {

            CommandResponse response = ctx.execute(line);

            // For now, just print the response.
            //
            CommandMessage[] messages = response.getMessages();

            // Print the first message for now.
            // TODO do something better with the message array
            //
            writer.writeln(messages[0].getMessageText());

          }
        } catch (KarmaException e) {
          writer.writeln(e.getErrorMessage());
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

  private String[] formatCommands(Collection commands) {

    String[] commandStrings = new String[commands.size()];

    int j = 0;

    for (Iterator i = commands.iterator(); i.hasNext();) {
      CommandDescriptor descriptor = (CommandDescriptor) i.next();

      int count1 = 25 - (descriptor.getName().length() + descriptor.getAlias().length() + 2);
//      logger.debug(">> count : " + count1);

      commandStrings[j++] =
        descriptor.getName() + "(" + descriptor.getAlias() + ")" +
        StringUtils.repeat(" ", count1) + descriptor.getDescription();
//			logger.debug(">> String: " + commandStrings[j-1]);
    }

    return commandStrings;

  }
}
