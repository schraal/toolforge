package nl.toolforge.karma.cli;

import nl.toolforge.karma.core.boot.WorkingContext;
import nl.toolforge.karma.core.cmd.CommandContext;
import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.location.LocationException;
import nl.toolforge.karma.core.manifest.ManifestException;
import nl.toolforge.karma.cli.cmd.CLICommandResponseHandler;

import java.util.prefs.Preferences;
import java.util.Collections;
import java.util.Arrays;
import java.lang.reflect.Array;

/**
 * The command-line-interface for Karma. This class runs one command, then quits (gracefully hopefully). All arguments
 * are considered a command-line that will be interpreted by the <code>CommandContext</code>.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public final class CLI {

  public static void main(String[] args) {

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
      e.printStackTrace();
      System.exit(1);
    } catch (NoWorkingContextException e) {
      e.printStackTrace();
      System.exit(1);
    }

    System.exit(0);
  }

  private void runCommand(String[] arguments) throws NoWorkingContextException, CommandException {

    String commandLine = "";

    for (int i = 0; i < arguments.length; i++) {
      commandLine += arguments[i] + " ";
    }

    String workingContextName = Preferences.userRoot().get(WorkingContext.WORKING_CONTEXT, "default");

    System.out.println("Working context : " + workingContextName);

    WorkingContext workingContext = new WorkingContext(workingContextName);

    CommandContext commandContext = new CommandContext(workingContext);
    try {
      commandContext.init(new CLICommandResponseHandler());
    } catch (LocationException e) {
      System.out.println(e.getErrorMessage());
    } catch (ManifestException e) {
      System.out.println(e.getErrorMessage());
    }

    commandContext.execute(commandLine);
  }
}
