package nl.toolforge.karma.cli;

import java.text.MessageFormat;
import java.util.prefs.Preferences;

import nl.toolforge.karma.cli.cmd.CLICommandResponseHandler;
import nl.toolforge.karma.core.boot.WorkingContext;
import nl.toolforge.karma.core.cmd.Command;
import nl.toolforge.karma.core.cmd.CommandContext;
import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.cmd.CommandFactory;
import nl.toolforge.karma.core.cmd.CommandLoadException;
import nl.toolforge.karma.core.location.LocationException;
import nl.toolforge.karma.core.manifest.ManifestException;

/**
 * The command-line-interface for Karma. This class runs one command, then quits (gracefully hopefully). All arguments
 * are considered a command-line that will be interpreted by the <code>CommandContext</code>.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public final class CLI {

  public static void main(String[] args) {

    try {
      if (args.length == 0) {

        System.out.println("\nCommand-line is missing ...\n\n");
        System.out.println("CLI usage :\n\n");

        System.out.println("karma <command-line-string>              Runs the command that is passed as a command-line-string");

        System.exit(1);
      }

      CLI cli = new CLI();

      try {
        cli.runCommand(args);
      } catch (CommandException e) {

        // todo the dame ugly way ...
        //
        String message;
        if (e.getMessageArguments() != null && e.getMessageArguments().length != 0) {
          MessageFormat messageFormat = new MessageFormat(e.getErrorMessage());
          message = messageFormat.format(e.getMessageArguments());
        } else {
          message = e.getErrorMessage();
        }
        System.out.println("\n" + message);

        System.out.println("\n\n\n\n");

        e.printStackTrace();

        System.exit(1);
      }

      System.exit(0);
    } catch (Exception e) {
      System.out.println("-- catch all ---\n");
      e.printStackTrace();  
    }
  }

  private void runCommand(String[] arguments) throws CommandException {

    String commandLine = "";

    for (int i = 0; i < arguments.length; i++) {
      commandLine += arguments[i] + " ";
    }

    // todo WorkingContext should be initializing the logging system. Some other way.

    String workingContextName = Preferences.userRoot().get(WorkingContext.WORKING_CONTEXT, "default");
    WorkingContext workingContext = new WorkingContext(workingContextName);

    System.out.println("Checking command ...");

    Command command = null;
    try {
      command = CommandFactory.getInstance().getCommand(commandLine);
    } catch (CommandLoadException e) {
      throw new CommandException(e.getErrorCode(),  e.getMessageArguments());
    }

    System.out.println("Command `" + command.getName() + "` ok !");
    System.out.println("Working context : " + workingContextName);

    CommandContext commandContext = new CommandContext(workingContext);
    try {
      commandContext.init(new CLICommandResponseHandler());
    } catch (LocationException e) {
      String message;
      if (e.getMessageArguments() != null && e.getMessageArguments().length != 0) {
        MessageFormat messageFormat = new MessageFormat(e.getErrorMessage());
        message = messageFormat.format(e.getMessageArguments());
      } else {
        message = e.getErrorMessage();
      }
      System.out.println("\n" + message);
    } catch (ManifestException e) {
      String message;
      if (e.getMessageArguments() != null && e.getMessageArguments().length != 0) {
        MessageFormat messageFormat = new MessageFormat(e.getErrorMessage());
        message = messageFormat.format(e.getMessageArguments());
      } else {
        message = e.getErrorMessage();
      }
      System.out.println("\n" + message);
    }

    commandContext.execute(commandLine);
  }
}
