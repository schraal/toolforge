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

        System.out.println("\n" + "[ karma ] " + e.getMessage());

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

    boolean updateStores = false;

    int index = 0;
    
    if (arguments.length > 0 && arguments[0].equals("-u")) {
      updateStores = true;
      index=1;
    }
    for (int i = index; i < arguments.length; i++) {
      commandLine += arguments[i] + " ";
    }

    // todo WorkingContext should be initializing the logging system. Some other way.

    WorkingContext workingContext =
        new WorkingContext(Preferences.userRoot().get(WorkingContext.WORKING_CONTEXT_PREFERENCE, WorkingContext.DEFAULT));

    System.out.println("[ karma ] Checking command ...");

    Command command = null;
    try {
      command = CommandFactory.getInstance().getCommand(commandLine);
    } catch (CommandLoadException e) {
      throw new CommandException(e.getErrorCode(),  e.getMessageArguments());
    }

    System.out.println("[ karma ] Command `" + command.getName() + "` ok !");
    System.out.println("[ karma ] Working context : " + workingContext.getName());

    CommandContext commandContext = new CommandContext(workingContext);
    try {
      commandContext.init(new CLICommandResponseHandler(), updateStores);
    } catch (LocationException e) {
      System.out.println("\n" + "[ karma ] " + e.getMessage());
    } catch (ManifestException e) {
      System.out.println("\n" + "[ karma ] " + e.getMessage());
    }

    commandContext.execute(commandLine);
  }
}
